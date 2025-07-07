package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.enums.DeviceType;
import com.farmtastic.farm.domain.enums.ModelType; // ModelType import 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    // 기존 메서드 (유지)
    List<Device> findByZoneZoneId(Long zoneId);

    // 기존 메서드 (유지)
    @Query("SELECT d FROM Device d WHERE d.zone.zoneId = :zoneId AND d.dType = :dType")
    List<Device> findByZoneZoneIdAndDType(@Param("zoneId") Long zoneId, @Param("dType") DeviceType dType);

    // ✅ 새로운 로직을 위해 이 메서드를 추가하세요.
    Optional<Device> findByZone_ZoneNameAndModelType(String zoneName, ModelType modelType);

    Optional<Device> findByZoneZoneNameAndModelType(String zoneName, ModelType modelType);
}