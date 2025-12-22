package com.university.exam.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class NotificationClient {

    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "notificationService", fallbackMethod = "fallback")
    public void sendExamNotification(String message) {
        restTemplate.postForObject(
            "http://notification-service:8088/api/notifications",
            message,
            Void.class
        );
    }

    public void fallback(String message, Throwable t) {
        System.out.println("Notification Service is DOWN. Message not sent.");
    }
}
