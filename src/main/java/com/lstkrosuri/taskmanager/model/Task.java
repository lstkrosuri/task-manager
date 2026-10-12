package com.lstkrosuri.taskmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "TASKS")
public class Task {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_sequence_generator"
    )
    @SequenceGenerator(
            name = "task_sequence_generator",
            sequenceName = "TASK_SEQ",
            allocationSize = 1
    )
    private Long id;

    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Task title cannot exceed 200 characters")
    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Size(max = 1000, message = "Task description cannot exceed 1000 characters")
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "COMPLETED", nullable = false)
    private boolean completed;

    // Required by JPA
    public Task() {
    }

    // Existing constructor
    public Task(Long id, String title, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}