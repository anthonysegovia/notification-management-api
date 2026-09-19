package com.anthony.notifications_api.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserEmail(String email);

    Optional<Notification> findByIdAndUserEmail(
            Long id,
            String email
    );
}