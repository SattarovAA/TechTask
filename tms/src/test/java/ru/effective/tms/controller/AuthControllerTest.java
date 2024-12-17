package ru.effective.tms.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.effective.tms.mapper.UserMapper;
import ru.effective.tms.model.dto.security.AuthResponse;
import ru.effective.tms.model.dto.security.LoginRequest;
import ru.effective.tms.model.dto.security.RefreshTokenRequest;
import ru.effective.tms.model.dto.security.RefreshTokenResponse;
import ru.effective.tms.model.dto.user.UserRequest;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.service.security.SecurityService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthControllerTest Tests")
public class AuthControllerTest {
    private final static String urlTemplate = "/api/auth";
    @MockitoBean
    private SecurityService securityService;
    @MockitoBean
    private UserMapper userMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    @DisplayName("authUser test: auth user from anonymous user.")
    void givenLoginRequestWhenSigninUrlThenAuthResponse() throws Exception {
        // Arrange
        String url = urlTemplate + "/signin";
        String requestJson = """
                {
                   "email": "email",
                   "password":"pass"
                }""";
        LoginRequest loginRequest = new LoginRequest(
                "email",
                "pass"
        );
        AuthResponse authResponse = new AuthResponse(
                1L,
                "token",
                "refreshToken",
                "user",
                Collections.singletonList(RoleType.ROLE_ADMIN.name())
        );
        when(securityService.authenticationUser(loginRequest))
                .thenReturn(authResponse);
        // Act
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(status().isOk());
        // Assert
        verify(securityService, times(1))
                .authenticationUser(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("registerUser test: new admin user from anonymous user.")
    void givenUserRequestWhenRegisterUrlThenMessage() throws Exception {
        // Arrange
        String url = urlTemplate + "/register";
        String requestJson = """
                {
                "username": "user",
                "password": "pass",
                "email": "email",
                "roles": ["ROLE_ADMIN"]
                }""";
        String responseJson = """
                {
                "message": "User created!"
                }""";
        User userToRegister = new User(
                1L,
                "user",
                "pass",
                "email",
                Collections.singleton(RoleType.ROLE_ADMIN),
                Collections.emptyList(),
                Collections.emptySet(),
                Collections.emptyList()
        );
        when(userMapper.requestToModel(any(UserRequest.class)))
                .thenReturn(userToRegister);
        when(securityService.registerNewUser(userToRegister))
                .thenReturn(userToRegister);
        // Act
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(content().json(responseJson))
                .andExpect(status().isCreated());
        // Assert
        verify(securityService, times(1))
                .registerNewUser(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("refreshToken test: refresh auth token from anonymous user.")
    void givenRefreshTokenRequestWhenRefreshTokenUrlThenRefreshTokenResponse()
            throws Exception {
        String url = urlTemplate + "/refresh-token";
        String requestJson = """
                {
                   "refreshToken": "refreshToken"
                }""";
        RefreshTokenRequest tokenRequest = new RefreshTokenRequest(
                "refreshToken"
        );
        RefreshTokenResponse tokenResponse = new RefreshTokenResponse(
                "refreshToken",
                "accessToken"
        );
        when(securityService.refreshToken(tokenRequest))
                .thenReturn(tokenResponse);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(status().isOk());

        verify(securityService, times(1))
                .refreshToken(any());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("logoutUser test: logout user from simple user.")
    void givenUserDetailsFromUserWhenLogoutUrlThenSimpleResponse()
            throws Exception {
        String url = urlTemplate + "/logout";
        String requestJson = """
                {
                   "username": "testUser",
                   "password":"pass",
                   "roles":"ROLE_USER"
                }""";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(jsonPath("$.message").isString())
                .andExpect(status().isOk());

        verify(securityService, times(1))
                .logout();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("logoutUser test: logout user from anonymous user.")
    void givenUserDetailsFromAnonymousUserWhenLogoutUrlThenStatusForbidden()
            throws Exception {
        String url = urlTemplate + "/logout";
        String requestJson = """
                {
                   "username": "testUser",
                   "password":"pass",
                   "roles":"ROLE_USER"
                }""";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isForbidden());
    }
}