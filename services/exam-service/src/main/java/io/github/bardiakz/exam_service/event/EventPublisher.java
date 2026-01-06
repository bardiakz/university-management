package io.github.bardiakz.exam_service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key.exam.started}")
    private String examStartedRoutingKey;

    public EventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishExamStartedEvent(ExamStartedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(exchangeName, examStartedRoutingKey, eventJson);
            log.info("Published ExamStartedEvent for exam ID: {}", event.examId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ExamStartedEvent: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to publish ExamStartedEvent: {}", e.getMessage(), e);
        }
    }
}