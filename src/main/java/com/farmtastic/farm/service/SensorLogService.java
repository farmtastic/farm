package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.dto.LogReceiveDto;
import com.farmtastic.farm.repository.DeviceRepository;
import com.farmtastic.farm.repository.SensorLogRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SensorLogService {

    private final SensorLogRepository sensorLogRepository;
    private final DeviceRepository deviceRepository;

    // MQTT로 수신된 센서 데이터(DTO)를 SensorLog 엔티티로 변환하여 저장
    @Transactional
    public void saveSensorLog(LogReceiveDto logDto) {

        Device device = deviceRepository.findById(logDto.getDeviceId())
                .orElseThrow(() -> new EntityNotFoundException("Device not found with id: " + logDto.getDeviceId()));

        SensorLog newSensorLog = SensorLog.builder()
                .device(device)
                .logValue(logDto.getValue())
                .build();

        // 데이터베이스에 저장
        sensorLogRepository.save(newSensorLog);
    }
}
