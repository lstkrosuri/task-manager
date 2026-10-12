package com.lstkrosuri.taskmanager.repository;

import com.lstkrosuri.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Sort;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompleted(boolean completed, Sort sort);
}