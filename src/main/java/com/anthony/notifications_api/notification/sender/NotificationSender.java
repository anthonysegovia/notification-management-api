package com.anthony.notifications_api.notification.sender;

import com.anthony.notifications_api.notification.Notification;
import com.anthony.notifications_api.notification.NotificationChannel;

public interface NotificationSender {

    NotificationChannel getChannel();

    void validate(Notification notification);

    void send(Notification notification);
}