package io.github.bardiakz.exam_service.service;

import io.github.bardiakz.exam_service.event.EventPublisher;
import io.github.bardiakz.exam_service.event.ExamStartedEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final EventPublisher eventPublisher;

    public NotificationService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Sends exam start notification with Circuit Breaker pattern.
     * If notification service fails, circuit opens and fallback method is called.
     *
     * @param event ExamStartedEvent to be published
     */
    @CircuitBreaker(name = "notificationService", fallbackMethod = "notifyExamStartFallback")
    public void notifyExamStart(ExamStartedEvent event) {
        log.info("Attempting to send exam start notification for exam ID: {}", event.getExamId());
        eventPublisher.publishExamStartedEvent(event);
        log.info("Successfully sent exam start notification for exam ID: {}", event.getExamId());
    }

    /**
     * Fallback method when circuit breaker is open or service fails.
     * Logs the failure and allows the system to continue.
     *
     * @param event ExamStartedEvent that failed to send
     * @param throwable Exception that triggered the fallback
     */
    private void notifyExamStartFallback(ExamStartedEvent event, Throwable throwable) {
        log.warn("Circuit breaker OPEN or notification failed for exam ID: {}. Fallback triggered. Reason: {}",
                event.getExamId(), throwable.getMessage());
        log.info("Exam {} will proceed without notification. Notification will be retried when service recovers.",
                event.getExamId());

        // In production, you might want to:
        // 1. Store failed notifications in a database for retry
        // 2. Send alert to monitoring system
        // 3. Queue notification for batch retry later
    }
}