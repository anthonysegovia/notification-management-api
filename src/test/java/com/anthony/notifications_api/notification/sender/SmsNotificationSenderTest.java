package com.anthony.notifications_api.notification.sender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.anthony.notifications_api.exception.InvalidNotificationException;
import com.anthony.notifications_api.notification.Notification;
import com.anthony.notifications_api.notification.NotificationChannel;

class SmsNotificationSenderTest {

    private final SmsNotificationSender sender =
            new SmsNotificationSender();

    @Test
        void shouldValidateSmsWhenContentHas160CharactersOrLess() {

        String content = "A".repeat(160);

        Notification notification = new Notification(
                "Test SMS",
                content,
                NotificationChannel.SMS,
                "+528112345678",
                null
        );

        assertDoesNotThrow(() ->
                sender.validate(notification)
        );
    }

    @Test
    void shouldRejectSmsWhenContentExceeds160Characters() {

        String content = "A".repeat(161);

        Notification notification = new Notification(
                "Test SMS",
                content,
                NotificationChannel.SMS,
                "+528112345678",
                null
        );

        assertThrows(
                InvalidNotificationException.class,
                () -> sender.validate(notification)
        );
    }
}