package ru.effective.tms.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.effective.tms.exception.EntityNotFoundException;
import ru.effective.tms.model.entity.Comment;
import ru.effective.tms.model.entity.Task;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.repository.CommentRepository;
import ru.effective.tms.service.impl.CommentServiceImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("CommentServiceImplTest Tests")
public class CommentServiceImplTest {
    private CommentServiceImpl commentService;
    @MockitoBean
    private CommentRepository commentRepository;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(
                commentRepository,
                userService,
                taskService
        );
    }

    @Test
    @DisplayName("findAll test: get all comment data.")
    void givenWhenGetAllThenListComment() {
        List<Comment> commentList = new ArrayList<>(List.of(
                new Comment(),
                new Comment()
        ));
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(commentRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(commentList));
        List<Comment> actual = commentService.findAll();

        assertEquals(commentList.size(), actual.size());
        verify(commentRepository, times(1))
                .findAll(pageRequest);
    }

    @Test
    @DisplayName("findById test: get comment data by id.")
    void givenExistingCommentIdWhenGetByIdThenComment() {
        Long userId = 1L;
        Comment defaultComment = new Comment(
                1L,
                "content",
                Instant.now(),
                Instant.now(),
                new Task(),
                new User()
        );

        when(commentRepository.findById(userId))
                .thenReturn(Optional.of(defaultComment));

        Comment actual = commentService.findById(userId);

        assertEquals(defaultComment, actual);
        verify(commentRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("findById test: try to get comment data by not existing id.")
    void givenNotExistingCommentIdWhenGetByIdThenThrow() {
        Long userId = 1L;

        when(commentRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.findById(userId),
                " index is incorrect"
        );
        verify(commentRepository, times(1))
                .findById(any());
    }

    @Test
    @WithMockUser
    @DisplayName("save test: send comment data to repository.")
    void givenCommentWhenSendUserToDbThenSavedUser() {
        Long commentId = 1L;
        Comment commentToSave = new Comment(
                commentId, "content", Instant.now(),
                Instant.now(), new Task(), null
        );
        User authorUser = new User(
                1L, "user", "pass", "email",
                Collections.singleton(RoleType.ROLE_USER),
                Collections.emptyList(),
                Collections.emptySet(),
                new ArrayList<>()
        );

        when(userService.findByUsername(anyString())).thenReturn(authorUser);
        when(commentRepository.save(commentToSave)).thenReturn(commentToSave);
        Comment actual = commentService.save(commentToSave);

        assertEquals(commentToSave, actual);
        assertNotNull(commentToSave.getAuthor());
        verify(commentRepository, times(1))
                .save(any());
        verify(userService, times(1))
                .findByUsername(any());
        verify(userService, times(1))
                .update(commentId, authorUser);
    }

    @Test
    @DisplayName("update test: send comment data to repository.")
    void givenCommentAndExistedCommentIdWhenSendUserToDbThenUpdatedComment() {
        Long userId = 1L;
        Comment commentToUpdate = new Comment(
                1L,
                "content",
                Instant.now(),
                Instant.now(),
                new Task(),
                new User()
        );

        when(commentRepository.findById(userId))
                .thenReturn(Optional.of(commentToUpdate));
        when(commentRepository.save(commentToUpdate))
                .thenReturn(commentToUpdate);

        Comment actual = commentService.update(userId, commentToUpdate);

        assertEquals(commentToUpdate, actual);
        verify(commentRepository, times(1))
                .save(any());
        verify(commentRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("update test: try update with not existed comment id.")
    void givenCommentAndNotExistedCommentIdWhenSendUserToDbThenThrow() {
        Long notExistedUserId = 1L;
        Comment commentToUpdate = new Comment(
                1L,
                "content",
                Instant.now(),
                Instant.now(),
                new Task(),
                new User()
        );

        when(commentRepository.findById(notExistedUserId))
                .thenReturn(Optional.empty());
        when(commentRepository.save(commentToUpdate))
                .thenReturn(commentToUpdate);

        assertThrows(EntityNotFoundException.class,
                () -> commentService.update(notExistedUserId, commentToUpdate),
                "commentId is incorrect."
        );
        verify(commentRepository, times(0))
                .save(any());
        verify(commentRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("delete test: delete comment data message for repository.")
    void givenExistedCommentIdWhenDeleteByIdThenVoid() {
        Long existedUserId = 1L;

        when(commentRepository.existsById(existedUserId))
                .thenReturn(true);
        commentService.deleteById(existedUserId);

        verify(commentRepository, times(1))
                .existsById(existedUserId);
        verify(commentRepository, times(1))
                .deleteById(existedUserId);
    }

    @Test
    @DisplayName("delete test: delete comment data message for repository.")
    void givenNotExistedCommentIdWhenDeleteByIdThenThrow() {
        Long notExistedUserId = 1L;

        when(commentRepository.existsById(notExistedUserId))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> commentService.deleteById(notExistedUserId),
                "commentId is incorrect."
        );
        verify(commentRepository, times(1))
                .existsById(notExistedUserId);
        verify(commentRepository, times(0))
                .deleteById(notExistedUserId);
    }
}
