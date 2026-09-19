package com.anthony.notifications_api.notification;
import com.anthony.notifications_api.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.MockitoAnnotations;

import com.anthony.notifications_api.exception.InvalidNotificationException;
import com.anthony.notifications_api.exception.ResourceNotFoundException;
import com.anthony.notifications_api.notification.sender.NotificationSender;
import com.anthony.notifications_api.notification.sender.NotificationSenderFactory;
import com.anthony.notifications_api.user.UserRepository;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationSenderFactory senderFactory;

    @Mock
    private NotificationSender notificationSender;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        notificationService = new NotificationService(
                notificationRepository,
                userRepository,
                senderFactory
        );
    }

    @Test
    void shouldNotAllowAccessToNotificationFromAnotherUser() {

        Long notificationId = 1L;
        String email = "other@example.com";

        when(
            notificationRepository.findByIdAndUserEmail(
                notificationId,
                email
            )
        ).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.findById(
                        notificationId,
                        email
                )
        );
    }

    @Test
    void shouldReturnNotificationWhenItBelongsToUser() {

        User user = new User(
                "anthony@example.com",
                "hashed-password"
        );

        Notification notification = new Notification(
                "Welcome",
                "Hello Anthony",
                NotificationChannel.EMAIL,
                "anthony@example.com",
                user
        );

        when(
            notificationRepository.findByIdAndUserEmail(
                1L,
                "anthony@example.com"
            )
        ).thenReturn(Optional.of(notification));

        NotificationResponse response =
                notificationService.findById(
                        1L,
                        "anthony@example.com"
                );

        assertEquals("Welcome", response.title());
        assertEquals("Hello Anthony", response.content());
        assertEquals(
                NotificationChannel.EMAIL,
                response.channel()
        );
    }

        @Test
        void shouldValidateAndSendBeforeSavingNotification() {

        User user = new User(
            "anthony@example.com",
            "hashed-password"
        );
        CreateNotificationRequest request =
            new CreateNotificationRequest(
                "Welcome",
                "Hello Anthony",
                NotificationChannel.EMAIL,
                "anthony@example.com"
            );

        when(userRepository.findByEmail("anthony@example.com"))
            .thenReturn(Optional.of(user));
        when(senderFactory.getSender(NotificationChannel.EMAIL))
            .thenReturn(notificationSender);
        when(notificationRepository.save(any(Notification.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.create("anthony@example.com", request);

        InOrder order = inOrder(notificationSender, notificationRepository);
        order.verify(notificationSender).validate(any(Notification.class));
        order.verify(notificationSender).send(any(Notification.class));
        order.verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        void shouldValidateUpdatedNotificationWithoutSendingIt() {

        Notification notification = new Notification(
            "Welcome",
            "Hello Anthony",
            NotificationChannel.EMAIL,
            "anthony@example.com",
            new User("anthony@example.com", "hashed-password")
        );
        UpdateNotificationRequest request =
            new UpdateNotificationRequest(
                "SMS update",
                "Updated content",
                NotificationChannel.SMS,
                "+528112345678"
            );

        when(notificationRepository.findByIdAndUserEmail(1L, "anthony@example.com"))
            .thenReturn(Optional.of(notification));
        when(senderFactory.getSender(NotificationChannel.SMS))
            .thenReturn(notificationSender);
        when(notificationRepository.save(notification)).thenReturn(notification);

        notificationService.update(1L, "anthony@example.com", request);

        verify(notificationSender).validate(notification);
        verify(notificationSender, never()).send(any(Notification.class));
        verify(notificationRepository).save(notification);
        }

        @Test
        void shouldNotSaveInvalidUpdatedNotification() {

        Notification notification = new Notification(
            "Welcome",
            "Hello Anthony",
            NotificationChannel.EMAIL,
            "anthony@example.com",
            new User("anthony@example.com", "hashed-password")
        );
        UpdateNotificationRequest request =
            new UpdateNotificationRequest(
                "Invalid SMS",
                "Updated content",
                NotificationChannel.SMS,
                "invalid-number"
            );

        when(notificationRepository.findByIdAndUserEmail(1L, "anthony@example.com"))
            .thenReturn(Optional.of(notification));
        when(senderFactory.getSender(NotificationChannel.SMS))
            .thenReturn(notificationSender);
        doThrow(new InvalidNotificationException("Invalid phone number"))
            .when(notificationSender).validate(notification);

        assertThrows(
            InvalidNotificationException.class,
            () -> notificationService.update(1L, "anthony@example.com", request)
        );

        verify(notificationSender).validate(notification);
        verify(notificationSender, never()).send(any(Notification.class));
        verify(notificationRepository, never()).save(any(Notification.class));
        }
}