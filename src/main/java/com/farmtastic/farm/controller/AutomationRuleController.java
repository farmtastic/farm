package com.farmtastic.farm.controller;

import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.dto.AutomationRuleDTO;
import com.farmtastic.farm.service.AutomationRuleService;
import com.farmtastic.farm.service.SensorLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.SharedTimer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
        log.info("규칙 생성{}", rule);
        return ResponseEntity.ok(rule);
    }

    //규칙 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<AutomationRuleDTO> updateRule(@RequestBody AutomationRuleDTO dto, @PathVariable Long id){
        log.info("rule update");
        AutomationRuleDTO updateRule = automationRuleService.updateRule(dto, id);
        log.info("규칙 수정{}", updateRule);
        return ResponseEntity.ok(updateRule);
    }

    //규칙 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRule( @PathVariable Long id){
        log.info("delete rule");
        String deleteRule = automationRuleService.deleteRuleById( id);

        return ResponseEntity.ok(deleteRule);
    }


      private final SensorLogService sensorLogService;

    //자동제어 명령어 테스트를 위한 post
    @PostMapping("/test")
    public ResponseEntity<String> testSensorData(@RequestBody Map<String, BigDecimal> sensorDataMap) {
        log.info("test 시작 ");
        String testTopic = "farm/data/zone-A"; // zone 이름은 고정 테스트
        sensorLogService.saveSensorLogsFromMap(testTopic, sensorDataMap);
        return ResponseEntity.ok("센서 데이터 수신 및 처리 완료");
    }


}
