package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.ControlLog;
import com.farmtastic.farm.domain.Notification;
import com.farmtastic.farm.event.ControlLogCreatedEvent;
import com.farmtastic.farm.repository.NotificationRepository;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ControlLog 생성 트랜잭션이 성공적으로 완료되면 이벤트를 비동기로 처리
    @Async
    @TransactionalEventListener
    public void handleControlLogCreated(ControlLogCreatedEvent event) {
        ControlLog controlLog = event.getControlLog();

        // 1. 사용자 친화적인 알림 메시지 생성
        String formattedTime = controlLog.getLogTime()
            .format(DateTimeFormatter.ofPattern("MM월 dd일 HH:mm"));
        String message = String.format("%s, '%s' 장치가 '%s' 명령을 수행했습니다. (원인: %s)",
            formattedTime,
            controlLog.getDevice().getDeviceName(),
            controlLog.getCommand(),
            controlLog.getSource().toString()
        );

        log.info(message);


        // 2. 알림 엔티티 생성 및 저장
        Notification notification = Notification.builder()
            .message(message)
            .build();
        notificationRepository.save(notification);
        log.info("새로운 알림이 생성 및 저장되었습니다: {}", message);

    }
}
