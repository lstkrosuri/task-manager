
package com.lstkrosuri.taskmanager.service;

import com.lstkrosuri.taskmanager.model.Task;
import com.lstkrosuri.taskmanager.repository.TaskRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    // Constructor injection
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // GET /api/tasks
    public List<Task> getAllTasks(String sortBy) {

        Sort sort = createValidatedSort(sortBy);

        return taskRepository.findAll(sort);
    }

    public List<Task> getTasksByCompleted(boolean completed, String sortBy) {

        Sort sort = createValidatedSort(sortBy);

        return taskRepository.findByCompleted(completed, sort);
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

    private Sort createValidatedSort(String sortBy) {

        if (!sortBy.equals("id") && !sortBy.equals("title")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field. Allowed values: id, title"
            );
        }

        return Sort.by(Sort.Direction.ASC, sortBy);
    }

    public Page<Task> getTasksPaginated(
            Boolean completed,
            int page,
            int size,
            String sortBy) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number must be zero or greater"
            );
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }

        Sort sort = createValidatedSort(sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        if (completed != null) {
            return taskRepository.findByCompleted(completed, pageable);
        }

        return taskRepository.findAll(pageable);
    }
}
