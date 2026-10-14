package com.lstkrosuri.taskmanager.service;

import com.lstkrosuri.taskmanager.model.Task;
import com.lstkrosuri.taskmanager.repository.TaskRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTaskById_shouldReturnTask_whenTaskExists() {

        // Arrange: create a sample task
        Task task = new Task();
        task.setId(21L);
        task.setTitle("Learn Unit Testing");
        task.setDescription("Practice JUnit and Mockito");
        task.setCompleted(false);

        // Tell Mockito what the repository should return
        when(taskRepository.findById(21L))
                .thenReturn(Optional.of(task));

        // Act: call the real service method
        Task result = taskService.getTaskById(21L);

        // Assert: verify the result
        assertEquals(21L, result.getId());
        assertEquals("Learn Unit Testing", result.getTitle());
        assertEquals(
                "Practice JUnit and Mockito",
                result.getDescription()
        );
        assertFalse(result.isCompleted());
    }

    @Test
    void getTaskById_shouldThrowNotFound_whenTaskDoesNotExist() {

        // Arrange: simulate a task that does not exist
        Long taskId = 9999L;

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        // Act: call the service and capture the exception
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.getTaskById(taskId)
        );

        // Assert: verify the error status and message
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        assertEquals(
                "Task not found with ID: 9999",
                exception.getReason()
        );
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {

        // Arrange: create a task with an ID
        Task newTask = new Task();
        newTask.setId(100L);
        newTask.setTitle("Learn Mockito");
        newTask.setDescription("Practice testing task creation");
        newTask.setCompleted(false);

        // Simulate the database assigning a new ID
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> {
                    Task taskToSave = invocation.getArgument(0);

                    // Verify the service cleared the ID
                    assertNull(taskToSave.getId());

                    taskToSave.setId(101L);
                    return taskToSave;
                });

        // Act: call the real service method
        Task result = taskService.createTask(newTask);

        // Assert: verify the returned task
        assertEquals(101L, result.getId());
        assertEquals("Learn Mockito", result.getTitle());
        assertEquals(
                "Practice testing task creation",
                result.getDescription()
        );

        // Verify the repository was called exactly once
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    void updateTask_shouldUpdateAndSaveTask_whenTaskExists() {

        // Arrange: create an existing task
        Task existingTask = new Task();
        existingTask.setId(21L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setCompleted(false);

        // Create the new values
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setCompleted(true);

        // Mock finding the existing task
        when(taskRepository.findById(21L))
                .thenReturn(Optional.of(existingTask));

        // Mock saving the updated task
        when(taskRepository.save(existingTask))
                .thenReturn(existingTask);

        // Act: call the real service method
        Task result = taskService.updateTask(21L, updatedTask);

        // Assert: verify the updated values
        assertEquals(21L, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertTrue(result.isCompleted());

        // Verify repository interactions
        verify(taskRepository, times(1)).findById(21L);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void deleteTask_shouldDeleteTask_whenTaskExists() {

        // Arrange: create an existing task
        Task existingTask = new Task();
        existingTask.setId(21L);
        existingTask.setTitle("Task to Delete");
        existingTask.setDescription("Practice Mockito delete testing");
        existingTask.setCompleted(false);

        // Mock finding the existing task
        when(taskRepository.findById(21L))
                .thenReturn(Optional.of(existingTask));

        // Act: call the real service method
        taskService.deleteTask(21L);

        // Assert: verify repository interactions
        verify(taskRepository, times(1)).findById(21L);
        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void getTasksPaginated_shouldReturnFirstPage() {

        // Arrange: create two sample tasks
        Task task1 = new Task();
        task1.setId(21L);
        task1.setTitle("Learn Spring Boot");

        Task task2 = new Task();
        task2.setId(41L);
        task2.setTitle("Learn JPA");

        // Create a pagination request
        Pageable pageable = PageRequest.of(
                0,
                2,
                Sort.by(Sort.Direction.ASC, "id")
        );

        // Simulate four total tasks, with two on this page
        Page<Task> mockPage = new PageImpl<>(
                List.of(task1, task2),
                pageable,
                4
        );

        // Configure the mock repository
        when(taskRepository.findAll(pageable))
                .thenReturn(mockPage);

        // Act: call the real service method
        Page<Task> result = taskService.getTasksPaginated(
                null,
                0,
                2,
                "id"
        );

        // Assert: verify pagination results
        assertEquals(2, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(0, result.getNumber());

        assertEquals(21L, result.getContent().get(0).getId());
        assertEquals(41L, result.getContent().get(1).getId());

        // Verify repository interaction
        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    void getTasksPaginated_shouldFilterIncompleteTasks() {

        // Arrange: create two incomplete tasks
        Task task1 = new Task();
        task1.setId(62L);
        task1.setTitle("Apple Task");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setId(21L);
        task2.setTitle("Learn Spring Boot");
        task2.setCompleted(false);

        // Create pagination and sorting request
        Pageable pageable = PageRequest.of(
                0,
                2,
                Sort.by(Sort.Direction.ASC, "title")
        );

        // Simulate three incomplete tasks in total
        Page<Task> mockPage = new PageImpl<>(
                List.of(task1, task2),
                pageable,
                3
        );

        // Mock the filtered repository method
        when(taskRepository.findByCompleted(false, pageable))
                .thenReturn(mockPage);

        // Act: request incomplete tasks
        Page<Task> result = taskService.getTasksPaginated(
                false,
                0,
                2,
                "title"
        );

        // Assert: check pagination information
        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(0, result.getNumber());

        // Check task information
        assertEquals("Apple Task", result.getContent().get(0).getTitle());
        assertEquals("Learn Spring Boot", result.getContent().get(1).getTitle());

        assertFalse(result.getContent().get(0).isCompleted());
        assertFalse(result.getContent().get(1).isCompleted());

        // Verify the filtered repository method was called
        verify(taskRepository, times(1))
                .findByCompleted(false, pageable);
    }

    @Test
    void getTasksPaginated_shouldRejectNegativePageNumber() {

        // Arrange: use an invalid page number
        int page = -1;
        int size = 2;
        String sortBy = "id";

        // Act: call the service and expect an exception
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.getTasksPaginated(
                        null,
                        page,
                        size,
                        sortBy
                )
        );

        // Assert: verify the HTTP status
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Assert: verify the error message
        assertEquals(
                "Page number must be zero or greater",
                exception.getReason()
        );

        // Verify that the repository was never called
        verifyNoInteractions(taskRepository);
    }

    @Test
    void getTasksPaginated_shouldRejectZeroPageSize() {

        // Arrange: use an invalid page size
        int page = 0;
        int size = 0;
        String sortBy = "id";

        // Act: expect the service to throw an exception
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.getTasksPaginated(
                        null,
                        page,
                        size,
                        sortBy
                )
        );

        // Assert: verify HTTP 400 Bad Request
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Assert: verify the error message
        assertEquals(
                "Page size must be between 1 and 100",
                exception.getReason()
        );

        // Verify no repository methods were called
        verifyNoInteractions(taskRepository);
    }

    @Test
    void getTasksPaginated_shouldRejectPageSizeGreaterThan100() {

        // Arrange: use an invalid page size
        int page = 0;
        int size = 101;
        String sortBy = "id";

        // Act: expect the service to throw an exception
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.getTasksPaginated(
                        null,
                        page,
                        size,
                        sortBy
                )
        );

        // Assert: verify HTTP 400 Bad Request
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Assert: verify the error message
        assertEquals(
                "Page size must be between 1 and 100",
                exception.getReason()
        );

        // Verify that the repository was never called
        verifyNoInteractions(taskRepository);
    }

    @Test
    void getTasksPaginated_shouldRejectInvalidSortField() {

        // Arrange: use an invalid sorting field
        int page = 0;
        int size = 2;
        String sortBy = "unknownField";

        // Act: expect an exception
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.getTasksPaginated(
                        null,
                        page,
                        size,
                        sortBy
                )
        );

        // Assert: verify HTTP 400 Bad Request
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // Assert: verify the error message
        assertEquals(
                "Invalid sort field. Allowed values: id, title",
                exception.getReason()
        );

        // Verify that the repository was never called
        verifyNoInteractions(taskRepository);
    }
}