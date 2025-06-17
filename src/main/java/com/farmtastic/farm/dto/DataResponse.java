package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.SensorLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DataResponse {

    private DataResponse() {}

    // 최신 상태 조회 (/latest) API의 응답 DTO
    @Getter
    public static class Latest {
        private BigDecimal waterLevel;
        private BigDecimal illuminance;
        private BigDecimal ph;
        private LocalDateTime timestamp;

        public Latest(List<SensorLog> latestLogs) {
            if (latestLogs == null || latestLogs.isEmpty()) {
                return;
            }

            this.timestamp = latestLogs.stream()
                    .map(SensorLog::getLogTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            for (SensorLog log : latestLogs) {
                switch (log.getDevice().getMType()) {
                    case WATER_LEVEL:
                        this.waterLevel = log.getLogValue();
                        break;
                    case ILLUMINANCE:
                        this.illuminance = log.getLogValue();
                        break;
                    case PH:
                        this.ph = log.getLogValue();
                        break;
                    default:
                        // 없는 타입은 무시하거나 로그
                        break;
                }
            }
        }
    }

    // 이력 조회 (/history) API에서 사용될 개별 데이터 포인트 DTO
    @Getter
    @AllArgsConstructor
    public static class HistoryPoint {
        private LocalDateTime timestamp;
        private BigDecimal value;
    }
}
