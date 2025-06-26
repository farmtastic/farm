package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.domain.Zone;
import com.farmtastic.farm.domain.enums.DeviceType;
import com.farmtastic.farm.domain.enums.ModelType;
import com.farmtastic.farm.dto.ZoneDashboardResponse;
import com.farmtastic.farm.repository.SensorLogRepository;
import com.farmtastic.farm.repository.ZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorDataService {

    private final SensorLogRepository sensorLogRepository;
    private final ZoneRepository zoneRepository;
    private static final int HISTORY_LIMIT = 100;

    // 특정 구역의 최신 센서 데이터를 조회
    public ZoneDashboardResponse getLatestData(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone not found with id: " + zoneId));

        List<SensorLog> latestLogs = sensorLogRepository.findLatestLogsForEachDeviceInZone(zoneId);

        // ModelType 별 최신 값을 Map으로 구성
        Map<ModelType, ZoneDashboardResponse.LatestValue> latestValuesMap = latestLogs.stream()
                .collect(Collectors.toMap(
                        log -> log.getDevice().getMType(),
                        log -> ZoneDashboardResponse.LatestValue.builder()
                                .value(log.getLogValue().doubleValue())
                                .timestamp(log.getLogTime())
                                .build(),
                        (existing, replacement) -> existing // 동일 키 충돌 시 기존 값 유지
                ));

        return ZoneDashboardResponse.builder()
                .zoneId(zone.getZoneId())
                .zoneName(zone.getZoneName())
                .latestValues(latestValuesMap)
                .historyValues(null)
                .build();
    }

    // 특정 구역의 센서 데이터 이력을 조회
    public ZoneDashboardResponse getHistoryData(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone not found with id: " + zoneId));

        // 현재는 해당 파라미터 없으므로 모든 로그를 가져온다고 가정
        List<SensorLog> allLogsInZone = sensorLogRepository.findSensorLogsForZoneHistory(zoneId, DeviceType.SENSOR);

        // 가져온 로그들을 ModelType을 기준으로 그룹화
        Map<ModelType, List<ZoneDashboardResponse.HistoryPoint>> historyValuesMap = allLogsInZone.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getDevice().getMType(),
                        Collectors.mapping(
                                log -> ZoneDashboardResponse.HistoryPoint.builder()
                                        .timestamp(log.getLogTime())
                                        .value(log.getLogValue().doubleValue())
                                        .build(),
                                Collectors.toList()
                        )
                ));

        return ZoneDashboardResponse.builder()
                .zoneId(zone.getZoneId())
                .zoneName(zone.getZoneName())
                .latestValues(null)
                .historyValues(historyValuesMap)
                .build();
    }
}
