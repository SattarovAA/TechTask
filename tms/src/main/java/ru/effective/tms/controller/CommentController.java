package ru.effective.tms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.effective.tms.aop.CheckUserIdPrivacy;
import ru.effective.tms.mapper.CommentMapper;
import ru.effective.tms.model.aop.EntityType;
import ru.effective.tms.model.dto.comment.CommentListResponse;
import ru.effective.tms.model.dto.comment.CommentRequest;
import ru.effective.tms.model.dto.comment.CommentResponse;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.service.CommentService;

@Tag(name = "CommentController",
        description = "Controller for working with comments.")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    /**
     * Get all comments.
     *
     * @return {@link CommentListResponse} Comment List Response.
     */
    @Operation(
            summary = "Get all comments.",
            tags = {"comment", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = CommentListResponse.class))
            }),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping()
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommentListResponse> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentMapper.modelListToModelListResponse(
                        commentService.findAll())
                );
    }

    /**
     * Get a Comment object by specifying its id.
     * The response is Comment object with
     * id, content, author userName, updatedTime, taskId.
     *
     * @param id the id of the comment to retrieve.
     * @return {@link CommentResponse} with searched id.
     */
    @Operation(
            summary = "Get comment by id.",
            description = "Get a Comment object by specifying its id. " +
                    "The response is Comment object with " +
                    "id, content, author userName, updatedTime, taskId.",
            tags = {"comment", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = CommentResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommentResponse> getById(
            @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentMapper.modelToResponse(
                                commentService.findById(id)
                        )
                );
    }

    /**
     * Create comment by specifying its id and task id.
     *
     * @param commentRequest {@link CommentRequest} to create comment.
     * @return {@link CommentResponse} by created comment.
     */
    @Operation(
            summary = "Create comment by specifying its id and task id.",
            tags = {"comment", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = CommentResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
    })
    @PostMapping()
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommentResponse> create(
            @RequestBody @Valid CommentRequest commentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentMapper.modelToResponse(
                                commentService.save(
                                        commentMapper.requestToModel(commentRequest)
                                )
                        )
                );
    }

    /**
     * Update comment by specifying its id and task id.
     * Only with admin access.
     *
     * @param id             comment id to update comment.
     * @param commentRequest {@link CommentRequest} to update comment.
     * @return {@link CommentResponse} by updated comment.
     */
    @Operation(
            summary = "Update comment by specifying its id.",
            description = "only with admin access.",
            tags = {"comment", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = CommentResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid CommentRequest commentRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentMapper.modelToResponse(
                                commentService.update(
                                        id,
                                        commentMapper.requestToModel(commentRequest)
                                )
                        )
                );
    }

    /**
     * Delete comment by specifying its id.
     * Only with admin access.
     *
     * @param id id comment to delete Comment.
     * @return {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT}.
     */
    @Operation(
            summary = "Delete comment by specifying its id.",
            description = "only with admin access.",
            tags = {"comment", "delete"})
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CheckUserIdPrivacy(entityType = EntityType.COMMENT,
            alwaysAccessRoles = RoleType.ROLE_ADMIN)
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        commentService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
