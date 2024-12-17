package ru.effective.tms.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.effective.tms.model.entity.security.RoleType;

import java.util.Set;

/**
 * Request DTO for working with entity user.
 *
 * @param username user username.
 * @param password user password.
 * @param email    user email.
 * @param roles    authentication {@link RoleType}.
 */
public record UserRequest(

        @NotBlank(message = "Field username must be filled!")
        String username,

        @NotBlank(message = "Field password must be filled!")
        String password,

        @NotBlank(message = "Field email must be filled!")
        String email,
        @NotEmpty(message = "Field roles must be filled!")
        @Size(min = 1)
        Set<RoleType> roles
) {
}
