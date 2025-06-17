package com.farmtastic.farm;

import com.farmtastic.farm.domain.AutomationRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleRepository extends JpaRepository<AutomationRule, Long> {
}
