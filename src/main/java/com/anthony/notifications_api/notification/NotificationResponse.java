package com.anthony.notifications_api.notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String content,
        NotificationChannel channel,
        String recipient,
        LocalDateTime createdAt
) {

    public static NotificationResponse from(
            Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getCreatedAt()
        );
    }
}