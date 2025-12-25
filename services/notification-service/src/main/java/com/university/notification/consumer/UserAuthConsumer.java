package com.university.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserAuthConsumer {
    @RabbitListener(queues = "q.notifications.user_auth")
    public void consume(String message) {
        System.out.println("[NOTIFICATION][USER_AUTH] " + message);
    }
}
