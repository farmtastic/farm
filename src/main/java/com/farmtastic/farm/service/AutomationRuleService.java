package com.farmtastic.farm.service;

import com.farmtastic.farm.config.MqttGateway;
import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.domain.enums.DeviceType;
import com.farmtastic.farm.dto.AutomationRuleDTO;
import com.farmtastic.farm.dto.LogReceiveDto;
import com.farmtastic.farm.repository.DeviceRepository;
import com.farmtastic.farm.repository.AutomationRuleRepository;
import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.repository.SensorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AutomationRuleService {

    private final AutomationRuleRepository automationRuleRepository;
    private final DeviceRepository deviceRepository;

    private final SensorLogRepository sensorLogRepository;
    private final MqttGateway mqttGateway;


    //insert
    @Transactional
    public AutomationRuleDTO createRule(AutomationRuleDTO dto) {
        log.info("createRule");

        Device sensor = deviceRepository.findById((long) dto.getSensorId())
            .filter(d -> d.getDType() == DeviceType.SENSOR)
            .orElseThrow(() -> new IllegalArgumentException("센서 없음"));

        Device actuator = deviceRepository.findById((long) dto.getActuatorId())
            .filter(d -> d.getDType() == DeviceType.ACTUATOR)
            .orElseThrow(() -> new IllegalArgumentException("엑추에이터 없음"));

        AutomationRule rule = AutomationRule.builder()
            .ruleName(dto.getRuleName())
            .conditionOp(dto.getConditionOp())
            .thresholdValue(dto.getThreshold())
            .actionCommand(dto.getCommand())
            .isActive(dto.getActive())
            .sensor(sensor)
            .actuator(actuator)
            .build();

        AutomationRule saved = automationRuleRepository.save(rule);

        return AutomationRuleDTO.fromEntity(saved);
    }


    //update
    @Transactional
    public AutomationRuleDTO updateRule(AutomationRuleDTO dto, Long id) {
        log.info("updateRule");

        AutomationRule rule = automationRuleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("규칙 없음"));

        Device sensor = deviceRepository.findById((long) dto.getSensorId())
            .filter(d -> d.getDType() == DeviceType.SENSOR)
            .orElseThrow(() -> new IllegalArgumentException("센서 없음"));

        Device actuator = deviceRepository.findById((long) dto.getActuatorId())
            .filter(d -> d.getDType() == DeviceType.ACTUATOR)
            .orElseThrow(() -> new IllegalArgumentException("엑추에이터 없음"));

        rule.update(
            dto.getRuleName(),
            dto.getConditionOp(),
            dto.getThreshold(),
            dto.getCommand(),
            sensor,
            actuator,
            dto.getActive()
        );

        AutomationRule updated = automationRuleRepository.save(rule);

        return AutomationRuleDTO.fromEntity(updated);
    }


    //view
    public List<AutomationRule> getAllRule() {
        log.info("getAllRule");
        return automationRuleRepository.findAll();
    }


    //delete
    @Transactional
    public String deleteRuleById(Long id) {

        AutomationRule rule = automationRuleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 id가 존재하는 규칙이 없습니다"));

        automationRuleRepository.delete(rule);
        return "임계점 삭제 완료";
    }

    //mqtt에서 호출해줌
    @Transactional(readOnly = true)
    public void checkAndExcuteRules(LogReceiveDto logDto, String topic) {
        Long sensorId = logDto.getDeviceId();
        double currentValue = logDto.getValue().doubleValue();
        log.info("sensorId:{}", sensorId);
        log.info("currentValue:{}", currentValue);

        //1. 현재 센서에 연결된 모든 활성화된 자동화 규칙 조회
        List<AutomationRule> rules = automationRuleRepository.findBySensorDeviceIdAndIsActive(sensorId,
            true);

        log.info("rules:{}", rules);

        String sensorType = determineSensorType(topic);
        log.info("sensorType:{}", sensorType);

        //1-1. 적용할 규칙이 없다면 종료
        if (rules.isEmpty()) {
            return;
        }

        log.info("");
        log.info("");
        log.info("Device Id{}에 대해 {}개의 규칙 검사", sensorId, rules.size());
        log.info("");
        log.info("");

        //2. 각 규칙의 조건 확인
        for (AutomationRule rule : rules) {
            boolean conditionMet = false;

            //현재 값과 지정된 임계정 비교(내장된 함수를 이용함)
            if (rule.getConditionOp().equals("GREATER_THAN")) {
                conditionMet = currentValue > rule.getThresholdValue().intValue();
            } else if (rule.getConditionOp().equals("LESS_THAN")) {
                conditionMet = currentValue < rule.getThresholdValue().intValue();
            } else if (rule.getConditionOp().equals("EQUAL")) {
                conditionMet = currentValue == rule.getThresholdValue().doubleValue();
            }

            //3. 조건이 충족되면 제어 명령 MQTT로 발행
            if (conditionMet) {
                Device actuator = rule.getActuator();
                String command = rule.getActionCommand();

                //제어 명령의 topic -> 동적으로 셍성
                String controlTopic = buildControlTopic(topic, actuator.getDeviceId());

                //MQTT 메세지 전송
                mqttGateway.sendToMqtt(controlTopic, command);

                log.info("규칙 '{}' 충족 Topic:{}, Command:{} 메세지 발행", rule.getRuleName(), controlTopic,
                    command);

                //4. 현재 센서값이 임계값을 초과하면 엑츄에이터 on 상태로 유지합니다.
                checkControlActuator(rule, currentValue, controlTopic);
            }
        }

    }


    //임계값을 초과하면 지정된 임계값에서 -1까지 엑츄에이터 ON 상태로 유지
    private void checkControlActuator(AutomationRule rule, double currentValue,
        String controlTopic) {
        double thresholdValue = rule.getThresholdValue().doubleValue();
        double lowerLimit = thresholdValue - 1;

        //센서값이 임계값을 초과하고 현재 값이 -1 이하오 내려가기 전까지는 on 상태 유지
        if (currentValue > thresholdValue) {
            while (currentValue > lowerLimit) {
                String command = "ON";
                mqttGateway.sendToMqtt(controlTopic, command);
                log.info("");

                //일정 시간(5분) 간격으로 값을 확인
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //값을 다시 읽어오기 -> 최신 값
                currentValue = getSensorValue(rule.getSensor().getDeviceId());

            }
        } else {
            String command = "OFF";
            mqttGateway.sendToMqtt(controlTopic, command);
            log.info("현재 임계값보다 이하로 측정되어 OFF 상태로 전환");
        }

    }

    private double getSensorValue(Long deviceId) {

        Optional<SensorLog> sensorLogOptional = sensorLogRepository.findByDeviceDeviceId(deviceId);

        if (sensorLogOptional.isPresent()) {
            SensorLog sensor = sensorLogOptional.get();
            return sensor.getLogValue().doubleValue();
        } else {
            log.warn("Device ID{}에 해당하는 센서를 찾을 수 없습니다.");
            return -1;
        }

    }

    //수신된 토픽에 맞는 센서 타입을 결정하는 메소드
    private String determineSensorType(String topic) {
        if (topic.contains("temperature")) {
            return "TEMPERATURE";
        } else if (topic.contains("ph")) {
            return "PH";
        } else if (topic.contains("waterlevel")) {
            return "WATER_LEVEL";
        }
        return "unknown";
    }

    private String buildControlTopic(String receivedTopic, Long actuatorId) {
        String[] parts = receivedTopic.split("/");
        if (parts.length >= 2) {
            String farmId = parts[1]; //farm id 추출
            return String.format("farm/%s/actuator/%d/control", farmId, actuatorId);
        }
        return String.format("farm/unknown/actuator/%d/control", actuatorId);
    }

}
