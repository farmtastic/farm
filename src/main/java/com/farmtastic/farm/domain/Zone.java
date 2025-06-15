package com.farmtastic.farm.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "zones")
@Getter
@Setter
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL 타입 매핑
    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp // 엔티티 생성 시 자동으로 현재 시간 저장
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Zone(1) -> Devices(N) 관계
    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Device> devices = new ArrayList<>();
}

