package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.enums.ModelType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ZoneLatestResponse {
    private Long zoneId;
    private String zoneName;
    private Map<ModelType, LatestValue> latestValues;

    @Getter
    @Builder
    public static class LatestValue {
        private Double value;
        private LocalDateTime timestamp;
    }
}
