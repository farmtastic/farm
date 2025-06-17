package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.ControlLog;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ControlLogDto {
    private final String deviceName;
    private final String command;
    private final String source;
    private final LocalDateTime logTime;

    public ControlLogDto(ControlLog controlLog) {
        this.deviceName = controlLog.getDevice().getDeviceName();
        this.command = controlLog.getCommand();
        this.source = controlLog.getSource().toString();
        this.logTime = controlLog.getLogTime();
    }
}
