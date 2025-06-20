package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.dto.AutomationRuleDTO;
import com.farmtastic.farm.repository.DeviceRepository;
import com.farmtastic.farm.repository.RuleRepository;
import com.farmtastic.farm.domain.AutomationRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutomationRuleService {

    private final RuleRepository ruleRepository;
    private final DeviceRepository deviceRepository;

    //insert
    @Transactional
    public AutomationRuleDTO createRule(AutomationRuleDTO dto) {
        log.info("createRule");

        Device sensor = deviceRepository.findById((long)dto.getSensorId())
                .orElseThrow(()-> new IllegalArgumentException("센서 없음"));
        Device actuator = deviceRepository.findById((long) dto.getActuatorId())
                .orElseThrow(()-> new IllegalArgumentException("엑추에이터 없음"));

        AutomationRule rule = AutomationRule.builder()
                .ruleName(dto.getRuleName())
                .conditionOp(dto.getConditionOp())
                .thresholdValue(dto.getThreshold())
                .actionCommand(dto.getCommand())
                .isActive(dto.getActive())
                .sensor(sensor)
                .actuator(actuator)
                .build();

        ruleRepository.save(rule);

        return dto;
    }


    //update
    public AutomationRuleDTO updateRule(AutomationRuleDTO dto, Long id) {
        log.info("updateRule");

        AutomationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("규칙 없음"));

        Device sensor = deviceRepository.findById((long) dto.getSensorId())
                .orElseThrow(() -> new IllegalArgumentException("센서 없음"));

        Device actuator = deviceRepository.findById((long) dto.getActuatorId())
                .orElseThrow(() -> new IllegalArgumentException("액추에이터 없음"));

        rule.setRuleName(dto.getRuleName());
        rule.setThresholdValue(dto.getThreshold());
        rule.setActionCommand(dto.getCommand());
        rule.setConditionOp(dto.getConditionOp());
        rule.setIsActive(dto.getActive());
        rule.setSensor(sensor);
        rule.setActuator(actuator);

        ruleRepository.save(rule);


        return dto;
    }

    //delete
    public void deleteRule(Long id) {
        log.info("deleteRule");

        if (!ruleRepository.existsById(id)) {
            throw new IllegalArgumentException("규칙을 찾을 수 없음");
        }
        ruleRepository.deleteById(id);
    }

    //view
    public List<AutomationRule> getAllRule() {
        log.info("getAllRule");
        return ruleRepository.findAll();
    }

}
