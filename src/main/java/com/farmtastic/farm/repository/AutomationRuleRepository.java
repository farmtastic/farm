package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.domain.Device;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutomationRuleRepository extends JpaRepository<AutomationRule, Long> {

    // 특정 센서에 연결된 활성화된 모든 자동화 규칙을 찾는 메서드
    AutomationRule findBySensorAndIsActiveTrue(Device sensor);


}
