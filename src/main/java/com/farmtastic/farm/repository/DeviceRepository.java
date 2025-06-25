package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Zone에 속한 모든 디바이스 목록을 조회
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByZoneZoneId(Long zoneId);

    @Query("SELECT d FROM Device d WHERE d.zone.zoneId = :zoneId AND d.dType = :dType")
    List<Device> findByZoneZoneIdAndDType(@Param("zoneId") Long zoneId, @Param("dType") DeviceType dType);

}
