package com.example.demo.project;

import com.example.demo.project.dto.ProjectDTO;
import com.example.demo.security.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only REST controller for managing all projects
 * All endpoints verify admin role from database using AuthorizationService
 */
@RestController
@RequestMapping("/api/admin/projects")
@Tag(name = "Admin - Projects", description = "Admin APIs for project management")
@SecurityRequirement(name = "bearer-jwt")
@Slf4j
public class AdminProjectController {

    private final ProjectService projectService;
    private final AuthorizationService authorizationService;

    @Autowired
    public AdminProjectController(ProjectService projectService, AuthorizationService authorizationService) {
        this.projectService = projectService;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieve all projects in the system with pagination")
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        authorizationService.requireAdmin(authentication);
        log.info("Admin fetching all projects");
        Page<Project> projects = projectService.getAllProjects(pageable);
        Page<ProjectDTO> projectDTOs = projects.map(projectService::convertToDTO);
        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get projects by owner", description = "Retrieve all projects owned by a specific user")
    public ResponseEntity<Page<ProjectDTO>> getProjectsByOwner(
            @PathVariable String ownerId,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        authorizationService.requireAdmin(authentication);
        log.info("Admin fetching projects for owner: {}", ownerId);
        Page<Project> projects = projectService.getProjectsByOwner(ownerId, pageable);
        Page<ProjectDTO> projectDTOs = projects.map(projectService::convertToDTO);
        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieve any project by its ID")
    public ResponseEntity<ProjectDTO> getProjectById(
            @PathVariable Long id,
            Authentication authentication) {
        authorizationService.requireAdmin(authentication);
        log.info("Admin fetching project with id: {}", id);
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(projectService.convertToDTO(project));
    }
}

