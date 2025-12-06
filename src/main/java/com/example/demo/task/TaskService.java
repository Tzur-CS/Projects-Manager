package com.example.demo.task;

import com.example.demo.project.Project;
import com.example.demo.project.ProjectService;
import com.example.demo.task.dto.CreateTaskRequest;
import com.example.demo.task.dto.TaskDTO;
import com.example.demo.task.dto.UpdateTaskRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
    }

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasksByProject(Long projectId, String userId, boolean isAdmin, Pageable pageable) {
        if (!isAdmin) {
            projectService.getProjectByIdAndOwner(projectId, userId);
        }
        return taskRepository.findByProjectId(projectId, pageable);
    }

    public Page<Task> getTasksByProjectAndStatus(Long projectId, Task.TaskStatus status, String userId, boolean isAdmin, Pageable pageable) {
        if (!isAdmin) {
            projectService.getProjectByIdAndOwner(projectId, userId);
        }
        return taskRepository.findByProjectIdAndStatus(projectId, status, pageable);
    }

    public Page<Task> getTasksByAssignedUser(String userId, Pageable pageable) {
        return taskRepository.findByAssignedTo(userId, pageable);
    }

    public Page<Task> getTasksByStatus(Task.TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
    }

    public Task createTask(CreateTaskRequest request, String userId, boolean isAdmin) {
        log.info("Creating new task for project: {}", request.getProjectId());

        Project project = isAdmin ? projectService.getProjectById(request.getProjectId())
                                  : projectService.getProjectByIdAndOwner(request.getProjectId(), userId);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.TODO);
        task.setProject(project);

        if (request.getAssignedTo() != null && !request.getAssignedTo().isEmpty()) {
            task.setAssignedTo(request.getAssignedTo());
        } else {
            task.setAssignedTo(userId);
        }

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());
        return savedTask;
    }

    public Task updateTask(Long id, UpdateTaskRequest request, String userId, boolean isAdmin) {
        log.info("Updating task with id: {}", id);

        Task task = getTaskById(id);

        if (!isAdmin) {
            projectService.getProjectByIdAndOwner(task.getProject().getId(), userId);
        }

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getAssignedTo() != null) {
            task.setAssignedTo(request.getAssignedTo());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with id: {}", updatedTask.getId());
        return updatedTask;
    }

    public Task updateTaskStatus(Long id, Task.TaskStatus status, String userId, boolean isAdmin) {
        log.info("Updating task status for task id: {} to status: {}", id, status);

        Task task = getTaskById(id);

        if (!isAdmin) {
            projectService.getProjectByIdAndOwner(task.getProject().getId(), userId);
        }

        task.setStatus(status);

        Task updatedTask = taskRepository.save(task);
        log.info("Task status updated successfully");
        return updatedTask;
    }

    public void deleteTask(Long id, String userId, boolean isAdmin) {
        log.info("Deleting task with id: {}", id);

        Task task = getTaskById(id);

        if (!isAdmin) {
            projectService.getProjectByIdAndOwner(task.getProject().getId(), userId);
        }

        taskRepository.delete(task);
        log.info("Task deleted successfully");
    }

    public TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setProjectId(task.getProject().getId());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }
}

