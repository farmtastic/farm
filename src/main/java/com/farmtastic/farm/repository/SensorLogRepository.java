package com.farmtastic.farm.repository;

import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.domain.enums.DeviceType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorLogRepository extends JpaRepository<SensorLog, Long> {

    // 3시간 이후의 로그만 조회
    @Query("SELECT sl FROM SensorLog sl JOIN FETCH sl.device d " +
            "WHERE d.zone.zoneId = :zoneId " +
            "AND d.dType = :dType " +
            "AND sl.logTime >= :startTime " + // 시작 시간 조건 추가
            "ORDER BY sl.logTime ASC")
    List<SensorLog> findLogsAfter(@Param("zoneId") Long zoneId, @Param("dType") DeviceType dType, @Param("startTime") LocalDateTime startTime);

    // 최신 로그 1개
    @Query("SELECT sl FROM SensorLog sl " +
            "WHERE sl.device.zone.zoneId = :zoneId " +
            "AND sl.device.dType = com.farmtastic.farm.domain.enums.DeviceType.SENSOR " +
            "AND sl.logTime = (" +
            "  SELECT MAX(sl2.logTime) FROM SensorLog sl2 WHERE sl2.device = sl.device" +
            ")")
    List<SensorLog> findLatestLogs(@Param("zoneId") Long zoneId);

    // 오래된 데이터를 삭제하기
    @Modifying
    @Transactional
    void deleteByLogTimeBefore(LocalDateTime cutoffDateTime);

    // 서버 시작 시 각 디바이스의 최신 로그를 가져오기 - 추후 선별 저장을 한다면 사용
    // @Query("SELECT sl FROM SensorLog sl WHERE sl.logId IN " +
    //         "(SELECT MAX(sl2.logId) FROM SensorLog sl2 GROUP BY sl2.device.deviceId)")
    // List<SensorLog> findLatestLogForEachDevice();

    Optional<SensorLog> findByDeviceDeviceId(Long deviceId);
}
