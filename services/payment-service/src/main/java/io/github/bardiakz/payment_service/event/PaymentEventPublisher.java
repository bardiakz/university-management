package io.github.bardiakz.payment_service.event;

import io.github.bardiakz.payment_service.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);
    private static final String EXCHANGE_NAME = "payment.events";

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publish PaymentCompleted event - Saga T2 success
     * Marketplace will listen and mark order as COMPLETED
     */
    public void publishPaymentCompleted(Payment payment) {
        try {
            PaymentCompletedEvent event = new PaymentCompletedEvent();
            event.setEventId(UUID.randomUUID().toString());
            event.setPaymentId(payment.getId());
            event.setOrderId(payment.getOrderId());
            event.setUserId(payment.getUserId());
            event.setUserEmail(payment.getUserId() + "@university.edu"); // Construct email
            event.setAmount(payment.getAmount());
            event.setTransactionId(payment.getTransactionId());
            event.setTimestamp(LocalDateTime.now());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, "payment.completed", event);
            log.info("Published PaymentCompleted event for order: {}", payment.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish PaymentCompleted event", e);
        }
    }

    /**
     * Publish PaymentFailed event - Saga compensation trigger
     * Marketplace will listen and restore stock (C1)
     */
    public void publishPaymentFailed(Payment payment) {
        try {
            PaymentFailedEvent event = new PaymentFailedEvent();
            event.setEventId(UUID.randomUUID().toString());
            event.setOrderId(payment.getOrderId());
            event.setUserId(payment.getUserId());
            event.setUserEmail(payment.getUserId() + "@university.edu"); // Construct email
            event.setReason(payment.getFailureReason());
            event.setTimestamp(LocalDateTime.now());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, "payment.failed", event);
            log.info("Published PaymentFailed event for order: {} - Triggering compensation",
                    payment.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish PaymentFailed event", e);
        }
    }
}