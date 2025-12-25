package com.university.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "notifications.x";

    @Bean
    TopicExchange notificationsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue qUserAuth() {
        return QueueBuilder.durable("q.notifications.user_auth").build();
    }

    @Bean
    Binding bUserAuth(TopicExchange notificationsExchange, Queue qUserAuth) {
        return BindingBuilder.bind(qUserAuth).to(notificationsExchange).with("user_auth.*");
    }
}
