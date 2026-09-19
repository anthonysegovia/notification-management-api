package com.anthony.notifications_api.notification;

import org.springframework.stereotype.Service;
import java.util.List;

import com.anthony.notifications_api.exception.ResourceNotFoundException;
import com.anthony.notifications_api.notification.sender.NotificationSender;
import com.anthony.notifications_api.notification.sender.NotificationSenderFactory;
import com.anthony.notifications_api.user.User;
import com.anthony.notifications_api.user.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationSenderFactory senderFactory;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            NotificationSenderFactory senderFactory) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.senderFactory = senderFactory;
    }

    public NotificationResponse create(
            String email,
            CreateNotificationRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                new ResourceNotFoundException("User not found")
                );
                
        Notification notification = new Notification(
                request.title(),
                request.content(),
                request.channel(),
                request.recipient(),
                user
        );

        NotificationSender sender =
                senderFactory.getSender(request.channel());

        sender.send(notification);

        Notification saved =
                notificationRepository.save(notification);

        return NotificationResponse.from(saved);
    }

    public List<NotificationResponse> findAll(String email) {

        return notificationRepository
                .findAllByUserEmail(email)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    public NotificationResponse findById(
        Long id,
        String email) {

        Notification notification =
                notificationRepository
                        .findByIdAndUserEmail(id, email)
                        .orElseThrow(() ->
                            new ResourceNotFoundException(
                                "Notification not found"
                            )
                        );

        return NotificationResponse.from(notification);
    }

    public NotificationResponse update(
        Long id,
        String email,
        UpdateNotificationRequest request) {

        Notification notification =
                    notificationRepository
                            .findByIdAndUserEmail(id, email)
                            .orElseThrow(() ->
                                new ResourceNotFoundException(
                                    "Notification not found"
                                )
                            );

        notification.setTitle(request.title());
        notification.setContent(request.content());
        notification.setChannel(request.channel());
        notification.setRecipient(request.recipient());

        Notification updated =
                notificationRepository.save(notification);

        return NotificationResponse.from(updated);
    }

    public void delete(
        Long id,
        String email) {

        Notification notification =
                notificationRepository
                        .findByIdAndUserEmail(id, email)
                        .orElseThrow(() ->
                            new ResourceNotFoundException(
                                "Notification not found"
                            )
                        );

        notificationRepository.delete(notification);
    }
}