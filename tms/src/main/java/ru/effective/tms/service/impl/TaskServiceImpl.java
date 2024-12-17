package ru.effective.tms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.effective.tms.exception.DeleteEntityWithReferenceException;
import ru.effective.tms.exception.EntityNotFoundException;
import ru.effective.tms.model.dto.TaskFilter;
import ru.effective.tms.model.entity.Task;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.repository.TaskRepository;
import ru.effective.tms.repository.specification.TaskSpecifications;
import ru.effective.tms.service.TaskService;
import ru.effective.tms.service.UserService;

import java.text.MessageFormat;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    /**
     * {@link Task} Repository.
     */
    private final TaskRepository taskRepository;
    /**
     * Service for working with users.
     */
    private final UserService userService;
    /**
     * Default page size.
     *
     * @see #findAll()
     */
    @Value("${app.service.task.defaultPageSize}")
    private int defaultPageSize = 10;
    /**
     * Default page number.
     *
     * @see #findAll()
     */
    @Value("${app.service.task.defaultPageNumber}")
    private int defaultPageNumber;

    @Override
    public List<Task> findAll() {
        log.info("Try to get all tasks without filter.");
        return taskRepository.findAll(
                PageRequest.of(defaultPageNumber, defaultPageSize)
        ).getContent();
    }

    @Override
    public List<Task> filterBy(TaskFilter filter) {
        log.info("Try to get all tasks with filter.");
        return taskRepository.findAll(
                TaskSpecifications.withFilter(filter),
                PageRequest.of(filter.getPageNumber(), filter.getPageSize())
        ).getContent();
    }

    @Override
    public Task findById(Long id) {
        log.info("find task with id {}.", id);
        return taskRepository.findById(id).orElseThrow(
                EntityNotFoundException.create(
                        MessageFormat.format("Task with id {0} not found!", id)
                )
        );
    }

    @Override
    public Task save(Task model) {
        log.warn("Try to create new task.");
        userService.existsById(model.getAuthor().getId());
        return taskRepository.save(model);
    }

    @Override
    public Task update(Long id, Task model) {
        log.warn("Try to update task with id: {}.", id);
        Task updatedModel = insert(id, model);
        return taskRepository.save(updatedModel);
    }
    /**
     * Update model to full version.
     * If the model has no field values, then the values are taken
     * from a previously existing entity with the same id.
     *
     * @param model   {@link Task} with partially updated fields.
     * @param modelId user id to update {@link Task}.
     * @return Updated {@link Task}.
     */
    private Task insert(Long modelId, Task model) {
        log.warn("insert in model with id: {}.", modelId);
        Task modelToUpdate = findById(modelId);

        return Task.builder()
                .id(modelId)
                .title(model.getTitle() == null
                        ? modelToUpdate.getTitle()
                        : model.getTitle()
                )
                .description(model.getDescription() == null
                        ? modelToUpdate.getDescription()
                        : model.getDescription()
                )
                .currentStatus(model.getCurrentStatus() == null
                        ? modelToUpdate.getCurrentStatus()
                        : model.getCurrentStatus()
                )
                .currentPriority(model.getCurrentPriority() == null
                        ? modelToUpdate.getCurrentPriority()
                        : model.getCurrentPriority()
                )
                .author(model.getAuthor() == null
                        ? modelToUpdate.getAuthor()
                        : model.getAuthor()
                )
                .performerList(model.getPerformerList() == null
                        ? modelToUpdate.getPerformerList()
                        : model.getPerformerList()
                )
                .build();
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Try to delete task with id {}.", id);
        Task taskToDelete = findById(id);
        checkTaskCommentsReference(taskToDelete);
        taskRepository.deleteById(id);
    }

    /**
     * Check task reference by id.
     *
     * @param model {@link Task} to check.
     * @throws EntityNotFoundException if {@link Task#getComments()} not empty.
     */
    private void checkTaskCommentsReference(Task model) {
        if (!model.getComments().isEmpty()) {
            throw new DeleteEntityWithReferenceException(
                    MessageFormat.format(
                            "Unable to delete task with id {0}. Task have {1} comments.",
                            model.getId(),
                            model.getComments().size()
                    )
            );
        }
    }

    /**
     * Add performer id to add into performerList.
     *
     * @param id          task id to update.
     * @param performerId performer id to add into performerList.
     * @return {@link Task} with updated performerList.
     * @see #insert
     */
    @Override
    public Task addNewPerformer(Long id, Long performerId) {
        Task modelToUpdate = findById(id);
        User performerForTask = userService.findById(performerId);
        log.warn("add task with id {} to the takenTask in User with id {}.",
                id,
                performerId
        );
        performerForTask.getTakenTask().add(modelToUpdate);
        modelToUpdate.getPerformerList().add(performerForTask);
        return update(id, modelToUpdate);
    }

    /**
     * Check task existence by id.
     *
     * @param id id to find.
     * @throws EntityNotFoundException if {@link Task#getComments()} not found.
     */
    @Override
    public void existsById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    MessageFormat.format("Task with id {0} not found!",
                            id
                    )
            );
        }
    }
}
