
package com.lstkrosuri.taskmanager.service;

import com.lstkrosuri.taskmanager.model.Task;
import com.lstkrosuri.taskmanager.repository.TaskRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    // Constructor injection
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // GET /api/tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // GET /api/tasks/{id}
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found with ID: " + id
                ));
    }

    // POST /api/tasks
    public Task createTask(Task newTask) {
        newTask.setId(null);
        return taskRepository.save(newTask);
    }

    // PUT /api/tasks/{id}
    public Task updateTask(Long id, Task updatedTask) {

        Task existingTask = getTaskById(id);

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existingTask);
    }

    // DELETE /api/tasks/{id}
    public void deleteTask(Long id) {

        Task existingTask = getTaskById(id);

        taskRepository.delete(existingTask);
    }
}
