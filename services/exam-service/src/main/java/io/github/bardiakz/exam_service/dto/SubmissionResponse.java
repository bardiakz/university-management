package io.github.bardiakz.exam_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SubmissionResponse {
    private Long id;
    private Long examId;
    private String studentId;
    private List<AnswerDto> answers;
    private LocalDateTime submittedAt;
    private String status;
    private Integer totalScore;
    private Integer obtainedScore;
    private LocalDateTime gradedAt;
    private String feedback;
}