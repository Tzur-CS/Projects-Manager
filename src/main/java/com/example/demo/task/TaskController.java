package com.example.demo.task;

import com.example.demo.task.dto.CreateTaskRequest;
import com.example.demo.task.dto.TaskDTO;
import com.example.demo.task.dto.UpdateTaskRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management APIs")
@SecurityRequirement(name = "bearer-jwt")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get all tasks (Admin only)", description = "Retrieve all tasks with pagination")
    public ResponseEntity<Page<TaskDTO>> getAllTasks(@PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching all tasks");
        Page<Task> tasks = taskService.getAllTasks(pageable);
        Page<TaskDTO> taskDTOs = tasks.map(taskService::convertToDTO);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by project", description = "Retrieve all tasks for a specific project")
    public ResponseEntity<Page<TaskDTO>> getTasksByProject(
            @PathVariable Long projectId,
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Fetching tasks for project: {}", projectId);
        Page<Task> tasks = taskService.getTasksByProject(projectId, userId, isAdmin, pageable);
        Page<TaskDTO> taskDTOs = tasks.map(taskService::convertToDTO);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/project/{projectId}/status/{status}")
    @Operation(summary = "Get tasks by project and status", description = "Retrieve tasks for a project filtered by status")
    public ResponseEntity<Page<TaskDTO>> getTasksByProjectAndStatus(
            @PathVariable Long projectId,
            @PathVariable Task.TaskStatus status,
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Fetching tasks for project: {} with status: {}", projectId, status);
        Page<Task> tasks = taskService.getTasksByProjectAndStatus(projectId, status, userId, isAdmin, pageable);
        Page<TaskDTO> taskDTOs = tasks.map(taskService::convertToDTO);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/my-tasks")
    @Operation(summary = "Get assigned tasks", description = "Retrieve tasks assigned to the authenticated user")
    public ResponseEntity<Page<TaskDTO>> getMyTasks(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        String userId = getUserId(authentication);
        log.info("Fetching tasks assigned to user: {}", userId);
        Page<Task> tasks = taskService.getTasksByAssignedUser(userId, pageable);
        Page<TaskDTO> taskDTOs = tasks.map(taskService::convertToDTO);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get tasks by status (Admin only)", description = "Retrieve all tasks with a specific status")
    public ResponseEntity<Page<TaskDTO>> getTasksByStatus(
            @PathVariable Task.TaskStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching tasks with status: {}", status);
        Page<Task> tasks = taskService.getTasksByStatus(status, pageable);
        Page<TaskDTO> taskDTOs = tasks.map(taskService::convertToDTO);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    public ResponseEntity<TaskDTO> getTaskById(
            @PathVariable Long id,
            Authentication authentication) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Fetching task with id: {}", id);
        Task task = taskService.getTaskById(id);

        if (!isAdmin) {
            taskService.getTasksByProject(task.getProject().getId(), userId, false, Pageable.unpaged());
        }

        return ResponseEntity.ok(taskService.convertToDTO(task));
    }

    @PostMapping
    @Operation(summary = "Create a new task", description = "Create a new task within a project")
    public ResponseEntity<TaskDTO> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Creating new task for project: {}", request.getProjectId());
        Task task = taskService.createTask(request, userId, isAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.convertToDTO(task));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Update task fields (title, description, status, assignedTo)")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Updating task with id: {}", id);
        Task task = taskService.updateTask(id, request, userId, isAdmin);
        return ResponseEntity.ok(taskService.convertToDTO(task));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Update the status of a task")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam Task.TaskStatus status,
            Authentication authentication) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Updating task status for id: {} to: {}", id, status);
        Task task = taskService.updateTaskStatus(id, status, userId, isAdmin);
        return ResponseEntity.ok(taskService.convertToDTO(task));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Delete an existing task")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Deleting task with id: {}", id);
        taskService.deleteTask(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    private String getUserId(Authentication authentication) {
        return ((Jwt) authentication.getPrincipal()).getSubject();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}

