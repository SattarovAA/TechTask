package ru.effective.tms.model.dto.comment;

import java.time.Instant;
/**
 * Response DTO for working with entity comment.
 */
public record CommentResponse(
        Long id,
        String content,
        String userName,
        Instant updatedTime,
        Long taskId) {
}
