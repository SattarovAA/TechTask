package ru.effective.tms.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.effective.tms.exception.security.RefreshTokenException;
import ru.effective.tms.jwt.JwtUtils;
import ru.effective.tms.model.dto.security.AuthResponse;
import ru.effective.tms.model.dto.security.LoginRequest;
import ru.effective.tms.model.dto.security.RefreshTokenRequest;
import ru.effective.tms.model.dto.security.RefreshTokenResponse;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.model.entity.security.AppUserDetails;
import ru.effective.tms.model.entity.security.RefreshToken;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.service.UserService;
import ru.effective.tms.service.impl.security.RefreshTokenServiceImpl;
import ru.effective.tms.service.impl.security.SecurityServiceImpl;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("SecurityServiceImplTest Tests")
public class SecurityServiceImplTest {
    private SecurityServiceImpl securityService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private RefreshTokenServiceImpl refreshTokenService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityServiceImpl(userService,
                refreshTokenService,
                authenticationManager,
                jwtUtils);
    }

    @Test
    @DisplayName("authenticationUser test: try to authenticate " +
            "user with LoginRequest.")
    void givenCorrectLoginRequestWhenAuthenticationThenAuthResponse() {
        Long userPrincipalId = 10L;
        User defaultUser = new User(
                userPrincipalId,
                "username",
                "pass",
                "email",
                Collections.singleton(RoleType.ROLE_USER),
                Collections.emptyList(),
                Collections.emptySet(),
                Collections.emptyList()
        );
        AppUserDetails principal = new AppUserDetails(defaultUser);
        LoginRequest loginRequest = new LoginRequest(
                "username",
                "pass"
        );
        UsernamePasswordAuthenticationToken expectedToken =
                new UsernamePasswordAuthenticationToken(
                        "username",
                        loginRequest.getPassword()
                );
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        principal, "password", principal.getAuthorities()
                );
        RefreshToken refreshToken = new RefreshToken(
                1L, userPrincipalId, "token", Instant.now()
        );
        String jwtToken = "jwtToken";
        AuthResponse expected = AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .id(principal.getUserId())
                .username(principal.getUsername())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();


        when(refreshTokenService.createRefreshToken(principal.getUserId()))
                .thenReturn(refreshToken);
        when(jwtUtils.generateJwtToken(principal))
                .thenReturn(jwtToken);
        //getAuthenticationFromLoginRequest()
        when(authenticationManager.authenticate(expectedToken))
                .thenReturn(auth);
        //getAuthenticationFromLoginRequest()
        when(userService.findByEmail(anyString()))
                .thenReturn(defaultUser);
        AuthResponse actual = securityService.authenticationUser(loginRequest);

        assertEquals(expected, actual);
        verify(authenticationManager, times(1))
                .authenticate(any());
        verify(refreshTokenService, times(1))
                .createRefreshToken(any());
        verify(jwtUtils, times(1))
                .generateJwtToken(any());
    }

    @Test
    @DisplayName("registerNewUser test: send to new user data to UserService.")
    void givenUserWhenRegisterNewUserThenUser() {
        User user = new User(
                1L,
                "user",
                "pass",
                "email",
                Collections.singleton(RoleType.ROLE_USER),
                Collections.emptyList(),
                Collections.emptySet(),
                Collections.emptyList()
        );

        when(userService.save(user)).thenReturn(user);
        User actual = securityService.registerNewUser(user);

        assertEquals(user, actual);
        verify(userService, times(1))
                .save(any());
    }

    @Test
    @DisplayName("refreshToken test: update correct refreshToken.")
    void givenRefreshTokenRequestWhenRefreshTokenThenRefreshTokenResponse() {
        Long userId = 1L;
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(
                "refreshTokenRequest"
        );
        RefreshToken oldRefreshToken = new RefreshToken(
                1L, userId, "oldRefreshToken", Instant.now()
        );
        RefreshToken updatedRefreshToken = new RefreshToken(
                2L, userId, "updatedRefreshToken", Instant.now()
        );
        User user = new User(
                userId,
                "user",
                "pass",
                "email",
                Collections.singleton(RoleType.ROLE_USER),
                Collections.emptyList(),
                Collections.emptySet(),
                Collections.emptyList()
        );
        String jwtToken = "jwtToken";

        when(refreshTokenService.findByRefreshToken("refreshTokenRequest"))
                .thenReturn(Optional.of(oldRefreshToken));
        when(refreshTokenService.checkRefreshToken(oldRefreshToken))
                .thenReturn(oldRefreshToken);
        when(userService.findById(userId))
                .thenReturn(user);
        when(jwtUtils.generateTokenFromUsername("user"))
                .thenReturn(jwtToken);
        when(refreshTokenService.createRefreshToken(userId))
                .thenReturn(updatedRefreshToken);

        RefreshTokenResponse expected = new RefreshTokenResponse(
                jwtToken,
                "updatedRefreshToken"
        );
        RefreshTokenResponse actual =
                securityService.refreshToken(refreshTokenRequest);

        assertEquals(expected, actual);
        verify(refreshTokenService, times(1))
                .findByRefreshToken(any());
        verify(refreshTokenService, times(1))
                .checkRefreshToken(any());
        verify(userService, times(1))
                .findById(any());
        verify(jwtUtils, times(1))
                .generateTokenFromUsername(any());
        verify(refreshTokenService, times(1))
                .createRefreshToken(any());
    }

    @Test
    @DisplayName("refreshToken test: throw when refreshToken not found.")
    void givenRefreshTokenRequestWhenRefreshTokenThenRefreshThrow() {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(
                "refreshTokenRequest"
        );

        when(refreshTokenService.findByRefreshToken("refreshTokenRequest"))
                .thenReturn(Optional.empty());

        assertThrows(RefreshTokenException.class,
                () -> securityService.refreshToken(refreshTokenRequest),
                "refreshToken not found."
        );
        verify(refreshTokenService, times(1))
                .findByRefreshToken(any());
        verify(refreshTokenService, times(0))
                .checkRefreshToken(any());
        verify(userService, times(0))
                .findById(any());
        verify(jwtUtils, times(0))
                .generateTokenFromUsername(any());
        verify(refreshTokenService, times(0))
                .createRefreshToken(any());
    }

    @Test
    @DisplayName("logout test: delete correct refreshToken by userId.")
    void givenCorrectPrincipalWhenLogoutThenCallDeleteRefreshToken() {
        Long userPrincipalId = 1L;
        AppUserDetails principal =
                new AppUserDetails(new User(
                        userPrincipalId,
                        "username",
                        "pass",
                        "email",
                        Collections.singleton(RoleType.ROLE_USER),
                        Collections.emptyList(),
                        Collections.emptySet(),
                        Collections.emptyList()
                ));
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        principal, "password", principal.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);

        securityService.logout();

        verify(refreshTokenService, times(1))
                .deleteByUserId(userPrincipalId);
    }
}
