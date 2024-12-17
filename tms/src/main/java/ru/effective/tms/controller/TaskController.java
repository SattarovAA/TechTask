package ru.effective.tms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effective.tms.aop.CheckUserIdPrivacy;
import ru.effective.tms.mapper.TaskMapper;
import ru.effective.tms.model.aop.EntityType;
import ru.effective.tms.model.dto.TaskFilter;
import ru.effective.tms.model.dto.comment.CommentResponse;
import ru.effective.tms.model.dto.task.TaskListResponse;
import ru.effective.tms.model.dto.task.TaskRequest;
import ru.effective.tms.model.dto.task.TaskResponse;
import ru.effective.tms.model.dto.task.TaskResponseWith;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.model.enums.Priority;
import ru.effective.tms.model.enums.Status;
import ru.effective.tms.service.TaskService;

@Tag(name = "TaskController",
        description = "Controller for working with tasks.")
@RequiredArgsConstructor
@Validated
@Slf4j
@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    /**
     * Get all tasks.
     * Only with admin access.
     *
     * @return {@link TaskListResponse} Task List Response.
     */
    @Operation(
            summary = "Get all tasks.",
            description = "Only with admin access.",
            tags = {"task", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = TaskListResponse.class))
            }),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<TaskListResponse> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper
                        .modelListToModelListResponse(taskService.findAll())
                );
    }

    /**
     * Get all tasks by {@link TaskFilter}.
     * Only with admin access.
     *
     * @return {@link TaskListResponse} Task List Response by filter.
     */
    @Operation(
            summary = "Get tasks by filter.",
            description = "Only with admin access.",
            tags = {"task", "get", "filter"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = TaskListResponse.class))
            }),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskListResponse> getAllFilterBy(
            @Valid TaskFilter filter) {
        log.info("try to get all hotels with filter");
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelListToModelListResponse(
                                taskService.filterBy(filter)
                        )
                );
    }

    /**
     * Get a Task object by specifying its id.
     * The response is Task object with
     * id, title, description, currentStatus, currentPriority,
     * authorId, list performerIds, list comments.
     *
     * @param id the id of the task to retrieve.
     * @return {@link TaskResponseWith} with searched id.
     */
    @Operation(
            summary = "Get task by id.",
            description = "Get a Task object by specifying its id. " +
                    "The response is Task object with " +
                    "id, title, description, currentStatus, currentPriority," +
                    "authorId, list performerIds, list comments.",
            tags = {"task", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = TaskResponseWith.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @CheckUserIdPrivacy(entityType = EntityType.TASK,
            alwaysAccessRoles = RoleType.ROLE_ADMIN)
    public ResponseEntity<TaskResponseWith> getById(
            @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelToResponseWith(
                                taskService.findById(id)
                        )
                );
    }

    /**
     * Create task by specifying its id and task id.
     * Only with admin access.
     *
     * @param taskRequest {@link TaskRequest} to create task.
     * @return {@link TaskResponse} by created task.
     */
    @Operation(
            summary = "Create task by specifying its id and task id.",
            description = "Only with admin access.",
            tags = {"task", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = CommentResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
    })
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> create(
            @RequestBody @Valid TaskRequest taskRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskMapper.modelToResponse(
                                taskService.save(
                                        taskMapper.requestToModel(taskRequest)
                                )
                        )
                );
    }

    /**
     * Update task by specifying its id.
     * Only with admin access.
     *
     * @param id          task id to update comment.
     * @param taskRequest {@link TaskRequest} to update task.
     * @return {@link TaskResponse} by updated task.
     */
    @Operation(
            summary = "Update task by specifying its id.",
            description = "Only with admin access.",
            tags = {"task", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = TaskResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid TaskRequest taskRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelToResponse(
                                taskService.update(id,
                                        taskMapper.requestToModel(taskRequest)
                                )
                        )
                );
    }

    /**
     * Change task priority by specifying its id and new status.
     *
     * @param id            task to change priority.
     * @param statusRequest new {@link Status} for task.
     * @return {@link TaskResponse} by updated task.
     */
    @Operation(
            summary = "Change task priority.",
            description = "By specifying its id and new priority.",
            tags = {"task", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = TaskResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @PutMapping(path = "/{id}/status")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @CheckUserIdPrivacy(entityType = EntityType.TASK,
            alwaysAccessRoles = RoleType.ROLE_ADMIN)
    public ResponseEntity<TaskResponse> changeStatus(
            @PathVariable("id") Long id,
            @RequestBody Status statusRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelToResponse(
                                taskService.update(id,
                                        taskMapper.statusToModel(statusRequest)
                                )
                        )
                );
    }

    /**
     * Change task priority by specifying its id and new priority.
     *
     * @param id              task to change priority.
     * @param priorityRequest new {@link Priority} for task.
     * @return {@link TaskResponse} by updated task.
     */
    @Operation(
            summary = "Change task priority.",
            description = "By specifying its id and new priority.",
            tags = {"task", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = TaskResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @PutMapping(path = "/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> changePriority(
            @PathVariable("id") Long id,
            @RequestBody Priority priorityRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelToResponse(
                                taskService.update(id,
                                        taskMapper.priorityToModel(priorityRequest)
                                )
                        )
                );
    }

    /**
     * Add New Performer to the task
     * by specifying its id and new performer id.
     * Only with admin access.
     *
     * @param id          task to change priority.
     * @param performerId id new performer to add.
     * @return {@link TaskResponse} by updated task.
     */
    @Operation(
            summary = "Add New Performer to the task.",
            description = "By specifying its id and new performer id.",
            tags = {"task", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = TaskResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @PostMapping(path = "/{id}/{performerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> addNewPerformer(
            @PathVariable("performerId") Long performerId,
            @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelToResponse(
                                taskService.addNewPerformer(id, performerId)
                        )
                );
    }

    /**
     * Delete task by specifying its id.
     * Only with admin access.
     *
     * @param id id task to delete Task.
     * @return {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT}.
     */
    @Operation(
            summary = "Delete task by specifying its id.",
            description = "Only with admin access.",
            tags = {"task", "delete"})
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        taskService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
