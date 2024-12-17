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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effective.tms.aop.CheckUserIdPrivacy;
import ru.effective.tms.mapper.UserMapper;
import ru.effective.tms.model.aop.EntityType;
import ru.effective.tms.model.dto.user.UserListResponse;
import ru.effective.tms.model.dto.user.UserRequest;
import ru.effective.tms.model.dto.user.UserResponse;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.service.UserService;

@Tag(name = "UserController", description = "Controller for working with users.")
@RequiredArgsConstructor
@Validated
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Get all users.
     * Only with admin access.
     *
     * @return {@link UserListResponse} User List Response.
     */
    @Operation(
            summary = "Get all users.",
            description = "only with admin access.",
            tags = {"user", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = UserListResponse.class))
            }),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403", description = "only admin access.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<UserListResponse> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userMapper.modelListToModelListResponse(
                        userService.findAll())
                );
    }

    /**
     * Get a User object by specifying its id.
     * The response is User object with
     * id, username, encoded password, email, roles,
     * list createdTask, list takenTask, list comments.
     *
     * @param id the id of the user to retrieve.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}
     * and {@link UserResponse} with searched id.
     */
    @Operation(
            summary = "Get user by id.",
            description = "Get a User object by specifying its id. " +
                    "The response is User object with " +
                    "id, username, encoded password, email, roles, " +
                    "list createdTask, list takenTask, list comments.",
            tags = {"user", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = UserResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @CheckUserIdPrivacy(entityType = EntityType.USER,
            alwaysAccessRoles = RoleType.ROLE_ADMIN)
    @GetMapping(path = "/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable("id") Long id) {
        log.info("Try to get user with id " + id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userMapper.modelToResponse(
                                userService.findById(id)
                        )
                );
    }

    /**
     * Update user by specifying its id and {@link UserRequest}.
     * Only with admin access.
     *
     * @param id           user id to update user.
     * @param modelRequest {@link UserRequest} to update user.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}
     * and {@link UserResponse} by updated user.
     */
    @Operation(
            summary = "Update user by specifying its id.",
            description = "only with admin access.",
            tags = {"user", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = UserResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @CheckUserIdPrivacy(entityType = EntityType.USER,
            alwaysAccessRoles = RoleType.ROLE_ADMIN)
    @PutMapping(path = "/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserRequest modelRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userMapper.modelToResponse(
                                userService.update(
                                        id,
                                        userMapper.requestToModel(modelRequest)
                                )
                        )
                );
    }

    /**
     * Delete user by specifying its id.
     * Only with admin access.
     *
     * @param id id user to delete User.
     * @return {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT}.
     */
    @Operation(
            summary = "Delete user by specifying its id.",
            description = "only with admin access.",
            tags = {"user", "delete"})
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @CheckUserIdPrivacy(entityType = EntityType.USER,
            alwaysAccessRoles = RoleType.ROLE_ADMIN)
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

