package com.example.demo.task;

import com.example.demo.security.AuthorizationService;
import com.example.demo.task.dto.TaskDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only REST controller for managing all tasks
 * All endpoints verify admin role from database using AuthorizationService
 */
@RestController
@RequestMapping("/api/admin/tasks")
@Tag(name = "Admin - Tasks", description = "Admin APIs for task management")
@SecurityRequirement(name = "bearer-jwt")
@Slf4j
public class AdminTaskController {

    private final TaskService taskService;
    private final AuthorizationService authorizationService;

    @Autowired
    public AdminTaskController(TaskService taskService, AuthorizationService authorizationService) {
        this.taskService = taskService;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve all tasks in the system with pagination")
    public ResponseEntity<Page<TaskDTO>> getAllTasks(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        authorizationService.requireAdmin(authentication);
        log.info("Admin fetching all tasks");
        Page<Task> tasks = taskService.getAllTasks(pageable);
        Page<TaskDTO> taskDTOs = tasks.map(taskService::convertToDTO);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get tasks by user", description = "Retrieve all tasks assigned to a specific user")
    public ResponseEntity<Page<TaskDTO>> getTasksByUser(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        authorizationService.requireAdmin(authentication);
        log.info("Admin fetching tasks for user: {}", userId);
        Page<Task> tasks = taskService.getTasksByAssignedUser(userId, pageable);
        Page<TaskDTO> taskDTOs = tasks.map(taskService::convertToDTO);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve any task by its ID")
    public ResponseEntity<TaskDTO> getTaskById(
            @PathVariable Long id,
            Authentication authentication) {
        authorizationService.requireAdmin(authentication);
        log.info("Admin fetching task with id: {}", id);
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(taskService.convertToDTO(task));
    }
}

