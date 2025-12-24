package io.github.bardiakz.booking_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfig {
    // Enables @Retryable annotation for optimistic locking retry
}