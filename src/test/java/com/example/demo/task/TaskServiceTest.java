package com.example.demo.task;

import com.example.demo.project.Project;
import com.example.demo.project.ProjectService;
import com.example.demo.task.dto.CreateTaskRequest;
import com.example.demo.task.dto.TaskDTO;
import com.example.demo.task.dto.UpdateTaskRequest;
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
 * Unit tests for TaskService
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskDTO testTaskDTO;
    private Project testProject;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = "test-user-123";

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setOwnerId(userId);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.TODO);
        testTask.setProject(testProject);
        testTask.setAssignedTo(userId);

        testTaskDTO = new TaskDTO();
        testTaskDTO.setTitle("Test Task");
        testTaskDTO.setDescription("Test Description");
        testTaskDTO.setStatus(Task.TaskStatus.TODO);
        testTaskDTO.setProjectId(1L);
        testTaskDTO.setAssignedTo(userId);
    }

    @Test
    void getAllTasks_ShouldReturnPageOfTasks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        // Act
        Page<Task> result = taskService.getAllTasks(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    void getTasksByProject_AsOwner_ShouldReturnTasks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(projectService.getProjectByIdAndOwner(1L, userId)).thenReturn(testProject);
        when(taskRepository.findByProjectId(1L, pageable)).thenReturn(taskPage);

        // Act
        Page<Task> result = taskService.getTasksByProject(1L, userId, false, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getProject().getId());
        verify(projectService, times(1)).getProjectByIdAndOwner(1L, userId);
        verify(taskRepository, times(1)).findByProjectId(1L, pageable);
    }

    @Test
    void getTasksByProject_AsAdmin_ShouldSkipOwnershipCheck() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findByProjectId(1L, pageable)).thenReturn(taskPage);

        // Act
        Page<Task> result = taskService.getTasksByProject(1L, userId, true, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(projectService, never()).getProjectByIdAndOwner(anyLong(), anyString());
        verify(taskRepository, times(1)).findByProjectId(1L, pageable);
    }

    @Test
    void getTasksByStatus_ShouldReturnTasksWithStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findByStatus(Task.TaskStatus.TODO, pageable)).thenReturn(taskPage);

        // Act
        Page<Task> result = taskService.getTasksByStatus(Task.TaskStatus.TODO, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(Task.TaskStatus.TODO, result.getContent().get(0).getStatus());
        verify(taskRepository, times(1)).findByStatus(Task.TaskStatus.TODO, pageable);
    }

    @Test
    void getTasksByProjectAndStatus_ShouldReturnFilteredTasks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(projectService.getProjectByIdAndOwner(1L, userId)).thenReturn(testProject);
        when(taskRepository.findByProjectIdAndStatus(1L, Task.TaskStatus.TODO, pageable)).thenReturn(taskPage);

        // Act
        Page<Task> result = taskService.getTasksByProjectAndStatus(1L, Task.TaskStatus.TODO, userId, false, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(Task.TaskStatus.TODO, result.getContent().get(0).getStatus());
        verify(projectService, times(1)).getProjectByIdAndOwner(1L, userId);
        verify(taskRepository, times(1)).findByProjectIdAndStatus(1L, Task.TaskStatus.TODO, pageable);
    }

    @Test
    void getTasksByAssignedUser_ShouldReturnUserTasks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findByAssignedTo(userId, pageable)).thenReturn(taskPage);

        // Act
        Page<Task> result = taskService.getTasksByAssignedUser(userId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userId, result.getContent().get(0).getAssignedTo());
        verify(taskRepository, times(1)).findByAssignedTo(userId, pageable);
    }

    @Test
    void getTaskById_WhenExists_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // Act
        Task result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void createTask_AsOwner_ShouldReturnSavedTask() {
        // Arrange
        when(projectService.getProjectByIdAndOwner(1L, userId)).thenReturn(testProject);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        CreateTaskRequest createRequest = new CreateTaskRequest(
                testTaskDTO.getTitle(),
                testTaskDTO.getDescription(),
                testTaskDTO.getProjectId(),
                testTaskDTO.getStatus(),
                testTaskDTO.getAssignedTo()
        );

        // Act
        Task result = taskService.createTask(createRequest, userId, false);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testProject, result.getProject());
        verify(projectService, times(1)).getProjectByIdAndOwner(1L, userId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_AsAdmin_ShouldReturnSavedTask() {
        // Arrange
        when(projectService.getProjectById(1L)).thenReturn(testProject);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        CreateTaskRequest createRequest = new CreateTaskRequest(
                testTaskDTO.getTitle(),
                testTaskDTO.getDescription(),
                testTaskDTO.getProjectId(),
                testTaskDTO.getStatus(),
                testTaskDTO.getAssignedTo()
        );

        // Act
        Task result = taskService.createTask(createRequest, userId, true);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(projectService, times(1)).getProjectById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_AsProjectOwner_ShouldUpdateTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(projectService.getProjectByIdAndOwner(1L, userId)).thenReturn(testProject);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        testTaskDTO.setTitle("Updated Task");
        UpdateTaskRequest updateRequest = new UpdateTaskRequest(
                testTaskDTO.getTitle(),
                testTaskDTO.getDescription(),
                testTaskDTO.getStatus(),
                testTaskDTO.getAssignedTo()
        );

        // Act
        Task result = taskService.updateTask(1L, updateRequest, userId, false);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(1L);
        verify(projectService, times(1)).getProjectByIdAndOwner(1L, userId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatus() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(projectService.getProjectByIdAndOwner(1L, userId)).thenReturn(testProject);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        Task result = taskService.updateTaskStatus(1L, Task.TaskStatus.IN_PROGRESS, userId, false);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(1L);
        verify(projectService, times(1)).getProjectByIdAndOwner(1L, userId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_AsProjectOwner_ShouldDeleteTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(projectService.getProjectByIdAndOwner(1L, userId)).thenReturn(testProject);
        doNothing().when(taskRepository).delete(testTask);

        // Act
        taskService.deleteTask(1L, userId, false);

        // Assert
        verify(taskRepository, times(1)).findById(1L);
        verify(projectService, times(1)).getProjectByIdAndOwner(1L, userId);
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    void convertToDTO_ShouldConvertTaskToDTO() {
        // Act
        TaskDTO result = taskService.convertToDTO(testTask);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());
        assertEquals(testTask.getProject().getId(), result.getProjectId());
        assertEquals(testTask.getAssignedTo(), result.getAssignedTo());
    }
}
