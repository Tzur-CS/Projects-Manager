package com.example.demo.task.dto;

import com.example.demo.task.Task;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Task.TaskStatus status;

    private String assignedTo;
}

