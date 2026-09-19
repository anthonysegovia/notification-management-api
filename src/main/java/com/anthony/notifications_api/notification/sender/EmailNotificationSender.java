package com.anthony.notifications_api.notification.sender;

import org.springframework.stereotype.Component;

import com.anthony.notifications_api.exception.InvalidNotificationException;
import com.anthony.notifications_api.notification.Notification;
import com.anthony.notifications_api.notification.NotificationChannel;

@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void validate(Notification notification) {

        String recipient = notification.getRecipient();

        if (recipient == null ||
                !recipient.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {

            throw new InvalidNotificationException(
                    "Invalid email recipient"
            );
        }
    }

    @Override
    public void send(Notification notification) {

        String recipient = notification.getRecipient();

        String template = """
                Subject: %s

                %s
                """.formatted(
                    notification.getTitle(),
                    notification.getContent()
                );

        System.out.println(
                "[EMAIL] Sending to: " + recipient
        );

        System.out.println(template);
    }
}