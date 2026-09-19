package com.anthony.notifications_api.notification.sender;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.anthony.notifications_api.exception.InvalidNotificationException;
import com.anthony.notifications_api.notification.Notification;
import com.anthony.notifications_api.notification.NotificationChannel;

@Component
public class SmsNotificationSender implements NotificationSender {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
        public void validate(Notification notification) {

        String phoneNumber = notification.getRecipient();

        if (phoneNumber == null ||
                !phoneNumber.matches("^\\+?[0-9]{10,15}$")) {

            throw new InvalidNotificationException(
                    "Invalid phone number"
            );
        }

        if (notification.getContent().length() > 160) {
            throw new InvalidNotificationException(
                    "SMS content cannot exceed 160 characters"
            );
        }
        }

        @Override
        public void send(Notification notification) {

                String phoneNumber = notification.getRecipient();

        System.out.println(
                "[SMS] Number: " + phoneNumber
        );

        System.out.println(
                "[SMS] Date: " + LocalDateTime.now()
        );

        System.out.println(
                "[SMS] Content: " + notification.getContent()
        );
    }
}