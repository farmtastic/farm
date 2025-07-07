package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.domain.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorLogRepository extends JpaRepository<SensorLog, Long> {

    // 최신 1개의 센서 값
    @Query("SELECT sl FROM SensorLog sl JOIN FETCH sl.device d " +
            "WHERE d.zone.zoneId = :zoneId " +
            "AND d.dType = :dType " +
            "ORDER BY sl.logTime ASC")
    List<SensorLog> findSensorLogsForZoneHistory(@Param("zoneId") Long zoneId, @Param("dType") DeviceType dType);

    // SensorLog 이력
    @Query("SELECT sl FROM SensorLog sl " +
            "WHERE sl.device.zone.zoneId = :zoneId " +
            "AND sl.device.dType = com.farmtastic.farm.domain.enums.DeviceType.SENSOR " +
            "AND sl.logTime = (" +
            "  SELECT MAX(sl2.logTime) FROM SensorLog sl2 WHERE sl2.device = sl.device" +
            ")")
    List<SensorLog> findLatestLogsForEachDeviceInZone(@Param("zoneId") Long zoneId);

    Optional<SensorLog> findByDevice(Long deviceId);
}
