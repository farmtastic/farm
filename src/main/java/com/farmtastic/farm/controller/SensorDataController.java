package com.farmtastic.farm.controller;

import com.farmtastic.farm.service.SensorDataService;
import com.farmtastic.farm.dto.DataResponse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zones")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    // 최신 상태 조회 API
    // GET /api/zones/{zoneId}/latest
    @GetMapping("/{zoneId}/latest")
    public ResponseEntity<Latest> getLatestSensorData(@PathVariable Long zoneId) {
        Latest response = sensorDataService.getLatestData(zoneId);
        return ResponseEntity.ok(response);
    }

    // 이력 조회 API
    // GET /api/zones/{zoneId}/history
    @GetMapping("/{zoneId}/history")
    public ResponseEntity<Map<String, List<HistoryPoint>>> getSensorDataHistory(@PathVariable Long zoneId) {
        Map<String, List<HistoryPoint>> response = sensorDataService.getHistoryData(zoneId);
        return ResponseEntity.ok(response);
    }
}
