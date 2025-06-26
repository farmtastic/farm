package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.domain.Device;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutomationRuleDTO {

    private String ruleName;
    private int sensorId;
    private String conditionOp;
    private BigDecimal threshold;
    private int actuatorId;
    private String command;
    private Boolean active;

    public static AutomationRuleDTO fromEntity(AutomationRule rule) {
        return AutomationRuleDTO.builder()
                .ruleName(rule.getRuleName())
                .sensorId(rule.getSensor().getDeviceId().intValue())
                .conditionOp(rule.getConditionOp())
                .threshold(rule.getThresholdValue())
                .actuatorId(rule.getActuator().getDeviceId().intValue())
                .command(rule.getActionCommand())
                .active(rule.getIsActive())
                .build();
    }

    public void update(String ruleName, String conditionOp, BigDecimal thresholdValue,
                       String actionCommand, Device sensor, Device actuator, Boolean isActive) {
        this.ruleName = ruleName;
        this.conditionOp = conditionOp;
        this.threshold = thresholdValue;
        this.command = actionCommand;
        this.sensorId = sensor.getDeviceId().intValue();
        this.actuatorId = actuator.getDeviceId().intValue();
        this.active = isActive;
    }

}
