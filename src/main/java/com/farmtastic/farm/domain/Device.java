package com.farmtastic.farm.domain;

import com.farmtastic.farm.domain.enums.DeviceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devices")
@Getter
@Setter
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 문자열로 저장
    @Column(name = "d_type", nullable = false)
    private DeviceType dType;

    @Column(name = "m_type", nullable = false)
    private String mType; // 모델 타입은 종류가 많을 수 있어 String으로 처리

    @Column(name = "pin_number")
    private Integer pinNumber;

    @CreationTimestamp
    @Column(name = "installed_at", updatable = false)
    private LocalDateTime installedAt;

    // Devices(N) -> Zone(1) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    // Device(1) -> SensorLogs(N)
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorLog> sensorLogs = new ArrayList<>();

    // Device(1) -> ControlLogs(N)
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ControlLog> controlLogs = new ArrayList<>();

    // Device(1) -> AutomationRules(N) (센서로서의 관계)
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<AutomationRule> rulesAsSensor = new ArrayList<>();

    // Device(1) -> AutomationRules(N) (액추에이터로서의 관계)
    @OneToMany(mappedBy = "actuator", cascade = CascadeType.ALL)
    private List<AutomationRule> rulesAsActuator = new ArrayList<>();
}