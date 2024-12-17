package ru.effective.tms.model.dto.task;

import ru.effective.tms.model.enums.Priority;
import ru.effective.tms.model.enums.Status;

/**
 * Request DTO for working with entity task.
 */
public record TaskRequest(
        String title,
        String description,
        Status currentStatus,
        Priority currentPriority,
        Long authorId
) {
}
