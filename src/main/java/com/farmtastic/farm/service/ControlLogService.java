package com.farmtastic.farm.service;

import com.farmtastic.farm.domain.ControlLog;
import com.farmtastic.farm.event.ControlLogCreatedEvent;
import com.farmtastic.farm.repository.ControlLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ControlLogService {

    private final ControlLogRepository controlLogRepository;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행을 위한 객체

    @Transactional
    public void createLog(ControlLog controlLog) {
        // 1. 제어 이력을 먼저 저장
        controlLogRepository.save(controlLog);

        // 2. 저장이 완료되면 이벤트를 발행
        eventPublisher.publishEvent(new ControlLogCreatedEvent(controlLog));
    }
}
