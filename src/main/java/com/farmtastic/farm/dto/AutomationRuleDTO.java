package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.AutomationRule;
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

}
