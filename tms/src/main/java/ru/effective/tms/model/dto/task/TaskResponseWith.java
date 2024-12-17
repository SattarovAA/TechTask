package ru.effective.tms.model.dto.task;

import ru.effective.tms.model.dto.comment.CommentResponse;
import ru.effective.tms.model.enums.Priority;
import ru.effective.tms.model.enums.Status;

import java.util.List;

/**
 * Response DTO for working with entity task.
 * Have CommentResponse list.
 */
public record TaskResponseWith(
        Long id,
        String title,
        String description,
        Status currentStatus,
        Priority currentPriority,
        Long authorId,
        List<Long> performerIds,
        List<CommentResponse> comments
) {
}
