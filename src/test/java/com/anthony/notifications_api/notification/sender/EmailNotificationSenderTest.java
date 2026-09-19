package com.anthony.notifications_api.notification.sender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.anthony.notifications_api.exception.InvalidNotificationException;
import com.anthony.notifications_api.notification.Notification;
import com.anthony.notifications_api.notification.NotificationChannel;

class EmailNotificationSenderTest {

    private final EmailNotificationSender sender =
            new EmailNotificationSender();

    @Test
        void shouldValidateEmailWithValidRecipient() {

        Notification notification = new Notification(
                "Welcome",
                "Welcome to the application!",
                NotificationChannel.EMAIL,
                "anthony@example.com",
                null
        );

        assertDoesNotThrow(() ->
                sender.validate(notification)
        );
    }

    @Test
    void shouldRejectInvalidEmailRecipient() {

        Notification notification = new Notification(
                "Welcome",
                "Welcome to the application!",
                NotificationChannel.EMAIL,
                "invalid-email",
                null
        );

        assertThrows(
                InvalidNotificationException.class,
                () -> sender.validate(notification)
        );
    }
}