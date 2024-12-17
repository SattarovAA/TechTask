package ru.effective.tms.model.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * List Response DTO for working with entity task.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskListResponse {
    List<TaskResponse> tasks = new ArrayList<>();
}
