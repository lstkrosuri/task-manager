
package com.lstkrosuri.taskmanager.service;

import com.lstkrosuri.taskmanager.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>(List.of(
            new Task(1L, "Learn Java",
                    "Practice Java classes and objects", false),

            new Task(2L, "Learn Spring Boot",
                    "Build my first REST API", true),

            new Task(3L, "Learn Angular",
                    "Create the Task Manager frontend", false),

            new Task(4L, "Learn Oracle SQL",
                    "Practice SELECT, INSERT, UPDATE and DELETE", false)
    ));

    // READ all tasks
    public List<Task> getAllTasks() {
        return tasks;
    }

    // READ one task by ID
    public Task getTaskById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found with id: " + id
                        )
                );
    }

    // CREATE a new task
    public Task createTask(Task newTask) {

        long nextId = tasks.stream()
                .mapToLong(Task::getId)
                .max()
                .orElse(0L) + 1;

        newTask.setId(nextId);
        tasks.add(newTask);

        return newTask;
    }

    // UPDATE an existing task
    public Task updateTask(Long id, Task updatedTask) {

        Task existingTask = getTaskById(id);

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        return existingTask;
    }

    // DELETE a task
    public void deleteTask(Long id) {

        boolean removed = tasks.removeIf(
                task -> task.getId().equals(id)
        );

        if (!removed) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task not found with id: " + id
            );
        }
    }
}
