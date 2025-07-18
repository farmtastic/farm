package com.farmtastic.farm.service;

import com.farmtastic.farm.repository.SensorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataCleanupService {

    private final SensorLogRepository sensorLogRepository;
    private static final int DATA_RETENTION_DAYS = 1; // 데이터 보관 주기 (1일)

    // 매일 새벽 3시에 오래된 로그 데이터를 삭제
    @Scheduled(cron = "0 * * * * *")
    public void purgeOldSensorLogs() {

        LocalDateTime cutoffDateTime = LocalDateTime.now().minusDays(DATA_RETENTION_DAYS);

        try {
            sensorLogRepository.deleteByLogTimeBefore(cutoffDateTime);
            log.info("오래된 센서 로그 데이터 삭제가 완료되었습니다.");
        } catch (Exception e) {
            log.error("오래된 센서 로그 데이터 삭제 중 오류가 발생했습니다.", e);
        }
    }

}
