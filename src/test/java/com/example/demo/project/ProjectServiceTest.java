package com.example.demo.project;

import com.example.demo.project.dto.ProjectDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProjectService
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private ProjectDTO testProjectDTO;
    private String ownerId;

    @BeforeEach
    void setUp() {
        ownerId = "test-user-123";

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setOwnerId(ownerId);

        testProjectDTO = new ProjectDTO();
        testProjectDTO.setName("Test Project");
        testProjectDTO.setDescription("Test Description");
    }

    @Test
    void getAllProjects_ShouldReturnPageOfProjects() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(Arrays.asList(testProject));
        when(projectRepository.findAll(pageable)).thenReturn(projectPage);

        // Act
        Page<Project> result = projectService.getAllProjects(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Project", result.getContent().get(0).getName());
        verify(projectRepository, times(1)).findAll(pageable);
    }

    @Test
    void getProjectsByOwner_ShouldReturnOwnerProjects() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(Arrays.asList(testProject));
        when(projectRepository.findByOwnerId(ownerId, pageable)).thenReturn(projectPage);

        // Act
        Page<Project> result = projectService.getProjectsByOwner(ownerId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ownerId, result.getContent().get(0).getOwnerId());
        verify(projectRepository, times(1)).findByOwnerId(ownerId, pageable);
    }

    @Test
    void getProjectById_WhenExists_ShouldReturnProject() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // Act
        Project result = projectService.getProjectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
        assertEquals(testProject.getName(), result.getName());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void getProjectById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.getProjectById(1L);
        });

        assertTrue(exception.getMessage().contains("Project not found"));
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void getProjectByIdAndOwner_WhenExists_ShouldReturnProject() {
        // Arrange
        when(projectRepository.findByIdAndOwnerId(1L, ownerId)).thenReturn(Optional.of(testProject));

        // Act
        Project result = projectService.getProjectByIdAndOwner(1L, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
        assertEquals(ownerId, result.getOwnerId());
        verify(projectRepository, times(1)).findByIdAndOwnerId(1L, ownerId);
    }

    @Test
    void getProjectByIdAndOwner_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(projectRepository.findByIdAndOwnerId(1L, ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.getProjectByIdAndOwner(1L, ownerId);
        });

        assertTrue(exception.getMessage().contains("Project not found or access denied"));
        verify(projectRepository, times(1)).findByIdAndOwnerId(1L, ownerId);
    }

    @Test
    void createProject_ShouldReturnSavedProject() {
        // Arrange
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        Project result = projectService.createProject(testProjectDTO, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(testProject.getName(), result.getName());
        assertEquals(ownerId, result.getOwnerId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void updateProject_AsOwner_ShouldUpdateProject() {
        // Arrange
        when(projectRepository.findByIdAndOwnerId(1L, ownerId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        testProjectDTO.setName("Updated Project");

        // Act
        Project result = projectService.updateProject(1L, testProjectDTO, ownerId, false);

        // Assert
        assertNotNull(result);
        verify(projectRepository, times(1)).findByIdAndOwnerId(1L, ownerId);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void updateProject_AsAdmin_ShouldUpdateProject() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        testProjectDTO.setName("Updated Project");

        // Act
        Project result = projectService.updateProject(1L, testProjectDTO, ownerId, true);

        // Assert
        assertNotNull(result);
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void deleteProject_AsOwner_ShouldDeleteProject() {
        // Arrange
        when(projectRepository.findByIdAndOwnerId(1L, ownerId)).thenReturn(Optional.of(testProject));
        doNothing().when(projectRepository).delete(testProject);

        // Act
        projectService.deleteProject(1L, ownerId, false);

        // Assert
        verify(projectRepository, times(1)).findByIdAndOwnerId(1L, ownerId);
        verify(projectRepository, times(1)).delete(testProject);
    }

    @Test
    void deleteProject_AsAdmin_ShouldDeleteProject() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        doNothing().when(projectRepository).delete(testProject);

        // Act
        projectService.deleteProject(1L, ownerId, true);

        // Assert
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).delete(testProject);
    }

    @Test
    void searchProjectsByName_ShouldReturnMatchingProjects() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(Arrays.asList(testProject));
        when(projectRepository.findByNameContainingIgnoreCase("Test", pageable)).thenReturn(projectPage);

        // Act
        Page<Project> result = projectService.searchProjectsByName("Test", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(projectRepository, times(1)).findByNameContainingIgnoreCase("Test", pageable);
    }

    @Test
    void convertToDTO_ShouldConvertProjectToDTO() {
        // Act
        ProjectDTO result = projectService.convertToDTO(testProject);

        // Assert
        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
        assertEquals(testProject.getName(), result.getName());
        assertEquals(testProject.getDescription(), result.getDescription());
        assertEquals(testProject.getOwnerId(), result.getOwnerId());
    }
}

