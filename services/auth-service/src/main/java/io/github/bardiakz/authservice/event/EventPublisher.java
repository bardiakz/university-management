package io.github.bardiakz.authservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
    private static final String EXCHANGE = "user.events";
    private static final String ROUTING_KEY = "user.registered";

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserRegistered(UserRegisteredEvent event) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
            log.info("Published UserRegistered event for username: {}", event.getUsername());
        } catch (Exception e) {
            log.error("Failed to publish UserRegistered event", e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }
}