package com.farmtastic.farm.service;

import com.farmtastic.farm.RuleRepository;
import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.domain.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AutomationRuleService {

    private final RuleRepository ruleRepository;

    public  AutomationRuleService(RuleRepository ruleRepository){
        this.ruleRepository = ruleRepository;
    }


    public List<AutomationRule> getAllRule() {
        log.info("getAllRule");
        return ruleRepository.findAll();
    }

    public AutomationRule createRule(AutomationRule dto) {
        log.info("createRule");
/*
        디바이스 repository 하면 작성할 것임
         그 후에 디바이스 연동되는 여부 나타내는 것
            Device device = deviceRepository.findById(dto.getDeviceId())
                .orElseThrow(() -> new RuntimeException("디바이스 없음"));

 */

        AutomationRule rule = new AutomationRule();
        rule.setRuleName(dto.getRuleName());
        rule.setThresholdValue(dto.getThresholdValue());
        rule.setConditionOp(dto.getConditionOp());
        rule.setIsActive(dto.getIsActive());
        rule.setActionCommand(dto.getActionCommand());
        return ruleRepository.save(rule);
    }

    public AutomationRule updateRule(AutomationRule dto, Long id) {
        log.info("updateRule");

        AutomationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 규칙이 존재하지 않습니다."));
        rule.setRuleName(dto.getRuleName());
        rule.setThresholdValue(dto.getThresholdValue());
        rule.setConditionOp(dto.getConditionOp());
        rule.setIsActive(dto.getIsActive());
        rule.setActionCommand(dto.getActionCommand());
        return ruleRepository.save(rule);
    }

    public void deleteRule(Long id) {
        log.info("deleteRule");

        ruleRepository.deleteById(id);
    }

}
