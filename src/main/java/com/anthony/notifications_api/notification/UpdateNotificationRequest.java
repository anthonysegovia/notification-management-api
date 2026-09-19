package com.anthony.notifications_api.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateNotificationRequest(

        @NotBlank
        String title,

        @NotBlank
        String content,

        @NotNull
        NotificationChannel channel,

        @NotBlank
        String recipient

) {
}