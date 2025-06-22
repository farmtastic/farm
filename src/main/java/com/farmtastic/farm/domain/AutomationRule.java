package com.farmtastic.farm.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "automation_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "condition_op", nullable = false, length = 10)
    private String conditionOp; // 예: ">", "<", "=="

    @Column(name = "threshold_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdValue;

    @Column(name = "action_command", nullable = false, length = 500)
    private String actionCommand; // 예: "ON", "OFF"

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 규칙의 조건이 되는 센서 (AutomationRules(N) -> Device(1))
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Device sensor;

    // 규칙의 제어 대상이 되는 액추에이터 (AutomationRules(N) -> Device(1))
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actuator_id", nullable = false)
    private Device actuator;
}