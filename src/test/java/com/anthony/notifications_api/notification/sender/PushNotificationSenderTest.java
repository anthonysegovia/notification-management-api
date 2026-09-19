package com.anthony.notifications_api.notification.sender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.anthony.notifications_api.exception.InvalidNotificationException;
import com.anthony.notifications_api.notification.Notification;
import com.anthony.notifications_api.notification.NotificationChannel;

class PushNotificationSenderTest {

    private final PushNotificationSender sender =
            new PushNotificationSender();

    @Test
    void shouldSendPushWithValidDeviceToken() {

        Notification notification = new Notification(
                "New message",
                "You have a new message",
                NotificationChannel.PUSH,
                "device-token-abc123",
                null
        );

        assertDoesNotThrow(() ->
                sender.send(notification)
        );
    }

    @Test
    void shouldRejectInvalidDeviceToken() {

        Notification notification = new Notification(
                "New message",
                "You have a new message",
                NotificationChannel.PUSH,
                "abc",
                null
        );

        assertThrows(
                InvalidNotificationException.class,
                () -> sender.send(notification)
        );
    }
}