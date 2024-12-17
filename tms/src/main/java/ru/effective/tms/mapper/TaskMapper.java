package ru.effective.tms.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.effective.tms.model.dto.task.TaskListResponse;
import ru.effective.tms.model.dto.task.TaskRequest;
import ru.effective.tms.model.dto.task.TaskResponse;
import ru.effective.tms.model.dto.task.TaskResponseWith;
import ru.effective.tms.model.entity.Task;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.model.enums.Priority;
import ru.effective.tms.model.enums.Status;

import java.util.List;

/**
 * Mapper for working with {@link Task} entity.
 *
 * @see TaskResponse
 * @see TaskRequest
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = CommentMapper.class)
public interface TaskMapper {
    /**
     * {@link TaskRequest} to {@link Task} mapping.
     *
     * @param request {@link TaskRequest} for mapping.
     * @return mapped {@link Task}.
     */
    @Mapping(source = "request.authorId", target = "author.id")
    Task requestToModel(TaskRequest request);

    /**
     * Map user by id to Long value.
     *
     * @param user user entity from {@link Task}.
     * @return user id Long value.
     */
    @Named("userToLong")
    default Long userToLong(User user) {
        return user.getId();
    }

    /**
     * Map {@link Status} to {@link Task} with only status filled field.
     *
     * @param currentStatus status value for mapping.
     * @return {@link Task} with only {@link Status} filled field.
     */
    Task statusToModel(Status currentStatus);
    /**
     * Map {@link Priority} to {@link Task} with only status filled field.
     *
     * @param currentPriority priority value for mapping.
     * @return {@link Task} with only {@link Priority} filled field.
     */
    Task priorityToModel(Priority currentPriority);

    /**
     * {@link Task} to {@link TaskResponse} mapping.
     *
     * @param model {@link Task} for mapping.
     * @return mapped {@link TaskResponse}.
     */
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "performerList", target = "performerIds",
            qualifiedByName = "userToLong")
    TaskResponse modelToResponse(Task model);

    /**
     * {@link Task} to {@link TaskResponseWith} mapping.
     *
     * @param model {@link Task} for mapping.
     * @return mapped {@link TaskResponseWith}.
     */
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "performerList", target = "performerIds",
            qualifiedByName = "userToLong")
    TaskResponseWith modelToResponseWith(Task model);

    /**
     * List of {@link Task} to List of {@link TaskResponse} mapping.
     *
     * @param models List of {@link Task} for mapping.
     * @return mapped List of {@link TaskResponse}.
     */
    List<TaskResponse> modelListToModelResponseList(List<Task> models);

    /**
     * List of {@link Task} to {@link TaskListResponse} mapping.
     *
     * @param models List of {@link Task} for mapping.
     * @return mapped {@link TaskListResponse}.
     * @see #modelListToModelResponseList(List)
     */
    default TaskListResponse modelListToModelListResponse(List<Task> models) {
        TaskListResponse response = new TaskListResponse();
        response.setTasks(modelListToModelResponseList(models));
        return response;
    }
}
