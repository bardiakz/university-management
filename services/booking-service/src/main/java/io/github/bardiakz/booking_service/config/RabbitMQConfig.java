package io.github.bardiakz.booking_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Booking events exchange (only declare the exchange - no queues/bindings needed here)
    public static final String BOOKING_EXCHANGE = "booking.events";

    // Listen to Resource Service events
    public static final String RESOURCE_EXCHANGE = "resource.events";
    public static final String BOOKING_RESOURCE_STATUS_QUEUE = "booking.resource.status.queue";

    // Only declare the exchange used for publishing (optional but harmless)
    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE);
    }

    // Resource Exchange (listen to Resource Service) - KEEP THESE
    @Bean
    public TopicExchange resourceExchange() {
        return new TopicExchange(RESOURCE_EXCHANGE);
    }

    @Bean
    public Queue bookingResourceStatusQueue() {
        return new Queue(BOOKING_RESOURCE_STATUS_QUEUE, true);
    }

    @Bean
    public Binding bookingResourceStatusBinding(Queue bookingResourceStatusQueue, TopicExchange resourceExchange) {
        return BindingBuilder.bind(bookingResourceStatusQueue)
                .to(resourceExchange)
                .with("resource.status.changed");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}