package io.github.bardiakz.exam_service.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerConfiguration.class);

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .slowCallRateThreshold(100.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(5))
                .recordExceptions(Exception.class)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);

        registry.circuitBreaker("notificationService").getEventPublisher()
                .onStateTransition(event -> {
                    log.info("Circuit Breaker State Transition: {} -> {}",
                            event.getStateTransition().getFromState(),
                            event.getStateTransition().getToState());
                })
                .onFailureRateExceeded(event -> {
                    log.warn("Circuit Breaker Failure Rate Exceeded: {}%", event.getFailureRate());
                })
                .onCallNotPermitted(event -> {
                    log.warn("Circuit Breaker Call Not Permitted - Circuit is OPEN");
                });

        return registry;
    }
}