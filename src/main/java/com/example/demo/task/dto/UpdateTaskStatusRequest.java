package com.example.demo.task.dto;

import com.example.demo.task.Task;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating only the task status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskStatusRequest {

    @NotNull(message = "Status is required")
    private Task.TaskStatus status;
}

