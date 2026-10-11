
package com.lstkrosuri.taskmanager.controller;

import com.lstkrosuri.taskmanager.model.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {

    @GetMapping("/api/tasks")
    public List<Task> getTasks() {

        Task task1 = new Task(
                1L,
                "Learn Java",
                "Practice Java classes and objects",
                false
        );

        Task task2 = new Task(
                2L,
                "Learn Spring Boot",
                "Build my first REST API",
                true
        );

        Task task3 = new Task(
                3L,
                "Learn Angular",
                "Create the Task Manager frontend",
                false
        );
        Task task4 = new Task(
                4L,
                "Learn Oracle SQL",
                "Practice SELECT, INSERT, UPDATE and DELETE",
                false
        );

        return List.of(task1, task2, task3,task4);
    }
}
