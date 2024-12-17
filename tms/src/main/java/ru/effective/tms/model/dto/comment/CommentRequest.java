package ru.effective.tms.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
/**
 * Request DTO for working with entity comment.
 */
public record CommentRequest(
        @NotBlank(message = "Field content must be filled!")
        String content,
        @NotNull(message = "Field taskId must be filled!")
        Long taskId,
        String userName,
        Instant updatedTime) {
}
