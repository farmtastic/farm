package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.AutomationRule;
import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.domain.enums.ModelType;
import com.farmtastic.farm.dto.LogReceiveDto;
import com.farmtastic.farm.repository.AutomationRuleRepository;
import com.farmtastic.farm.repository.DeviceRepository;
import com.farmtastic.farm.repository.SensorLogRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SensorLogService {

    private final SensorLogRepository sensorLogRepository;
    private final DeviceRepository deviceRepository;
    private final AutomationRuleRepository automationRuleRepository;
    private final ControlLogService controlLogService;

    // MQTT로 수신된 센서 데이터(DTO)를 SensorLog 엔티티로 변환하여 저장
    @Transactional
    public void saveSensorLog(LogReceiveDto logDto) {
        log.info(String.valueOf(logDto.getDeviceId()));
        Device device = deviceRepository.findById(logDto.getDeviceId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Device not found with id: " + logDto.getDeviceId()));

        BigDecimal currentThreshold = automationRuleRepository.findBySensorAndIsActiveTrue(device)
            .stream() // 리스트를 스트림으로 변환
            .findFirst() // 스트림의 첫 번째 항목을 찾음 (결과: Optional<AutomationRule>)
            .map(AutomationRule::getThresholdValue) // 항목이 있다면, ThresholdValue를 가져옴
            .orElse(null); // 항목이 없다면(리스트가 비었다면) null을 반환

        SensorLog newSensorLog = SensorLog.builder()
                .device(device)
                .logValue(logDto.getValue())
                .thresholdValue(currentThreshold)
                .build();

        // 데이터베이스에 저장
        sensorLogRepository.save(newSensorLog);
    }

    @Transactional // 여러 로그를 하나의 트랜잭션으로 묶어 처리합니다.
    public void saveSensorLogsFromMap(String topic, Map<String, BigDecimal> sensorDataMap) {

        // 1. 토픽에서 Zone 이름 파싱 (예시: "farm/data/zone-A" -> "zone-A")
        String zoneName = topic.substring(topic.lastIndexOf("/") + 1);

        // 2. Map의 각 항목(센서 데이터)을 순회하며 처리
        sensorDataMap.forEach((key, value) -> {
            try {
                // 3. Map의 key(ph, light 등)를 ModelType Enum으로 변환
                ModelType modelType = ModelType.valueOf(key.toUpperCase());

                // 4. Zone 이름과 ModelType으로 해당하는 Device 정보 조회
                deviceRepository.findByZoneZoneNameAndModelType(zoneName, modelType)
                    .ifPresent(device -> {

                        BigDecimal currentThreshold = automationRuleRepository.findBySensorAndIsActiveTrue(device)
                            .stream() // 리스트를 스트림으로 변환
                            .findFirst() // 스트림의 첫 번째 항목을 찾음 (결과: Optional<AutomationRule>)
                            .map(AutomationRule::getThresholdValue) // 항목이 있다면, ThresholdValue를 가져옴
                            .orElse(null); // 항목이 없다면(리스트가 비었다면) null을 반환

                        // SensorLog 저장
                        SensorLog sensorLog = SensorLog.builder()
                                .device(device)
                                .logValue(value)
                                .thresholdValue(currentThreshold)
                                .build();

                        sensorLogRepository.save(sensorLog);

                        // 6. (중요) 해당 센서에 대한 자동화 규칙 검사 및 실행 로직 호출

                        controlLogService.evaluateSensor(device, value);


                    });

            } catch (IllegalArgumentException e) {
                log.warn("지원하지 않는 ModelType 입니다: {}", key);
            }
        });
    }
}
