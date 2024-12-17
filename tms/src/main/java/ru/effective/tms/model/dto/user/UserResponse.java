package ru.effective.tms.model.dto.user;

import ru.effective.tms.model.entity.Comment;
import ru.effective.tms.model.entity.Task;
import ru.effective.tms.model.entity.security.RoleType;

import java.util.List;
import java.util.Set;

/**
 * Response DTO for working with entity user.
 *
 * @param id       user id.
 * @param username user username.
 * @param password user password.
 * @param email    user email.
 * @param roles    authentication {@link RoleType}.
 */
public record UserResponse(
        Long id,
        String username,
        String password,
        String email,
        Set<RoleType> roles
        ,
        List<Task> createdTask,
        Set<Task> takenTask,
        List<Comment> comments
) {
}