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
import ru.effective.tms.mapper.TaskMapper;
import ru.effective.tms.model.dto.comment.CommentResponse;
import ru.effective.tms.model.dto.task.TaskListResponse;
import ru.effective.tms.model.dto.task.TaskRequest;
import ru.effective.tms.model.dto.task.TaskResponse;
import ru.effective.tms.model.dto.task.TaskResponseWith;
import ru.effective.tms.model.entity.Task;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.model.entity.security.AppUserDetails;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.model.enums.Priority;
import ru.effective.tms.model.enums.Status;
import ru.effective.tms.service.TaskService;

import java.time.Instant;
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
@DisplayName("TaskControllerTest Tests")
public class TaskControllerTest {
    private final static String urlTemplate = "/api/task";
    @MockitoBean
    private TaskService taskService;
    @MockitoBean
    private TaskMapper taskMapper;
    @Autowired
    private MockMvc mockMvc;
    private static final String getAllUrl = urlTemplate;
    private static final String getByIdUrl = urlTemplate + "/1";
    private static final String updateUrl = urlTemplate + "/1";
    private static final String deleteUrl = urlTemplate + "/1";
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
    private static final String TASK_REQUEST_JSON = """
            {
                "title": "title",
                "description": "description",
                "currentStatus" : "OPEN",
                "currentPriority" : "MEDIUM",
                "authorId": 11,
                "performerIds": [6,8]
            }""";
    private static final Task defaultTask = new Task(
            1L,
            "title",
            "description",
            Status.OPEN,
            Priority.MEDIUM,
            defaultUser,
            Collections.emptySet(),
            Collections.emptyList()
    );
    private static final TaskResponse TASK_RESPONSE = new TaskResponse(
            1L,
            "title",
            "description",
            Status.OPEN,
            Priority.MEDIUM,
            1L,
            Collections.singletonList(2L)
    );
    private static final CommentResponse COMMENT_RESPONSE = new CommentResponse(
            1L, "content", "user", Instant.now(), 1L
    );
    private static final TaskResponseWith TASK_RESPONSE_WITH = new TaskResponseWith(
            1L,
            "title",
            "description",
            Status.OPEN,
            Priority.MEDIUM,
            1L,
            Collections.singletonList(2L),
            List.of(COMMENT_RESPONSE)
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
    @DisplayName("getAll test: get all tasks data from anonymous user.")
    void givenAnonymousUserWhenGetAllUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(getAllUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("getAll test: get all tasks data from simple user.")
    void givenSimpleUserWhenGetAllUrlThenStatusForbidden()
            throws Exception {
        mockMvc.perform(get(getAllUrl))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("getAll test: get all tasks data from admin user.")
    void givenAdminUserWhenGetAllUrlThenTaskListResponse()
            throws Exception {
        TaskListResponse taskListResponse = new TaskListResponse(
                new ArrayList<>(List.of(TASK_RESPONSE))
        );

        when(taskMapper.modelListToModelListResponse(any(List.class)))
                .thenReturn(taskListResponse);

        mockMvc.perform(get(getAllUrl))
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(status().isOk());

        verify(taskService, times(1))
                .findAll();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("getById test: get task data by id from anonymous user.")
    void givenAnonymousUserWhenGetByIdUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(getByIdUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getById test: get task data by id " +
            "from simple user with another id.")
    void givenAnotherUserWhenGetByIdUrlThenStatusForbidden()
            throws Exception {
        when(taskService.findById(any(Long.class)))
                .thenReturn(defaultTask);
        mockMvc.perform(get(getByIdUrl)
                        .with(user(principalWithAnotherId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").isString());
        verify(taskService, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("getById test: get task data by id " +
            "from simple user with same id.")
    void givenSameUserWhenGetByIdUrlThenStatusTaskResponse()
            throws Exception {
        when(taskService.findById(any(Long.class)))
                .thenReturn(defaultTask);
        when(taskMapper.modelToResponseWith(any(Task.class)))
                .thenReturn(TASK_RESPONSE_WITH);

        mockMvc.perform(get(getByIdUrl)
                        .with(user(principalWithSameId)))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.currentStatus").isString())
                .andExpect(jsonPath("$.currentPriority").isString())
                .andExpect(jsonPath("$.authorId").isNumber())
                .andExpect(jsonPath("$.performerIds").isArray())
                .andExpect(status().isOk());

        verify(taskMapper, times(1))
                .modelToResponseWith(any());
        verify(taskService, times(2))
                .findById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("getById test: get task data by id")
    void givenAdminUserAndTaskIdWhenGetByIdUrlThenTaskResponse()
            throws Exception {
        when(taskService.findById(any(Long.class)))
                .thenReturn(defaultTask);
        when(taskMapper.modelToResponseWith(any(Task.class)))
                .thenReturn(TASK_RESPONSE_WITH);

        mockMvc.perform(get(getByIdUrl))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.currentStatus").isString())
                .andExpect(jsonPath("$.currentPriority").isString())
                .andExpect(jsonPath("$.authorId").isNumber())
                .andExpect(jsonPath("$.performerIds").isArray())
                .andExpect(status().isOk());

        verify(taskMapper, times(1))
                .modelToResponseWith(any());
        verify(taskService, times(1))
                .findById(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("update test: update task data by id from anonymous user.")
    void givenAnonymousUserWhenUpdateUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(put(updateUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("update test: update task data by id " +
            "from task with same id.")
    void givenTaskWithAnotherIdWhenUpdateUrlThenForbidden()
            throws Exception {
        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TASK_REQUEST_JSON)
                        .with(user(principalWithSameId))
                )
                .andExpect(jsonPath("$.message").isString())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update test: update task data by id from admin user.")
    void givenAdminPrincipalWhenUpdateUrlThenUserResponse()
            throws Exception {
        when(taskMapper.requestToModel(any(TaskRequest.class)))
                .thenReturn(defaultTask);
        when(taskMapper.modelToResponse(any(Task.class)))
                .thenReturn(TASK_RESPONSE);
        when(taskService.update(any(Long.class), any(Task.class)))
                .thenReturn(defaultTask);

        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TASK_REQUEST_JSON)
                )
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.currentStatus").isString())
                .andExpect(jsonPath("$.currentPriority").isString())
                .andExpect(jsonPath("$.authorId").isNumber())
                .andExpect(jsonPath("$.performerIds").isArray())
                .andExpect(status().isOk());

        verify(taskMapper, times(1))
                .modelToResponse(any());
        verify(taskMapper, times(1))
                .requestToModel(any());
        verify(taskService, times(1))
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
    @DisplayName("delete test: delete task data by id from user with same id.")
    void givenUserPrincipalWithSameIdWhenDeleteUrlThenStatusNoContent()
            throws Exception {
        mockMvc.perform(delete(deleteUrl)
                        .with(user(principalWithSameId))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("delete test: delete task data by id from admin user.")
    void givenAdminPrincipalWhenDeleteUrlThenStatusNoContent()
            throws Exception {
        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isNoContent());
        verify(taskService, times(0))
                .findById(any());
    }
}

