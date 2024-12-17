package ru.effective.tms.service.impl.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.effective.tms.exception.security.RefreshTokenException;
import ru.effective.tms.jwt.JwtUtils;
import ru.effective.tms.model.dto.security.AuthResponse;
import ru.effective.tms.model.dto.security.LoginRequest;
import ru.effective.tms.model.dto.security.RefreshTokenRequest;
import ru.effective.tms.model.dto.security.RefreshTokenResponse;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.model.entity.security.AppUserDetails;
import ru.effective.tms.model.entity.security.RefreshToken;
import ru.effective.tms.service.UserService;
import ru.effective.tms.service.security.RefreshTokenService;
import ru.effective.tms.service.security.SecurityService;

import java.util.List;

/**
 * Service for work with new {@link User}.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {
    /**
     * Service for work with {@link User} entity.
     */
    private final UserService userService;
    /**
     * Service for work with {@link RefreshToken} entity.
     */
    private final RefreshTokenService refreshTokenService;
    /**
     * To authentication user by password and username.
     *
     * @see #getAuthenticationFromLoginRequest(LoginRequest)
     */
    private final AuthenticationManager authenticationManager;
    /**
     * To generate jwt token.
     */
    private final JwtUtils jwtUtils;

    public AuthResponse authenticationUser(LoginRequest loginRequest) {
        Authentication authentication =
                getAuthenticationFromLoginRequest(loginRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDetails userDetails =
                (AppUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(userDetails.getUserId());

        return AuthResponse.builder()
                .token(jwtUtils.generateJwtToken(userDetails))
                .refreshToken(refreshToken.getToken())
                .id(userDetails.getUserId())
                .username(userDetails.getUsername())
                .roles(roles)
                .build();
    }

    private Authentication getAuthenticationFromLoginRequest(LoginRequest loginRequest) {
        String username = userService
                .findByEmail(loginRequest.getEmail())
                .getUsername();
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        loginRequest.getPassword()
                )
        );
    }

    public User registerNewUser(User user) {
        return userService.save(user);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenRequest = request.getRefreshToken();
        return refreshTokenService.findByRefreshToken(refreshTokenRequest)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User tokenOwner = userService.findById(userId);
                    String token = jwtUtils.generateTokenFromUsername(tokenOwner.getUsername());
                    return new RefreshTokenResponse(
                            token,
                            refreshTokenService.createRefreshToken(userId).getToken()
                    );
                }).orElseThrow(() -> new RefreshTokenException(refreshTokenRequest,
                        "refresh token not found"));
    }

    public void logout() {
        var currentPrincipal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (currentPrincipal instanceof AppUserDetails userDetails) {
            Long userId = userDetails.getUserId();
            refreshTokenService.deleteByUserId(userId);
        }
    }
}
