package io.github.bardiakz.exam_service.dto;

import io.github.bardiakz.exam_service.entity.Exam;

import java.time.LocalDateTime;
import java.util.List;

public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private String instructorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Exam.ExamStatus status;
    private List<QuestionDto> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}