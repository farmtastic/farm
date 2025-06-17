package com.farmtastic.farm.controller;

import com.farmtastic.farm.domain.Notification;
import com.farmtastic.farm.dto.ControlLogDto;
import com.farmtastic.farm.dto.NotificationDto;
import com.farmtastic.farm.repository.ControlLogRepository;
import com.farmtastic.farm.repository.NotificationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final ControlLogRepository controlLogRepository;

    /**
     * 알림 목록 조회 API
     * @return 최신순으로 정렬된 알림 목록
     */
    @GetMapping("/notifications")
    public List<NotificationDto> getNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(NotificationDto::new)
            .collect(Collectors.toList());
    }

    /**
     * 제어 이력 조회 API
     *
     * @return 전체 제어 이력 목록
     */
    @GetMapping("/controls/logs")
    public List<ControlLogDto> getControlLogs() {
        return controlLogRepository.findAll().stream()
            .map(ControlLogDto::new)
            .collect(Collectors.toList());
    }
}
