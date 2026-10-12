
package com.lstkrosuri.taskmanager.controller;

import com.lstkrosuri.taskmanager.model.Task;
import com.lstkrosuri.taskmanager.service.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
public class TaskController {

    private final TaskService taskService;

    // Constructor injection
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // GET all tasks
    @GetMapping("/api/tasks")
    public List<Task> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "id") String sort) {

        if (completed != null) {
            return taskService.getTasksByCompleted(completed, sort);
        }

        return taskService.getAllTasks(sort);
    }
    // GET task by ID
    @GetMapping("/api/tasks/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    // POST create task
    @PostMapping("/api/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task newTask) {
        return taskService.createTask(newTask);
    }

    // PUT update task
    @PutMapping("/api/tasks/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task updatedTask) {

        return taskService.updateTask(id, updatedTask);
    }

    // DELETE task
    @DeleteMapping("/api/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
