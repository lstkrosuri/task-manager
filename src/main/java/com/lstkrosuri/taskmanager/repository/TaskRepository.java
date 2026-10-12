package com.lstkrosuri.taskmanager.repository;

import com.lstkrosuri.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompleted(boolean completed, Sort sort);

    Page<Task> findByCompleted(boolean completed, Pageable pageable);
}

