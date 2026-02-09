package io.github.bardiakz.notification_service.repository;

import io.github.bardiakz.notification_service.entity.Notification;
import io.github.bardiakz.notification_service.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(String userId);  // Changed from Long to String

    List<Notification> findByRecipientEmail(String email);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries);

    Long countByStatus(NotificationStatus status);
}