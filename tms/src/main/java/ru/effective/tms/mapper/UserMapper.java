package ru.effective.tms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.effective.tms.model.dto.user.UserListResponse;
import ru.effective.tms.model.dto.user.UserRequest;
import ru.effective.tms.model.dto.user.UserResponse;
import ru.effective.tms.model.entity.User;

import java.util.List;

/**
 * Mapper for working with {@link User} entity.
 *
 * @see UserResponse
 * @see UserRequest
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    /**
     * {@link UserRequest} to {@link User} mapping.
     *
     * @param request {@link UserRequest} for mapping.
     * @return mapped {@link User}.
     */
    User requestToModel(UserRequest request);

    /**
     * {@link User} to {@link UserResponse} mapping.
     *
     * @param model {@link User} for mapping.
     * @return mapped {@link UserResponse}.
     */
    UserResponse modelToResponse(User model);

    /**
     * List of {@link User} to List of {@link UserResponse} mapping.
     *
     * @param models List of {@link User} for mapping.
     * @return mapped List of {@link UserResponse}.
     */
    List<UserResponse> modelListToModelResponseList(List<User> models);

    /**
     * List of {@link User} to {@link UserListResponse} mapping.
     *
     * @param models List of {@link User} for mapping.
     * @return mapped {@link UserListResponse}.
     * @see #modelListToModelResponseList(List)
     */
    default UserListResponse modelListToModelListResponse(List<User> models) {
        UserListResponse response = new UserListResponse();
        response.setUsers(modelListToModelResponseList(models));
        return response;
    }
}
