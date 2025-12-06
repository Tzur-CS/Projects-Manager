package com.example.demo.project;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedAccessException;
import com.example.demo.project.dto.CreateProjectRequest;
import com.example.demo.project.dto.ProjectDTO;
import com.example.demo.project.dto.UpdateProjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Projects
 */
@Service
@Slf4j
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Get all projects with pagination (admin only)
     */
    @Transactional(readOnly = true)
    public Page<Project> getAllProjects(Pageable pageable) {
        log.debug("Fetching all projects with pagination: {}", pageable);
        return projectRepository.findAll(pageable);
    }

    /**
     * Get projects by owner with pagination
     */
    @Transactional(readOnly = true)
    public Page<Project> getProjectsByOwner(String ownerId, Pageable pageable) {
        log.debug("Fetching projects for owner: {} with pagination: {}", ownerId, pageable);
        return projectRepository.findByOwnerId(ownerId, pageable);
    }

    /**
     * Get project by ID
     */
    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        log.debug("Fetching project with id: {}", id);
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    /**
     * Get project by ID and verify ownership
     */
    @Transactional(readOnly = true)
    public Project getProjectByIdAndOwner(Long id, String ownerId) {
        log.debug("Fetching project with id: {} for owner: {}", id, ownerId);
        return projectRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new UnauthorizedAccessException("Project not found or you don't have access to this project"));
    }

    /**
     * Create a new project
     */
    public Project createProject(ProjectDTO projectDTO, String ownerId) {
        log.info("Creating new project for owner: {}", ownerId);

        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setOwnerId(ownerId);

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with id: {} owned by: {}", savedProject.getId(), savedProject.getOwnerId());
        return savedProject;
    }

    /**
     * Create a new project with CreateProjectRequest
     */
    public Project createProject(CreateProjectRequest request, String ownerId) {
        log.info("Creating new project for owner: {}", ownerId);

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwnerId(ownerId);

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with id: {} owned by: {}", savedProject.getId(), savedProject.getOwnerId());
        return savedProject;
    }

    /**
     * Update an existing project
     */
    public Project updateProject(Long id, ProjectDTO projectDTO, String ownerId, boolean isAdmin) {
        log.info("Updating project with id: {} by user: {}", id, ownerId);

        Project project;
        if (isAdmin) {
            project = getProjectById(id);
        } else {
            project = getProjectByIdAndOwner(id, ownerId);
        }

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());

        Project updatedProject = projectRepository.save(project);
        log.info("Project updated successfully with id: {}", updatedProject.getId());
        return updatedProject;
    }

    /**
     * Update an existing project with UpdateProjectRequest
     */
    public Project updateProject(Long id, UpdateProjectRequest request, String ownerId, boolean isAdmin) {
        log.info("Updating project with id: {} by user: {}", id, ownerId);

        Project project;
        if (isAdmin) {
            project = getProjectById(id);
        } else {
            project = getProjectByIdAndOwner(id, ownerId);
        }

        // Only update provided fields
        if (request.getName() != null && !request.getName().isEmpty()) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        Project updatedProject = projectRepository.save(project);
        log.info("Project updated successfully with id: {}", updatedProject.getId());
        return updatedProject;
    }

    /**
     * Delete a project
     */
    public void deleteProject(Long id, String ownerId, boolean isAdmin) {
        log.info("Deleting project with id: {} by user: {}", id, ownerId);

        Project project;
        if (isAdmin) {
            project = getProjectById(id);
        } else {
            project = getProjectByIdAndOwner(id, ownerId);
        }

        projectRepository.delete(project);
        log.info("Project deleted successfully with id: {}", id);
    }


    /**
     * Convert Project entity to DTO
     */
    public ProjectDTO convertToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setOwnerId(project.getOwnerId());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        dto.setTaskCount(project.getTasks() != null ? project.getTasks().size() : 0);
        return dto;
    }
}

