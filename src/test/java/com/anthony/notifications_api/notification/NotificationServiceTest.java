package com.anthony.notifications_api.notification;
import com.anthony.notifications_api.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.anthony.notifications_api.exception.ResourceNotFoundException;
import com.anthony.notifications_api.notification.sender.NotificationSenderFactory;
import com.anthony.notifications_api.user.UserRepository;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationSenderFactory senderFactory;

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
}