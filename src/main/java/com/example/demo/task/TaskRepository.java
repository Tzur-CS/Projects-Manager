package com.example.demo.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    Page<Task> findByProjectIdAndStatus(Long projectId, Task.TaskStatus status, Pageable pageable);
    Page<Task> findByAssignedTo(String assignedTo, Pageable pageable);
    Page<Task> findByStatus(Task.TaskStatus status, Pageable pageable);
}

