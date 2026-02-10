package io.github.bardiakz.exam_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key.exam.started}")
    private String examStartedRoutingKey;

    @Value("${rabbitmq.routing.key.exam.created}")
    private String examCreatedRoutingKey;

    @Value("${rabbitmq.routing.key.exam.submitted}")
    private String examSubmittedRoutingKey;

    @Value("${rabbitmq.routing.key.exam.graded}")
    private String examGradedRoutingKey;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishExamStartedEvent(ExamStartedEvent event) {
        publishEvent(examStartedRoutingKey, event);
    }

    public void publishExamCreatedEvent(ExamCreatedEvent event) {
        publishEvent(examCreatedRoutingKey, event);
    }

    public void publishExamSubmittedEvent(ExamSubmittedEvent event) {
        publishEvent(examSubmittedRoutingKey, event);
    }

    public void publishExamGradedEvent(ExamGradedEvent event) {
        publishEvent(examGradedRoutingKey, event);
    }

    private void publishEvent(String routingKey, Object event) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
            log.info("Published {} to key {}", event.getClass().getSimpleName(), routingKey);
        } catch (Exception e) {
            log.error("Failed to publish {}: {}", event.getClass().getSimpleName(), e.getMessage(), e);
        }
    }
}
