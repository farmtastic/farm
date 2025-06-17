package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.Notification;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class NotificationDto {
    private final String message;
    private final boolean isRead;
    private final LocalDateTime createdAt;

    public NotificationDto(Notification notification) {
        this.message = notification.getMessage();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }
}
