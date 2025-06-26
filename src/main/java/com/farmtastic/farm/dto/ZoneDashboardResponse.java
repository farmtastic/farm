package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.enums.ModelType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder // 빌더 패턴을 사용하여 객체 생성을 유연하게
public class ZoneDashboardResponse {
    private Long zoneId;
    private String zoneName;
    private Map<ModelType, LatestValue> latestValues; // /latest 응답에 사용
    private Map<ModelType, List<HistoryPoint>> historyValues; // /history 응답에 사용

    // 최신 값의 상세 정보를 담음
    @Getter
    @Builder
    public static class LatestValue {
        private Double value;
        private LocalDateTime timestamp;
    }

    // 이력 데이터 포인트 하나를 담음
    @Getter
    @Builder
    public static class HistoryPoint {
        private LocalDateTime timestamp;
        private Double value;
    }
}
