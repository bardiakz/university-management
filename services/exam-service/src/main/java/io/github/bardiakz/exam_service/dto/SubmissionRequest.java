package io.github.bardiakz.exam_service.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class SubmissionRequest {

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Answers are required")
    @Size(min = 1, message = "Submission must have at least one answer")
    private List<AnswerDto> answers;
}