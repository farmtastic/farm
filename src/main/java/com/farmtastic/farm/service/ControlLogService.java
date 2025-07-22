package com.farmtastic.farm.service;

import com.farmtastic.farm.config.MqttCommandPublisher;
import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.domain.ControlLog;
import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.enums.ControlSource;
import com.farmtastic.farm.event.ControlLogCreatedEvent;
import com.farmtastic.farm.repository.AutomationRuleRepository;
import com.farmtastic.farm.repository.ControlLogRepository;
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

        //1. 해당 센서를 기준으로 활성화된 자동제어 규칙 조회 (ph, 조도, 수위 검색)
        AutomationRule selectRule = automationRuleRepository.findBySensorAndIsActiveTrue(sensorDevice);

        //2. 검색된 규칙의 유무 확인
        if (selectRule != null) {
            log.info("센서로 검색된 규칙이름:{}, 임계값:{}, actoutor:{}", selectRule.getRuleName(), selectRule.getThresholdValue(), selectRule.getActuator().getDeviceName() );

            String modelType = sensorDevice.getModelType().name();
            ControlSource source = ControlSource.AUTOMATION_RULE;
            log.info("modelType:{}, source:{}", modelType, source);

            Device actoutor = selectRule.getActuator();
            String command = selectRule.getActionCommand();
            log.info("actoutor:{}, command:{}", actoutor.getDeviceName(), command);

            //3. 센서에 넘어온 실시간 측정값 sensorValue와 DB에 저장된 임계값 threshold 비교
            boolean shouldAct = compare(selectRule.getConditionOp(), value, selectRule.getThresholdValue());

            if (shouldAct) {
                log.info("조건 확인: 센서값={}, 연산자='{}', 임계값={}, 결과={}",
                        value, selectRule.getConditionOp(), selectRule.getThresholdValue(),
                        compare(selectRule.getConditionOp(), value, selectRule.getThresholdValue()));
                //3-1. 자동제어 발행 대상일 경우 (조도 or 수위)
                if ("LIGHT".equals(modelType) || "PH".equals(modelType)) {
                    //mqtt로 전달
                    mqttCommandPublisher.sendCommand(actoutor, command);

                    log.info("자동제어 mqtt 명령어 발행: sensor={}, value={}, 조건: '{} {}', -> actuator={}, command={}",
                            sensorDevice.getDeviceName(), value,
                            selectRule.getConditionOp(), selectRule.getThresholdValue(),
                            selectRule.getActuator().getDeviceName(), selectRule.getActionCommand()
                    );
                }

                //로그 DB에 저장
                ControlLog log = new ControlLog();
                log.setCommand(command);
                log.setSource(source);
                log.setDevice(actoutor);
                log.setLogTime(LocalDateTime.now());
                controlLogRepository.save(log);
            }
            //modelType이 물일 때 DB에 저장되는 source 변경
            switch (modelType) {
                case "WATER_LEVEL" -> {
                    source = ControlSource.MANUAL_OVERRIDE;
                    log.info("modelType:{}, source:{}", modelType, source);
                    //물이 0 or 1일 때 각각의 알림 전달(알림 로직 추가)
                    if (value.compareTo(BigDecimal.ZERO) == 0) {
                        //  notificationService.handleControlLogCreated("물을 채워주세요");
                        log.info("물을 채워주세요 알림 전송완료");
                    } else if (BigDecimal.ONE.compareTo(value)==0) {
                        //  notificationService.handleControlLogCreated("물을 빼주세요");
                        log.info("물을 빼주세요 알림 전송완료");
                    }
                }
            }

        } else {
            log.warn("자동제어 규칙이 존재하지 않음: sensor={}", sensorDevice.getDeviceName());
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
