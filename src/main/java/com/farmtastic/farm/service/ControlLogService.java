package com.farmtastic.farm.service;

import com.farmtastic.farm.config.MqttCommandPublisher;
import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.domain.ControlLog;
import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.enums.ControlSource;
import com.farmtastic.farm.event.ControlLogCreatedEvent;
import com.farmtastic.farm.repository.AutomationRuleRepository;
import com.farmtastic.farm.repository.ControlLogRepository;
import java.util.List;
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
        List<AutomationRule> activeRules = automationRuleRepository.findBySensorAndIsActiveTrue(sensorDevice);

        // 2. 검색된 규칙 목록의 유무 확인 (null이 아니면서 비어있지 않은지)
        if (activeRules != null && !activeRules.isEmpty()) {

            // 3. 조회된 모든 규칙에 대해 반복 실행
            for (AutomationRule rule : activeRules) {
                log.info("검사 규칙: '{}', 임계값: {}, 액추에이터: {}", rule.getRuleName(), rule.getThresholdValue(), rule.getActuator().getDeviceName());

                // 4. 센서 측정값과 규칙의 임계값 비교
                boolean shouldAct = compare(rule.getConditionOp(), value, rule.getThresholdValue());

                // 조건이 참일 경우
                if (shouldAct) {
                    log.info("조건 충족: 센서값={}, 연산자='{}', 임계값={}",
                        value, rule.getConditionOp(), rule.getThresholdValue());

                    Device actuator = rule.getActuator();
                    String command = rule.getActionCommand();
                    log.info("동작 대상 액추에이터: {}, 명령어: {}", actuator.getDeviceName(), command);

                    String modelType = sensorDevice.getModelType().name();

                    // 5. 자동제어 발행 (LIGHT, PH 등)
                    if ("LIGHT".equals(modelType) || "PH".equals(modelType)) {
                        // MQTT로 명령어 전달
                        mqttCommandPublisher.sendCommand(actuator, command);

                        log.info("자동제어 MQTT 명령어 발행: sensor={}, value={}, 조건: '{} {}', -> actuator={}, command={}",
                            sensorDevice.getDeviceName(), value,
                            rule.getConditionOp(), rule.getThresholdValue(),
                            actuator.getDeviceName(), rule.getActionCommand()
                        );
                    }

                    // 6. 제어 로그를 DB에 저장
                    ControlLog controlLog = new ControlLog();
                    controlLog.setCommand(command);
                    controlLog.setSource(ControlSource.AUTOMATION_RULE); // 자동제어 규칙에 의한 실행
                    controlLog.setDevice(actuator);
                    controlLog.setLogTime(LocalDateTime.now());
                    controlLogRepository.save(controlLog);
                }
            } // 반복문 종료

            // 7. 물 수위 센서에 대한 별도 로직 처리 (규칙과 별개로 값 자체로 알림)
            // 이 로직은 규칙 조건과 관계없이 특정 센서 타입에 대해 항상 실행될 수 있으므로 루프 밖에 위치합니다.
            String modelType = sensorDevice.getModelType().name();
            if ("WATER_LEVEL_BOTTOM".equals(modelType)) {
                // value가 1일 때 (물이 부족할 때) 알림
                if (value.compareTo(BigDecimal.ONE) == 0) {
                    //  notificationService.handleControlLogCreated("물을 채워주세요");
                    log.info("물을 채워주세요 알림 전송 로직 실행");
                }
            }

        } else {
            log.warn("활성화된 자동제어 규칙이 존재하지 않음: sensor={}", sensorDevice.getDeviceName());
        }
    }

    private boolean compare(String op,BigDecimal sensorValue, BigDecimal threshold){
        //연산자 문자열 op에 따라 비교해서 true/false 결과를 반환
        //op==">"라면 sensorValue > threshold일 때 true
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
