package com.farmtastic.farm.controller;

import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.dto.AutomationRuleDTO;
import com.farmtastic.farm.service.AutomationRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.SharedTimer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class AutomationRuleController {


    private final AutomationRuleService automationRuleService;


    //규칙 전체 목록 조회
    @GetMapping
    public List<AutomationRuleDTO> getRuleView(){
        log.info("rule view");

        return automationRuleService.getAllRule().stream()
                .map(AutomationRuleDTO::fromEntity)
                .toList();
    }

    //규칙 생성
    @PostMapping
    public ResponseEntity<AutomationRuleDTO> insertRule(@RequestBody AutomationRuleDTO dto){
        log.info("rule insert");
        AutomationRuleDTO rule = automationRuleService.createRule(dto);
        return ResponseEntity.ok(rule);
    }

    //규칙 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateRule(@RequestBody AutomationRuleDTO dto, @PathVariable Long id){
        log.info("rule update");
        AutomationRuleDTO updateRule = automationRuleService.updateRule(dto, id);
        return ResponseEntity.ok("수정 완료");
    }

    //규칙 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRule( @PathVariable Long id){
        log.info("delete rule");
        String deleteRule = automationRuleService.deleteRuleById( id);

        return ResponseEntity.ok(deleteRule);
    }


}
