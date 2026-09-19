package com.anthony.notifications_api.notification.sender;

import org.springframework.stereotype.Component;

import com.anthony.notifications_api.exception.InvalidNotificationException;
import com.anthony.notifications_api.notification.Notification;
import com.anthony.notifications_api.notification.NotificationChannel;

@Component
public class PushNotificationSender implements NotificationSender {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public void send(Notification notification) {

        String deviceToken = notification.getRecipient();

        if (deviceToken == null ||
                deviceToken.isBlank() ||
                deviceToken.length() < 10) {

            throw new InvalidNotificationException(
                    "Invalid device token"
            );
        }

        String payload = """
                {
                    "deviceToken": "%s",
                    "title": "%s",
                    "content": "%s"
                }
                """.formatted(
                    deviceToken,
                    notification.getTitle(),
                    notification.getContent()
                );

        System.out.println("[PUSH] Payload:");
        System.out.println(payload);
        System.out.println("[PUSH] Status: SENT");
    }
}