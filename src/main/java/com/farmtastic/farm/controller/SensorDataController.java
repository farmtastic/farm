package com.farmtastic.farm.controller;

import com.farmtastic.farm.dto.ZoneHistoryResponse;
import com.farmtastic.farm.dto.ZoneLatestResponse;
import com.farmtastic.farm.service.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zones")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    // 최신 상태 조회 API
    // GET /api/zones/{zoneId}/latest
    @GetMapping("/{zoneId}/latest")
    public ResponseEntity<ZoneLatestResponse> getLatestSensorData(@PathVariable("zoneId") Long zoneId) {
        ZoneLatestResponse response = sensorDataService.getLatestData(zoneId);
        return ResponseEntity.ok(response);
    }

    // 이력 조회 API
    // GET /api/zones/{zoneId}/history
    @GetMapping("/{zoneId}/history")
    public ResponseEntity<ZoneHistoryResponse> getSensorDataHistory(@PathVariable("zoneId") Long zoneId) {
        ZoneHistoryResponse response = sensorDataService.getHistoryData(zoneId);
        return ResponseEntity.ok(response);
    }
}
