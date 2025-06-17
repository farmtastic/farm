package com.farmtastic.farm.controller;

import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.service.AutomationRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("/api/rules")
public class AutomationRuleController {

    @Autowired
    AutomationRuleService automationRuleService;


    //규칙 전체 목록 조회
    @GetMapping
    public List<AutomationRule> getRuleView(){
        log.info("rule view");

        return automationRuleService.getAllRule();
    }

    //규칙 생성
    @PostMapping
    public AutomationRule insertRule(@RequestBody AutomationRule dto){
        log.info("rule insert");
        return automationRuleService.createRule(dto);
    }

    //규칙 수정
    @PutMapping("/{id}")
    public AutomationRule updateRule(AutomationRule dto, @PathVariable Long id){
        log.info("rule update");
        return automationRuleService.updateRule(dto, id);
    }

    //규칙 삭제
    @DeleteMapping("{/id}")
    public ResponseEntity<String> deleteRule(@PathVariable Long id){
        log.info("rule delete");

        automationRuleService.deleteRule(id);

        return ResponseEntity.ok("삭제 완료");

    }

}
