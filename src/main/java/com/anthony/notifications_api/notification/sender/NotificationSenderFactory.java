package com.anthony.notifications_api.notification.sender;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.anthony.notifications_api.exception.InvalidNotificationException;
import com.anthony.notifications_api.notification.NotificationChannel;

@Component
public class NotificationSenderFactory {

    private final Map<NotificationChannel, NotificationSender> senders;

    public NotificationSenderFactory(
            List<NotificationSender> senderList) {

        this.senders = new EnumMap<>(NotificationChannel.class);

        for (NotificationSender sender : senderList) {
            senders.put(sender.getChannel(), sender);
        }
    }

    public NotificationSender getSender(
            NotificationChannel channel) {

        NotificationSender sender = senders.get(channel);

        if (sender == null) {
            throw new InvalidNotificationException(
                    "Unsupported notification channel: " + channel
            );
        }

        return sender;
    }
}