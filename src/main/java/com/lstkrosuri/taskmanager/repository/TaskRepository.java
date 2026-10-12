
package com.lstkrosuri.taskmanager.repository;

import com.lstkrosuri.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
