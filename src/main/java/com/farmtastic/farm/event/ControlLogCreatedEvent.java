package com.farmtastic.farm.event;

import com.farmtastic.farm.domain.ControlLog;
import lombok.Getter;

// ControlLog가 생성되었을 때 발행할 이벤트
@Getter
public class ControlLogCreatedEvent {
    private final ControlLog controlLog;

    public ControlLogCreatedEvent(ControlLog controlLog) {
        this.controlLog = controlLog;
    }
}
