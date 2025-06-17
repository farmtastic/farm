package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.SensorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import com.farmtastic.farm.domain.enums.DeviceType.*;

import java.util.List;

public interface SensorLogRepository extends JpaRepository<SensorLog, Long> {

    // 특정 Zone에 속한 모든 Device들의 각각의 최신 SensorLog
    @Query("SELECT sl FROM SensorLog sl " +
            "WHERE sl.device.zone.zoneId = :zoneId " +
            // SENSOR 타입 필터링
            "  AND sl.device.dType = com.farmtastic.farm.domain.enums.DeviceType.SENSOR " +
            "  AND sl.logTime = (" +
            "    SELECT MAX(sl2.logTime) FROM SensorLog sl2 WHERE sl2.device = sl.device" +
            "  )")
    List<SensorLog> findLatestLogsForEachDeviceInZone(@Param("zoneId") Long zoneId);

    // 특정 Device의 SensorLog 이력을 최신순으로 조회
    List<SensorLog> findByDeviceIdOrderByLogTimeDesc(Long deviceId, Pageable pageable);
}
