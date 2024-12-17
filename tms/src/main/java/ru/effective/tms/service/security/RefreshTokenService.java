package ru.effective.tms.service.security;

import ru.effective.tms.model.entity.security.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    /**
     * Find {@link RefreshToken} by string value.
     *
     * @param token to search {@link RefreshToken}.
     * @return searhed {@link RefreshToken} if exists.
     */
    Optional<RefreshToken> findByRefreshToken(String token);

    /**
     * Create new {@link RefreshToken} by user id.
     *
     * @param userId user to create token.
     * @return created {@link RefreshToken}.
     */
    RefreshToken createRefreshToken(Long userId);

    /**
     * Check {@link RefreshToken} to expiry.
     *
     * @param token token to check
     * @return correct {@link RefreshToken}.
     */
    RefreshToken checkRefreshToken(RefreshToken token);

    /**
     * Delete all tokens with userId.
     *
     * @param userId user id to delete tokens.
     */
    void deleteByUserId(Long userId);

    /**
     * Check token by user id.
     *
     * @param userId id to check.
     * @return true if user with id exists.
     */
    boolean checkByUserId(Long userId);
}
