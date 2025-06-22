package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.AutomationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomationRuleRepository extends JpaRepository<AutomationRule, Long> {
}
