
package com.lstkrosuri.taskmanager.controller;

import com.lstkrosuri.taskmanager.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.ArrayList;

@RestController
public class TaskController {

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

    // GET all tasks
    @GetMapping("/api/tasks")
    public List<Task> getTasks() {
        return tasks;
    }

    // GET a single task by ID
    @GetMapping("/api/tasks/{id}")
    public Task getTaskById(@PathVariable Long id) {

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


    @PostMapping("/api/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task newTask) {

        long nextId = tasks.stream()
                .mapToLong(Task::getId)
                .max()
                .orElse(0L) + 1;

        newTask.setId(nextId);

        tasks.add(newTask);

        return newTask;
    }


    @PutMapping("/api/tasks/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestBody Task updatedTask) {

        Task existingTask = tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found with id: " + id
                        )
                );

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        return existingTask;
    }


    @DeleteMapping("/api/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {

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
