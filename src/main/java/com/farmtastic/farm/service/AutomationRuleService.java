package com.farmtastic.farm.service;

import com.farmtastic.farm.config.MqttCommandPublisher;
import com.farmtastic.farm.config.MqttGateway;
import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.domain.enums.DeviceType;
import com.farmtastic.farm.domain.enums.ModelType;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AutomationRuleService {

    private final AutomationRuleRepository automationRuleRepository;
    private final DeviceRepository deviceRepository;
    private final MqttCommandPublisher mqttCommandPublisher;


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

    public void evaluateSensor(Device sensorDevice, BigDecimal value) {
        log.info(" 규칙과 측정값 비교");
        //1. 해당 센서를 기준으로 활성화된 자동제어 규칙 조회 (ph, 조도, 수위 검색)
        AutomationRule selectRule = automationRuleRepository.findBySensorAndIsActiveTrue(sensorDevice);


        if (selectRule != null && compare(selectRule.getConditionOp(), value, selectRule.getThresholdValue())){
            if (compare(selectRule.getConditionOp(), value, selectRule.getThresholdValue())){
                mqttCommandPublisher.sendCommand(selectRule.getActuator(), selectRule.getActionCommand());

                log.info("자동제어 실행: sensor={}, value={}, 조건: '{} {}', -> actuator={}, command={}",
                        sensorDevice.getDeviceName(), value,
                        selectRule.getConditionOp(), selectRule.getThresholdValue(),
                        selectRule.getActuator().getDeviceName(), selectRule.getActionCommand());
            }
        }

    }

    private boolean compare(String op,BigDecimal sensorValue, BigDecimal threshold){

        //센서에 넘어온 실시간 측정값 sensorValue와 DB에 저장된 임계값 threshold 비교
        return switch (op){
            case ">" -> sensorValue.compareTo(threshold) > 0;
            case "<" -> sensorValue.compareTo(threshold) < 0;
            case ">=" -> sensorValue.compareTo(threshold) >= 0;
            case "<=" -> sensorValue.compareTo(threshold) <= 0;
            case "==" -> sensorValue.compareTo(threshold) == 0;
            default -> false;
        };
    }
}
