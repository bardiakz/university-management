package com.university.contract.notification;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class NotificationEvent {

    public String specVersion;
    public String eventId;
    public Domain domain;
    public String eventType;
    public Instant occurredAt;
    public String correlationId;
    public Producer producer;
    public Recipient recipient;
    public List<Channel> channelHints;
    public Priority priority;
    public String idempotencyKey;
    public Map<String, Object> payload;

    public enum Domain {
        USER_AUTH, MARKETETPLACE_ORDERS, BOOKING_RESOURCES, PAYMENTS, EXAMS, SYSTEM_ADMIN, FAILURE_EXCEPTION_INTERNAL
    }

    public enum Channel { EMAIL, IN_APP, PUSH }

    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    public static class Producer {
        public String service;
        public String host;
    }

    public static class Recipient {
        public Mode mode;
        public String userId;
        public String email;

        public enum Mode { USER, BROADCAST }
    }
}
