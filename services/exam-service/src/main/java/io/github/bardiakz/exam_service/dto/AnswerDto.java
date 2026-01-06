package io.github.bardiakz.exam_service.dto;

import jakarta.validation.constraints.*;

public class AnswerDto {
    private Long id;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Answer text is required")
    private String answerText;

    private Boolean isCorrect;
    private Integer marksAwarded;
}