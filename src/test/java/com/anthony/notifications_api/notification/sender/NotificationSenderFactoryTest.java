package com.anthony.notifications_api.notification.sender;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.anthony.notifications_api.notification.NotificationChannel;

class NotificationSenderFactoryTest {

    private final EmailNotificationSender emailSender =
            new EmailNotificationSender();

    private final SmsNotificationSender smsSender =
            new SmsNotificationSender();

    private final PushNotificationSender pushSender =
            new PushNotificationSender();

    private final NotificationSenderFactory factory =
            new NotificationSenderFactory(
                    List.of(
                            emailSender,
                            smsSender,
                            pushSender
                    )
            );

    @Test
    void shouldReturnEmailSender() {

        NotificationSender sender =
                factory.getSender(NotificationChannel.EMAIL);

        assertInstanceOf(
                EmailNotificationSender.class,
                sender
        );
    }

    @Test
    void shouldReturnSmsSender() {

        NotificationSender sender =
                factory.getSender(NotificationChannel.SMS);

        assertInstanceOf(
                SmsNotificationSender.class,
                sender
        );
    }

    @Test
    void shouldReturnPushSender() {

        NotificationSender sender =
                factory.getSender(NotificationChannel.PUSH);

        assertInstanceOf(
                PushNotificationSender.class,
                sender
        );
    }
}