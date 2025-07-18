package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.domain.Zone;
import com.farmtastic.farm.domain.enums.DeviceType;
import com.farmtastic.farm.domain.enums.ModelType;
import com.farmtastic.farm.dto.ZoneHistoryResponse;
import com.farmtastic.farm.dto.ZoneLatestResponse;
import com.farmtastic.farm.repository.SensorLogRepository;
import com.farmtastic.farm.repository.ZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorDataService {

    private final SensorLogRepository sensorLogRepository;
    private final ZoneRepository zoneRepository;

    // 특정 구역의 최신 센서 데이터를 조회
    public ZoneLatestResponse getLatestData(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone not found with id: " + zoneId));

        List<SensorLog> latestLogs = sensorLogRepository.findLatestLogs(zoneId);

        // ModelType 별 최신 값을 Map으로 구성
        Map<ModelType, ZoneLatestResponse.LatestValue> latestValuesMap = latestLogs.stream()
                .collect(Collectors.toMap(
                        log -> log.getDevice().getModelType(),
                        log -> ZoneLatestResponse.LatestValue.builder()
                                .value(log.getLogValue().doubleValue())
                                .timestamp(log.getLogTime())
                                .build(),
                        (existing, replacement) -> existing // 동일 키 충돌 시 기존 값 유지
                ));

        return ZoneLatestResponse.builder()
                .zoneId(zone.getZoneId())
                .zoneName(zone.getZoneName())
                .latestValues(latestValuesMap)
                .build();
    }

    // 특정 구역의 최근 3시간 센서 데이터 이력을 조회
    public ZoneHistoryResponse getHistoryData(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone not found with id: " + zoneId));

        // 조회 시작 시간을 현재로부터 3시간 전으로 설정
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);

        List<SensorLog> recentLogs = sensorLogRepository.findLogsAfter(zoneId, DeviceType.SENSOR, startTime);

        // 가져온 로그들을 ModelType을 기준으로 그룹화
        Map<ModelType, List<ZoneHistoryResponse.HistoryPoint>> historyValuesMap = recentLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getDevice().getModelType(),
                        Collectors.mapping(
                                log -> ZoneHistoryResponse.HistoryPoint.builder()
                                        .timestamp(log.getLogTime())
                                        .value(log.getLogValue().doubleValue())
                                        .threshold(log.getThresholdValue() != null ? log.getThresholdValue().doubleValue() : null)
                                        .build(),
                                Collectors.toList()
                        )
                ));

        return ZoneHistoryResponse.builder()
                .zoneId(zone.getZoneId())
                .zoneName(zone.getZoneName())
                .historyValues(historyValuesMap)
                .build();
    }
}
