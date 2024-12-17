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
import ru.effective.tms.model.dto.user.UserListResponse;
import ru.effective.tms.model.dto.user.UserRequest;
import ru.effective.tms.model.dto.user.UserResponse;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.model.entity.security.AppUserDetails;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserControllerTest Tests")
public class UserControllerTest {
    private final static String urlTemplate = "/api/user";
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserMapper userMapper;
    @Autowired
    private MockMvc mockMvc;
    private static final String getAllUrl = urlTemplate;
    private static final String getByIdUrl = urlTemplate + "/1";
    private static final String updateUrl = urlTemplate + "/1";
    private static final String deleteUrl = urlTemplate + "/1";
    private static final String SIMPLE_REQUEST_USER_JSON = """
            {
               "username": "user",
               "password": "pass",
               "email" : "email",
               "roles": ["ROLE_USER"]
            }""";
    private static final String ADMIN_REQUEST_USER_JSON = """
            {
               "username": "user",
               "password": "pass",
               "email" : "email",
               "roles": ["ROLE_ADMIN"]
            }""";
    private static final UserResponse simpleUserResponseJson = new UserResponse(
            1L,
            "user",
            "pass",
            "email",
            Collections.singleton(RoleType.ROLE_USER),
            Collections.emptyList(),
            Collections.emptySet(),
            Collections.emptyList()
    );
    private static final UserResponse adminUserResponse = new UserResponse(
            1L,
            "user",
            "pass",
            "email",
            Collections.singleton(RoleType.ROLE_ADMIN),
            Collections.emptyList(),
            Collections.emptySet(),
            Collections.emptyList()
    );
    private static final User defaultUser = new User(
            1L,
            "user",
            "pass",
            "email",
            Collections.singleton(RoleType.ROLE_USER),
            Collections.emptyList(),
            Collections.emptySet(),
            Collections.emptyList()
    );
    private static final User defaultAdmin = new User(
            1L,
            "user",
            "pass",
            "email",
            Collections.singleton(RoleType.ROLE_ADMIN),
            Collections.emptyList(),
            Collections.emptySet(),
            Collections.emptyList()
    );
    private static final AppUserDetails principalWithSameId =
            new AppUserDetails(new User(
                    1L,
                    "user",
                    "pass",
                    "email",
                    Collections.singleton(RoleType.ROLE_USER),
                    Collections.emptyList(),
                    Collections.emptySet(),
                    Collections.emptyList()
            ));
    private static final AppUserDetails principalWithAnotherId =
            new AppUserDetails(new User(
                    2L,
                    "user",
                    "pass",
                    "email",
                    Collections.singleton(RoleType.ROLE_USER),
                    Collections.emptyList(),
                    Collections.emptySet(),
                    Collections.emptyList()
            ));

    @Test
    @WithAnonymousUser
    @DisplayName("getAll test: get all users data from anonymous user.")
    void givenAnonymousUserWhenGetAllUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(getAllUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("getAll test: get all users data from simple user.")
    void givenSimpleUserWhenGetAllUrlThenStatusForbidden()
            throws Exception {
        mockMvc.perform(get(getAllUrl))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("getAll test: get all users data from admin user.")
    void givenUserDetailsFromUserWhenGetAllUrlThenUserListResponse()
            throws Exception {
        UserListResponse userListResponse = new UserListResponse(
                new ArrayList<>(List.of(adminUserResponse))
        );

        when(userMapper.modelListToModelListResponse(any(List.class)))
                .thenReturn(userListResponse);

        mockMvc.perform(get(getAllUrl))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(status().isOk());

        verify(userService, times(1))
                .findAll();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("getById test: get user data by id from anonymous user.")
    void givenAnonymousUserWhenGetByIdUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(getAllUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get user data by id " +
            "from simple user with same id.")
    void givenUserWithSameIdWhenGetByIdUrlThenUserResponse()
            throws Exception {
        when(userService.findById(any(Long.class)))
                .thenReturn(defaultUser);
        when(userMapper.modelToResponse(any(User.class)))
                .thenReturn(simpleUserResponseJson);

        mockMvc.perform(get(getByIdUrl)
                        .with(user(principalWithSameId))
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.password").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(status().isOk());

        verify(userMapper, times(1))
                .modelToResponse(any());
        /*
         1 - in controller.getById(id) method,
         2 - in @CheckUserIdPrivacy annotation
         */
        verify(userService, times(2))
                .findById(any());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get user data by id " +
            "from simple user with another id.")
    void givenUserWithAnotherIdWhenGetByIdUrlThenBadRequest()
            throws Exception {
        //@CheckUserIdPrivacy annotation
        when(userService.findById(any(Long.class)))
                .thenReturn(defaultUser);
        when(userMapper.modelToResponse(any(User.class)))
                .thenReturn(simpleUserResponseJson);
        mockMvc.perform(get(getByIdUrl)
                        .with(user(principalWithAnotherId))
                )
                .andExpect(jsonPath("$.errorMessage").isString())
                .andExpect(status().isBadRequest());

        verify(userMapper, times(0))
                .modelToResponse(any());
        verify(userService, times(1))
                .findById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("getById test: get user data by id from admin user.")
    void givenAdminUserWhenGetByIdUrlThenUserResponse()
            throws Exception {
        when(userService.findById(any(Long.class)))
                .thenReturn(new User());
        when(userMapper.modelToResponse(any(User.class)))
                .thenReturn(adminUserResponse);

        mockMvc.perform(get(getByIdUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADMIN_REQUEST_USER_JSON)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.password").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(status().isOk());

        verify(userMapper, times(1))
                .modelToResponse(any());
        verify(userService, times(1))
                .findById(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("update test: update user data by id from anonymous user.")
    void givenAnonymousUserWhenUpdateUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(updateUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("update test: update user data by id " +
            "from user with another id.")
    void givenUserWithAnotherIdWhenUpdateUrlThenBadRequest()
            throws Exception {
        //@CheckUserIdPrivacy annotation
        when(userService.findById(any(Long.class)))
                .thenReturn(defaultAdmin);

        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADMIN_REQUEST_USER_JSON)
                        .with(user(principalWithAnotherId))
                )
                .andExpect(jsonPath("$.errorMessage").isString())
                .andExpect(status().isBadRequest());
        /*
         1 - in @CheckUserIdPrivacy annotation
         */
        verify(userService, times(1))
                .findById(any());
    }

    @Test
    @WithMockUser()
    @DisplayName("update test: update user data by id from user with same id.")
    void givenUserWithSameIdWhenUpdateUrlThenUserResponse()
            throws Exception {
        //@CheckUserIdPrivacy annotation
        when(userService.findById(any(Long.class)))
                .thenReturn(defaultAdmin);
        when(userMapper.requestToModel(any(UserRequest.class)))
                .thenReturn(defaultAdmin);
        when(userMapper.modelToResponse(any(User.class)))
                .thenReturn(adminUserResponse);
        when(userService.update(any(Long.class), any(User.class)))
                .thenReturn(defaultAdmin);

        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADMIN_REQUEST_USER_JSON)
                        .with(user(principalWithSameId))
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.password").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(status().isOk());

        verify(userMapper, times(1))
                .modelToResponse(any());
        verify(userMapper, times(1))
                .requestToModel(any());
        verify(userService, times(1))
                .findById(any());
        verify(userService, times(1))
                .update(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update test: update user data by id from admin user.")
    void givenAdminUserWhenUpdateUrlThenUserResponse()
            throws Exception {
        when(userMapper.requestToModel(any(UserRequest.class)))
                .thenReturn(defaultAdmin);
        when(userMapper.modelToResponse(any(User.class)))
                .thenReturn(adminUserResponse);
        when(userService.update(any(Long.class), any(User.class)))
                .thenReturn(defaultAdmin);

        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADMIN_REQUEST_USER_JSON)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.password").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(status().isOk());

        verify(userMapper, times(1))
                .modelToResponse(any());
        verify(userMapper, times(1))
                .requestToModel(any());
        verify(userService, times(1))
                .update(any(), any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("delete test: delete user data by id from anonymous user.")
    void givenAnonymousUserWhenDeleteUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(deleteUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("delete test: delete user data by id from user with same id.")
    void givenUserWithSameIdWhenDeleteUrlThenForbidden()
            throws Exception {
        mockMvc.perform(delete(deleteUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIMPLE_REQUEST_USER_JSON)
                        .with(user(principalWithSameId))
                )
                .andExpect(jsonPath("$.message").isString())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("delete test: delete user data by id from admin user.")
    void givenAdminUserWhenDeleteUrlThenStatusNoContent()
            throws Exception {
        mockMvc.perform(delete(deleteUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADMIN_REQUEST_USER_JSON)
                )
                .andExpect(status().isNoContent());
        verify(userService, times(0))
                .findById(any());
    }
}
