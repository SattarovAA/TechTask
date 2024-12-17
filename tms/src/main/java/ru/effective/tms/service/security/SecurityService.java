package ru.effective.tms.service.security;

import ru.effective.tms.model.dto.security.AuthResponse;
import ru.effective.tms.model.dto.security.LoginRequest;
import ru.effective.tms.model.dto.security.RefreshTokenRequest;
import ru.effective.tms.model.dto.security.RefreshTokenResponse;
import ru.effective.tms.model.entity.User;

/**
 * Service interface for work with new {@link User}.
 */
public interface SecurityService {
    /**
     * Registration new {@link User}.
     *
     * @param user {@link User} for registration.
     * @return registered {@link User}.
     */
    User registerNewUser(User user);

    /**
     * Logout from system.
     * Delete token in system by user Id.
     */
    void logout();

    /**
     * get {@link RefreshTokenResponse} with updated token
     * from {@link RefreshTokenResponse} with correct refreshToken.
     *
     * @param request {@link RefreshTokenResponse} with refreshToken.
     * @return {@link RefreshTokenResponse} with updated token.
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    /**
     * Authentication user in system by email and password.
     *
     * @param loginRequest {@link LoginRequest} to authenticate.
     * @return {@link AuthResponse} with Authentication information.
     */
    AuthResponse authenticationUser(LoginRequest loginRequest);
}
