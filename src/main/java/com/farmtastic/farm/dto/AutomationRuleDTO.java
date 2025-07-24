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

    private int ruleId;
    private String ruleName;
    private int sensorId;
    private String conditionOp;
    private BigDecimal threshold;
    private int actuatorId;
    private String command;
    private Boolean active;

    public static AutomationRuleDTO fromEntity(AutomationRule rule) {
        return AutomationRuleDTO.builder()
                .ruleId(rule.getRuleId().intValue())
                .ruleName(rule.getRuleName())
                .sensorId(rule.getSensor().getDeviceId().intValue())
                .threshold(rule.getThresholdValue())
                .actuatorId(rule.getActuator().getDeviceId().intValue())
                .active(rule.getIsActive())
                .build();
    }


}
