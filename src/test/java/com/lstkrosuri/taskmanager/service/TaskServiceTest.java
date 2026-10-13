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
}