package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Zone에 속한 모든 디바이스 목록을 조회
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByZoneZoneId(Long zoneId);
    List<Device> findByZoneZoneIdAndDType(Long zoneId, DeviceType dType);
}
