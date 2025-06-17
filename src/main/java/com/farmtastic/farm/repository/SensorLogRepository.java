package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}
