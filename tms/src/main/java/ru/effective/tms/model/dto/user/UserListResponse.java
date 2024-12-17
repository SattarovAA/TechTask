package ru.effective.tms.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * List Response DTO for working with entity user.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserListResponse {
    /**
     * list of {@link UserResponse}.
     */
    List<UserResponse> users = new ArrayList<>();
}
