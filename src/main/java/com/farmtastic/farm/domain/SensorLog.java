package com.farmtastic.farm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_logs")
@Getter
@Setter
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

    // SensorLogs(N) -> Device(1) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}