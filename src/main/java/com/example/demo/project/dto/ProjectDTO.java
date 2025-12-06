package com.example.demo.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Project
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    private String ownerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer taskCount;
}

