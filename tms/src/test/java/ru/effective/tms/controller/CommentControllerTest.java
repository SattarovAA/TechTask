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
import ru.effective.tms.mapper.CommentMapper;
import ru.effective.tms.model.dto.comment.CommentListResponse;
import ru.effective.tms.model.dto.comment.CommentRequest;
import ru.effective.tms.model.dto.comment.CommentResponse;
import ru.effective.tms.model.entity.Comment;
import ru.effective.tms.model.entity.Task;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.model.entity.security.AppUserDetails;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.service.CommentService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CommentControllerTest Tests")
public class CommentControllerTest {
    private final static String urlTemplate = "/api/comment";
    @MockitoBean
    private CommentService commentService;
    @MockitoBean
    private CommentMapper commentMapper;
    @Autowired
    private MockMvc mockMvc;
    private static final String getAllUrl = urlTemplate;
    private static final String getByIdUrl = urlTemplate + "/1";
    private static final String updateUrl = urlTemplate + "/1";
    private static final String deleteUrl = urlTemplate + "/1";
    private static final User DEFAULT_USER = new User(
            1L,
            "user",
            "pass",
            "email",
            Collections.singleton(RoleType.ROLE_ADMIN),
            Collections.emptyList(),
            Collections.emptySet(),
            Collections.emptyList()
    );
    private static final String SIMPLE_REQUEST_COMMENT_JSON = """
            {
               "content": "content",
               "taskId": 1
            }""";

    private static final Comment DEFAULT_COMMENT = new Comment(
            1L,
            "content",
            Instant.now(),
            Instant.now(),
            new Task(),
            DEFAULT_USER
    );
    private static final CommentResponse COMMENT_RESPONSE = new CommentResponse(
            1L, "content", "user", Instant.now(), 1L
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
    @DisplayName("getAll test: get all comments data from anonymous user.")
    void givenAnonymousUserWhenGetAllUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(getAllUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("getAll test: get all comments data.")
    void givenSimpleUserWhenGetAllUrlThenCommentListResponse()
            throws Exception {
        CommentListResponse commentListResponse = new CommentListResponse(
                new ArrayList<>(List.of(COMMENT_RESPONSE))
        );

        when(commentMapper.modelListToModelListResponse(any(List.class)))
                .thenReturn(commentListResponse);

        mockMvc.perform(get(getAllUrl))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(status().isOk());

        verify(commentService, times(1))
                .findAll();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("getById test: get comment data by id from anonymous user.")
    void givenAnonymousUserWhenGetByIdUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(getByIdUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get comment data by id")
    void givenUserIdWhenGetByIdUrlThenCommentResponse()
            throws Exception {
        when(commentService.findById(any(Long.class)))
                .thenReturn(DEFAULT_COMMENT);
        when(commentMapper.modelToResponse(any(Comment.class)))
                .thenReturn(COMMENT_RESPONSE);
        mockMvc.perform(get(getByIdUrl))
                .andExpect(jsonPath("$.content").isString())
                .andExpect(jsonPath("$.userName").isString())
                .andExpect(jsonPath("$.updatedTime").isString())
                .andExpect(status().isOk());

        verify(commentMapper, times(1))
                .modelToResponse(any());
        verify(commentService, times(1))
                .findById(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("update test: update comment data by id from anonymous user.")
    void givenAnonymousUserWhenUpdateUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(updateUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("update test: update comment data " +
            "by id from user with same id.")
    void givenUserWithSameIdWhenUpdateUrlThenBadRequest()
            throws Exception {
        //@CheckUserIdPrivacy annotation
        when(commentService.findById(any(Long.class)))
                .thenReturn(DEFAULT_COMMENT);

        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIMPLE_REQUEST_COMMENT_JSON)
                        .with(user(principalWithSameId))
                )
                .andExpect(jsonPath("$.message").isString())
                .andExpect(status().isForbidden());

        verify(commentService, times(0))
                .findById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update test: update comment data by id from admin user.")
    void givenAdminUserWhenUpdateUrlThenUserResponse()
            throws Exception {
        when(commentMapper.requestToModel(any(CommentRequest.class)))
                .thenReturn(DEFAULT_COMMENT);
        when(commentMapper.modelToResponse(any(Comment.class)))
                .thenReturn(COMMENT_RESPONSE);
        when(commentService.update(any(Long.class), any(Comment.class)))
                .thenReturn(DEFAULT_COMMENT);

        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIMPLE_REQUEST_COMMENT_JSON)
                )
                .andExpect(jsonPath("$.content").isString())
                .andExpect(jsonPath("$.userName").isString())
                .andExpect(jsonPath("$.updatedTime").isString())
                .andExpect(status().isOk());

        verify(commentMapper, times(1))
                .modelToResponse(any());
        verify(commentMapper, times(1))
                .requestToModel(any());
        verify(commentService, times(1))
                .update(any(), any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("delete test: delete comment data by id from anonymous user.")
    void givenAnonymousUserWhenDeleteUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(get(deleteUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("delete test: delete comment data by id from user with same id.")
    void givenUserWithSameIdWhenDeleteUrlThenStatusNoContent()
            throws Exception {
        mockMvc.perform(delete(deleteUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIMPLE_REQUEST_COMMENT_JSON)
                        .with(user(principalWithSameId))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("delete test: delete comment data by id from admin user.")
    void givenAdminUserWhenDeleteUrlThenStatusNoContent()
            throws Exception {
        mockMvc.perform(delete(deleteUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIMPLE_REQUEST_COMMENT_JSON)
                )
                .andExpect(status().isNoContent());
        verify(commentService, times(0))
                .findById(any());
    }
}
