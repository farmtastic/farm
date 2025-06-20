package com.farmtastic.farm.repository;


import com.farmtastic.farm.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceRepository extends JpaRepository<Device, Long> {
}
