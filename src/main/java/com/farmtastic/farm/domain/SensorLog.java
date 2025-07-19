package com.farmtastic.farm.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_logs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGSERIAL 매핑
    @Column(name = "log_id")
    private Long logId;

    @CreationTimestamp
    @Column(name = "log_time", updatable = false)
    private LocalDateTime logTime;

    @Column(name = "log_value", precision = 10, scale = 2) // NUMERIC(10,2) 매핑
    private BigDecimal logValue;

    // 로그가 기록될 당시의 임계값을 저장할 필드 (비정규화)
    @Column(name = "threshold_value", precision = 10, scale = 2)
    private BigDecimal thresholdValue;

    // SensorLogs(N) -> Device(1) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}