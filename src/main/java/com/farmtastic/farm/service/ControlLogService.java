package com.farmtastic.farm.service;

import com.farmtastic.farm.config.MqttCommandPublisher;
import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.domain.ControlLog;
import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.event.ControlLogCreatedEvent;
import com.farmtastic.farm.repository.AutomationRuleRepository;
import com.farmtastic.farm.repository.ControlLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ControlLogService {

    private final ControlLogRepository controlLogRepository;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행을 위한 객체
    private final AutomationRuleRepository automationRuleRepository;
    private final MqttCommandPublisher mqttCommandPublisher;

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
