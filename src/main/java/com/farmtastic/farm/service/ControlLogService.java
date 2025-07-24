package com.farmtastic.farm.service;

import com.farmtastic.farm.config.MqttCommandPublisher;
import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.domain.ControlLog;
import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.enums.ControlSource;
import com.farmtastic.farm.event.ControlLogCreatedEvent;
import com.farmtastic.farm.repository.AutomationRuleRepository;
import com.farmtastic.farm.repository.ControlLogRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ControlLogService {

    private final ControlLogRepository controlLogRepository;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행을 위한 객체
    private final AutomationRuleRepository automationRuleRepository;
    private final MqttCommandPublisher mqttCommandPublisher;
    private final NotificationService notificationService;

    @Transactional
    public void createLog(ControlLog controlLog) {
        // 1. 제어 이력을 먼저 저장
        controlLogRepository.save(controlLog);

        // 2. 저장이 완료되면 이벤트를 발행
        eventPublisher.publishEvent(new ControlLogCreatedEvent(controlLog));
    }


    public void evaluateSensor(Device sensorDevice, BigDecimal value) {
        log.info(" 규칙과 측정값 비교");

        // 1. 해당 센서를 기준으로 활성화된 자동제어 규칙 '목록' 조회
        List<AutomationRule> activeRules = automationRuleRepository.findBySensorAndIsActiveTrue(
            sensorDevice);

        String modelType = sensorDevice.getModelType().name();

        // 2. 검색된 규칙 목록의 유무 확인 (null이 아니면서 비어있지 않은지)
        if (activeRules != null && !activeRules.isEmpty()) {
            Set<Device> commandedActuators = new HashSet<>();

            if ("WATER_LEVEL_TOP".equals(modelType) || "WATER_LEVEL_BOTTOM".equals(modelType)) {

            } else if ("LIGHT".equals(modelType) || "PH".equals(modelType)) {
                for (AutomationRule rule : activeRules) {
                    BigDecimal threshold = rule.getThresholdValue();
                    Device actuator = rule.getActuator();

                    if (commandedActuators.contains(actuator)) {
                        continue;

                    }

                    log.info("검사 규칙: '{}', 임계값: {}, 액추에이터: {}", rule.getRuleName(),
                        rule.getThresholdValue(), rule.getActuator().getDeviceName());

                    // 조건: 센서값 > 임계값
                    if (value.compareTo(threshold) > 0) {
                        String command = modelType + "_OFF";

                        mqttCommandPublisher.sendCommand(actuator, command);

                        log.info(
                            "자동제어 발행: sensor={}, value={}, threshold={}, actuator={}, command={}",
                            sensorDevice.getDeviceName(), value, threshold,
                            actuator.getDeviceName(), command);

                        // 제어 로그 저장
                        ControlLog logEntity = new ControlLog();
                        logEntity.setDevice(actuator);
                        logEntity.setCommand(command);
                        logEntity.setLogTime(LocalDateTime.now());
                        logEntity.setSource(ControlSource.AUTOMATION_RULE);
                        logEntity.setReason(
                            String.format("센서 '%s'의 값이 %.2f로 임계값  %.2f을 초과하여 자동 제어 실행",
                                sensorDevice.getDeviceName(), value, threshold));
                        this.createLog(logEntity);

                        // 명령을 보냈으므로, 이 액추에이터를 기록 (추가된 부분 3)
                        commandedActuators.add(actuator);

                    } else {
                        String command = modelType + "_ON";

                        mqttCommandPublisher.sendCommand(actuator, command);

                        log.info(
                            "자동제어 발행: sensor={}, value={}, threshold={}, actuator={}",
                            sensorDevice.getDeviceName(), value, threshold, actuator);

                        // 6. 제어 로그를 DB에 저장
                        ControlLog controlLog = new ControlLog();
                        controlLog.setCommand(command);
                        controlLog.setSource(ControlSource.AUTOMATION_RULE); // 자동제어 규칙에 의한 실행
                        controlLog.setDevice(actuator);
                        controlLog.setLogTime(LocalDateTime.now());
                        controlLog.setReason(
                            String.format("센서 '%s'의 값이 %.2f로 임계값  %.2f을 초과하여 자동 제어 실행",
                                sensorDevice.getDeviceName(), value, threshold));
                        this.createLog(controlLog);

                        // 명령을 보냈으므로, 이 액추에이터를 기록 (추가된 부분 3)
                        commandedActuators.add(actuator);
                    }

                }
            } // 반복문 종료
        } else {
            log.info("활성화된 자동제어 규칙이 존재하지 않음: sensor={}", sensorDevice.getDeviceName());
        }
    }

}
