package ru.effective.tms.service;

import ru.effective.tms.model.dto.TaskFilter;
import ru.effective.tms.model.entity.Task;

import java.util.List;

public interface TaskService extends CrudService<Task> {
    /**
     * Get all {@link Task} by {@link TaskFilter}.
     *
     * @param filter filter for selection task with required parameters.
     * @return {@link Task} list with required parameters.
     */
    List<Task> filterBy(TaskFilter filter);

    /**
     * Add new performer to {@link Task} with id to performer task list.
     *
     * @param id          {{@link Task} to update.
     * @param performerId user id to add in the performer task list.
     * @return {@link Task} with updated performer list.
     */
    Task addNewPerformer(Long id, Long performerId);

    /**
     * Check {@link Task} existence by id.
     *
     * @param id id to find.
     */
    void existsById(Long id);
}
