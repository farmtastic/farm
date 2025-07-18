package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.enums.ModelType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ZoneHistoryResponse {
    private Long zoneId;
    private String zoneName;
    private Map<ModelType, List<HistoryPoint>> historyValues;

    @Getter
    @Builder
    public static class HistoryPoint {
        private LocalDateTime timestamp;
        private Double value;
    }
}
