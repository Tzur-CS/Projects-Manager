package com.example.demo.project;

import com.example.demo.project.dto.CreateProjectRequest;
import com.example.demo.project.dto.ProjectDTO;
import com.example.demo.project.dto.UpdateProjectRequest;
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

/**
 * REST controller for managing Projects
 */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project management APIs")
@SecurityRequirement(name = "bearer-jwt")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get all projects (Admin only)", description = "Retrieve all projects with pagination")
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching all projects");
        Page<Project> projects = projectService.getAllProjects(pageable);
        Page<ProjectDTO> projectDTOs = projects.map(projectService::convertToDTO);
        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/my-projects")
    @Operation(summary = "Get user's projects", description = "Retrieve projects owned by the authenticated user")
    public ResponseEntity<Page<ProjectDTO>> getMyProjects(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        String userId = getUserId(authentication);
        log.info("Fetching projects for user: {}", userId);
        Page<Project> projects = projectService.getProjectsByOwner(userId, pageable);
        Page<ProjectDTO> projectDTOs = projects.map(projectService::convertToDTO);
        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieve a specific project by its ID")
    public ResponseEntity<ProjectDTO> getProjectById(
            @PathVariable Long id,
            Authentication authentication) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Fetching project with id: {} for user: {}", id, userId);

        Project project = isAdmin ? projectService.getProjectById(id)
                                  : projectService.getProjectByIdAndOwner(id, userId);

        return ResponseEntity.ok(projectService.convertToDTO(project));
    }

    @PostMapping
    @Operation(summary = "Create a new project", description = "Create a new project with name and description")
    public ResponseEntity<ProjectDTO> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {
        String userId = getUserId(authentication);
        log.info("Creating new project for user: {}", userId);
        Project project = projectService.createProject(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.convertToDTO(project));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a project", description = "Update project name and/or description")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request,
            Authentication authentication) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Updating project with id: {} by user: {}", id, userId);
        Project project = projectService.updateProject(id, request, userId, isAdmin);
        return ResponseEntity.ok(projectService.convertToDTO(project));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project", description = "Delete an existing project")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {
        String userId = getUserId(authentication);
        boolean isAdmin = hasRole(authentication, "admin");
        log.info("Deleting project with id: {} by user: {}", id, userId);

        projectService.deleteProject(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search projects by name", description = "Search projects by name with pagination")
    public ResponseEntity<Page<ProjectDTO>> searchProjects(
            @RequestParam String name,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Searching projects by name: {}", name);
        Page<Project> projects = projectService.searchProjectsByName(name, pageable);
        Page<ProjectDTO> projectDTOs = projects.map(projectService::convertToDTO);
        return ResponseEntity.ok(projectDTOs);
    }

    private String getUserId(Authentication authentication) {
        return ((Jwt) authentication.getPrincipal()).getSubject();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}

