package ru.effective.tms.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.effective.tms.exception.EntityNotFoundException;
import ru.effective.tms.model.enums.Priority;
import ru.effective.tms.model.enums.Status;
import ru.effective.tms.model.entity.Task;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.repository.TaskRepository;
import ru.effective.tms.service.impl.TaskServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("TaskServiceImplTest Tests")
public class TaskServiceImplTest {
    private TaskServiceImpl taskService;
    @MockitoBean
    private TaskRepository taskRepository;
    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, userService);
    }

    @Test
    @DisplayName("findAll test: get all user task.")
    void givenWhenGetAllThenListTask() {
        List<Task> taskListList = new ArrayList<>(List.of(
                new Task(),
                new Task()
        ));
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(taskRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(taskListList));
        List<Task> actual = taskService.findAll();

        assertEquals(taskListList.size(), actual.size());
        verify(taskRepository, times(1))
                .findAll(pageRequest);
    }

    @Test
    @DisplayName("findById test: get task data by id.")
    void givenExistingTaskIdWhenGetByIdThenTask() {
        Long userId = 1L;
        Task defaultTask = new Task(
                1L,
                "title",
                "description",
                Status.OPEN,
                Priority.MEDIUM,
                new User(),
                Collections.emptySet(),
                Collections.emptyList()
        );

        when(taskRepository.findById(userId))
                .thenReturn(Optional.of(defaultTask));

        Task actual = taskService.findById(userId);

        assertEquals(defaultTask, actual);
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("findById test: try to get task data by not existing id.")
    void givenNotExistingTaskIdWhenGetByIdThenThrow() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> taskService.findById(taskId),
                " index is incorrect"
        );
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("save test: send task data to repository.")
    void givenTaskWhenSendTaskToDbThenSavedTask() {
        Task taskToSave = new Task(
                1L,
                "title",
                "description",
                Status.OPEN,
                Priority.MEDIUM,
                new User(),
                Collections.emptySet(),
                Collections.emptyList()
        );

        when(taskRepository.save(taskToSave)).thenReturn(taskToSave);
        Task actual = taskService.save(taskToSave);

        assertEquals(taskToSave, actual);
        verify(taskRepository, times(1))
                .save(any());
    }

    @Test
    @DisplayName("update test: send user data to repository.")
    void givenTaskAndTaskIdWhenUpdateByIdThenUpdatedTask() {
        Long taskId = 1L;
        Task taskToUpdate = new Task(
                1L,
                "title",
                "description",
                Status.OPEN,
                Priority.MEDIUM,
                new User(),
                Collections.emptySet(),
                Collections.emptyList()
        );

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(taskToUpdate));
        when(taskRepository.save(taskToUpdate)).thenReturn(taskToUpdate);

        Task actual = taskService.update(taskId, taskToUpdate);

        assertEquals(taskToUpdate, actual);
        verify(taskRepository, times(1))
                .save(any());
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("update test: try update with not existed user id.")
    void givenTaskAndNotExistedTaskIdWhenUpdateByIdThenThrow() {
        Long notExistedTaskId = 1L;
        Task taskToUpdate = new Task(
                1L,
                "title",
                "description",
                Status.OPEN,
                Priority.MEDIUM,
                new User(),
                Collections.emptySet(),
                Collections.emptyList()
        );

        when(taskRepository.findById(notExistedTaskId))
                .thenReturn(Optional.empty());
        when(taskRepository.save(taskToUpdate)).thenReturn(taskToUpdate);

        assertThrows(EntityNotFoundException.class,
                () -> taskService.update(notExistedTaskId, taskToUpdate),
                "userId is incorrect"
        );
        verify(taskRepository, times(0))
                .save(any());
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("delete test: delete task data message to repository.")
    void givenExistedTaskIdWhenDeleteByIdThenVoid() {
        Long existedTaskId = 1L;

        when(taskRepository.findById(existedTaskId))
                .thenReturn(Optional.of(new Task()));
        taskService.deleteById(existedTaskId);

        verify(taskRepository, times(1))
                .findById(existedTaskId);
        verify(taskRepository, times(1))
                .deleteById(existedTaskId);
    }

    @Test
    @DisplayName("delete test: delete task data message to repository.")
    void givenNotExistedTaskIdWhenDeleteByIdThenThrow() {
        Long notExistedTaskId = 1L;

        assertThrows(EntityNotFoundException.class,
                () -> taskService.deleteById(notExistedTaskId),
                "taskId is incorrect."
        );
        verify(taskRepository, times(1))
                .findById(notExistedTaskId);
        verify(taskRepository, times(0))
                .deleteById(notExistedTaskId);
    }
}
