package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.Device;
import com.farmtastic.farm.domain.SensorLog;
import com.farmtastic.farm.domain.enums.DeviceType;
import com.farmtastic.farm.dto.DataResponse;
import com.farmtastic.farm.dto.DataResponse.*;
import com.farmtastic.farm.repository.DeviceRepository;
import com.farmtastic.farm.repository.SensorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorDataService {

    private final SensorLogRepository sensorLogRepository;
    private final DeviceRepository deviceRepository;
    private static final int HISTORY_LIMIT = 100;

    // 특정 구역의 최신 센서 데이터를 조회
    public Latest getLatestData(Long zoneId) {
        List<SensorLog> latestLogs = sensorLogRepository.findLatestLogsForEachDeviceInZone(zoneId);
        // 수정된 생성자 호출
        return new Latest(latestLogs);
    }

    // 특정 구역의 센서 데이터 이력을 조회
    public Map<String, List<DataResponse.HistoryPoint>> getHistoryData(Long zoneId) {
        List<Device> devicesInZone = deviceRepository.findByZoneZoneIdAndDType(zoneId, DeviceType.SENSOR);

        return devicesInZone.stream()
                .collect(Collectors.toMap(
                        device -> device.getMType().name(),
                        device -> {
                            Pageable pageable = PageRequest.of(0, HISTORY_LIMIT);
                            List<SensorLog> logs = sensorLogRepository.findByDeviceIdOrderByLogTimeDesc(device.getDeviceId(), pageable);

                            Collections.reverse(logs);

                            return logs.stream()
                                    // 수정된 생성자 호출
                                    .map(log -> new HistoryPoint(log.getLogTime(), log.getLogValue()))
                                    .collect(Collectors.toList());
                        }
                ));
    }
}
