package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.enums.DeviceType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ZoneResponse {
    // Main Response DTO: /api/zones/{zoneId}/dashboard 의 응답
    public record ZoneDashboardResponse(
            Long zoneId,
            String zoneName,
            Map<DeviceType, LatestValue> latestValues,   // 센서 타입별 최신 값
            Map<DeviceType, List<HistoryDataPoint>> historyValues // 센서 타입별 이력 값
    ) {}

    // 최신 값 상세 정보 DTO
    public record LatestValue(
            Double value,
            LocalDateTime timestamp
    ) {}

    // 이력 데이터 포인트 DTO
    public record HistoryDataPoint(
            LocalDateTime timestamp,
            Double value
    ) {}

}
