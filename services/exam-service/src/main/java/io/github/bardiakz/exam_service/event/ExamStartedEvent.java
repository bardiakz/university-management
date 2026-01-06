package io.github.bardiakz.exam_service.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ExamStartedEvent(@JsonProperty("event_type") String eventType, @JsonProperty("exam_id") Long examId,
                               @JsonProperty("title") String title, @JsonProperty("instructor_id") String instructorId,
                               @JsonProperty("start_time") LocalDateTime startTime,
                               @JsonProperty("duration_minutes") Integer durationMinutes,
                               @JsonProperty("total_marks") Integer totalMarks,
                               @JsonProperty("timestamp") LocalDateTime timestamp) {

    // All-args constructor
    public ExamStartedEvent(String eventType, Long examId, String title, String instructorId,
                            LocalDateTime startTime, Integer durationMinutes, Integer totalMarks,
                            LocalDateTime timestamp) {
        this.eventType = eventType;
        this.examId = examId;
        this.title = title;
        this.instructorId = instructorId;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.timestamp = timestamp;
    }

    // Getters
    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public Long examId() {
        return examId;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String instructorId() {
        return instructorId;
    }

    @Override
    public LocalDateTime startTime() {
        return startTime;
    }

    @Override
    public Integer durationMinutes() {
        return durationMinutes;
    }

    @Override
    public Integer totalMarks() {
        return totalMarks;
    }

    @Override
    public LocalDateTime timestamp() {
        return timestamp;
    }
}