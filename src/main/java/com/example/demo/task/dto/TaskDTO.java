package com.example.demo.task.dto;

import com.example.demo.task.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    @NotNull(message = "Task status is required")
    private Task.TaskStatus status;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private String assignedTo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

