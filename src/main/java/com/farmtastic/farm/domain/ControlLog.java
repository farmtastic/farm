package com.farmtastic.farm.domain;

import com.farmtastic.farm.domain.enums.ControlSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "control_logs")
@Getter
@Setter
public class ControlLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @CreationTimestamp
    @Column(name = "log_time", updatable = false)
    private LocalDateTime logTime;

    @Column(nullable = false, length = 50)
    private String command;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ControlSource source;

    // ControlLogs(N) -> Device(1) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}