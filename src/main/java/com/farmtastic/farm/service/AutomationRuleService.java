package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.enums.DeviceType;
import com.farmtastic.farm.dto.AutomationRuleDTO;
import com.farmtastic.farm.repository.DeviceRepository;
import com.farmtastic.farm.repository.AutomationRuleRepository;
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

    private final AutomationRuleRepository automationRuleRepository;
    private final DeviceRepository deviceRepository;

    //insert
    @Transactional
    public AutomationRuleDTO createRule(AutomationRuleDTO dto) {
        log.info("createRule");

        Device sensor =  deviceRepository.findById((long) dto.getSensorId())
                .filter(d -> d.getDType() == DeviceType.SENSOR)
                .orElseThrow(()-> new IllegalArgumentException("센서 없음"));

        Device actuator =  deviceRepository.findById((long) dto.getActuatorId())
                .filter(d -> d.getDType() == DeviceType.ACTUATOR)
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

        AutomationRule saved = automationRuleRepository.save(rule);

        return AutomationRuleDTO.fromEntity(saved);
    }


    //update
    @Transactional
    public AutomationRuleDTO updateRule(AutomationRuleDTO dto, Long id) {
        log.info("updateRule");

        AutomationRule rule = automationRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("규칙 없음"));

        Device sensor =  deviceRepository.findById((long) dto.getSensorId())
                .filter(d -> d.getDType() == DeviceType.SENSOR)
                .orElseThrow(()-> new IllegalArgumentException("센서 없음"));

        Device actuator =  deviceRepository.findById((long) dto.getActuatorId())
                .filter(d -> d.getDType() == DeviceType.ACTUATOR)
                .orElseThrow(()-> new IllegalArgumentException("엑추에이터 없음"));

        rule.update(
                dto.getRuleName(),
                dto.getConditionOp(),
                dto.getThreshold(),
                dto.getCommand(),
                sensor,
                actuator,
                dto.getActive()
        );

        AutomationRule updated = automationRuleRepository.save(rule);


        return AutomationRuleDTO.fromEntity(updated);
    }


    //view
    public List<AutomationRule> getAllRule() {
        log.info("getAllRule");
        return automationRuleRepository.findAll();
    }


    //delete
    @Transactional
    public void deleteRuleById(Long id) {
        automationRuleRepository.deleteById(id);
    }
}
