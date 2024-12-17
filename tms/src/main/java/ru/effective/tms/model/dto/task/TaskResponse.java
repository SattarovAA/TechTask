package ru.effective.tms.model.dto.task;

import ru.effective.tms.model.enums.Priority;
import ru.effective.tms.model.enums.Status;

import java.util.List;

/**
 * Response DTO for working with entity task.
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        Status currentStatus,
        Priority currentPriority,
        Long authorId,
        List<Long> performerIds
) {
}
