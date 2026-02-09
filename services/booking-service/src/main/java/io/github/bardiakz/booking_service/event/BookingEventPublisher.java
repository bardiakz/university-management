package io.github.bardiakz.booking_service.event;

import io.github.bardiakz.booking_service.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BookingEventPublisher.class);
    private static final String EXCHANGE_NAME = "booking.events";

    private final RabbitTemplate rabbitTemplate;

    public BookingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBookingConfirmed(Booking booking) {
        try {
            BookingConfirmedEvent event = new BookingConfirmedEvent();
            event.setEventId(UUID.randomUUID().toString());
            event.setBookingId(booking.getId());
            event.setResourceId(booking.getResourceId());
            event.setResourceName("Resource #" + booking.getResourceId()); // TODO: Get actual name
            event.setUserId(booking.getUserId());
            event.setUserEmail(booking.getUserId() + "@university.edu"); // Construct email
            event.setStartTime(booking.getStartTime());
            event.setEndTime(booking.getEndTime());
            event.setTimestamp(LocalDateTime.now());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, "booking.confirmed", event);
            log.info("Published BookingConfirmed event for booking ID: {}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to publish BookingConfirmed event", e);
        }
    }

    public void publishBookingCancelled(Booking booking) {
        try {
            BookingCancelledEvent event = new BookingCancelledEvent();
            event.setEventId(UUID.randomUUID().toString());
            event.setBookingId(booking.getId());
            event.setUserId(booking.getUserId());
            event.setUserEmail(booking.getUserId() + "@university.edu"); // Construct email
            event.setResourceName("Resource #" + booking.getResourceId());
            event.setTimestamp(LocalDateTime.now());

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, "booking.cancelled", event);
            log.info("Published BookingCancelled event for booking ID: {}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to publish BookingCancelled event", e);
        }
    }
}