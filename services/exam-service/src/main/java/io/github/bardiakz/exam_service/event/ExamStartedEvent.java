package io.github.bardiakz.exam_service.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ExamStartedEvent {

    @JsonProperty("event_type")
    private String eventType = "ExamStarted";

    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("instructor_id")
    private String instructorId;

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("duration_minutes")
    private Integer durationMinutes;

    @JsonProperty("total_marks")
    private Integer totalMarks;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();
}