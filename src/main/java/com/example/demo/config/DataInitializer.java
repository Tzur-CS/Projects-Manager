package com.example.demo.config;

import com.example.demo.project.Project;
import com.example.demo.project.ProjectRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Database initialization with real user data
 * Creates 2 users (Tzur and Eden) with their actual Cognito sub IDs in addition to the admin user (Moveo)
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    // Real Cognito Sub IDs
    private static final String TZUR_SUB = "f2c534c4-e011-70e8-7bc9-d2381a8864bc";
    private static final String EDEN_SUB = "d2b54484-d0b1-70bf-91da-77cb54986783";
    private static final String MOVEO_SUB = "4235a444-d0a1-70b2-f43e-0e4790f07064";

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   ProjectRepository projectRepository,
                                   TaskRepository taskRepository) {
        return args -> {
            // Check if data already exists
            if (userRepository.count() > 0) {
                log.info("Database already contains data. Skipping initialization.");
                return;
            }

            log.info("Initializing database with user data for Tzur, Eden, and Moveo...");

            // ==================== Create Users ====================

            User tzur = new User();
            tzur.setUsername("tzur");
            tzur.setCognitoSub(TZUR_SUB);
            tzur.setRole(User.UserRole.USER);
            userRepository.save(tzur);
            log.info("Created user: Tzur ({}) - Role: USER", TZUR_SUB);

            User eden = new User();
            eden.setUsername("eden");
            eden.setCognitoSub(EDEN_SUB);
            eden.setRole(User.UserRole.USER);
            userRepository.save(eden);
            log.info("Created user: Eden ({}) - Role: USER", EDEN_SUB);

            User moveo = new User();
            moveo.setUsername("moveo");
            moveo.setCognitoSub(MOVEO_SUB);
            moveo.setRole(User.UserRole.ADMIN);
            userRepository.save(moveo);
            log.info("Created admin user: Moveo ({}) - Role: ADMIN", MOVEO_SUB);

            // ==================== Create Projects for Tzur ====================

            Project tzurProject1 = new Project();
            tzurProject1.setName("Tzur Project 1");
            tzurProject1.setDescription("Tzur's first project");
            tzurProject1.setOwnerId(TZUR_SUB);
            tzurProject1 = projectRepository.save(tzurProject1);
            log.info("Created project: {} for Tzur", tzurProject1.getName());

            createTask(taskRepository, tzurProject1, "Tzur Task 1", "Task 1 description", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Tzur Task 2", "Task 2 description", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Tzur Task 3", "Task 3 description", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Tzur Task 4", "Task 4 description", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Tzur Task 5", "Task 5 description", Task.TaskStatus.TODO, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Tzur Task 6", "Task 6 description", Task.TaskStatus.TODO, TZUR_SUB);

            Project tzurProject2 = new Project();
            tzurProject2.setName("Tzur Project 2");
            tzurProject2.setDescription("Tzur's second project");
            tzurProject2.setOwnerId(TZUR_SUB);
            tzurProject2 = projectRepository.save(tzurProject2);
            log.info("Created project: {} for Tzur", tzurProject2.getName());

            createTask(taskRepository, tzurProject2, "Tzur Task 7", "Task 7 description", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject2, "Tzur Task 8", "Task 8 description", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject2, "Tzur Task 9", "Task 9 description", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject2, "Tzur Task 10", "Task 10 description", Task.TaskStatus.TODO, TZUR_SUB);
            createTask(taskRepository, tzurProject2, "Tzur Task 11", "Task 11 description", Task.TaskStatus.TODO, TZUR_SUB);

            Project tzurProject3 = new Project();
            tzurProject3.setName("Tzur Project 3");
            tzurProject3.setDescription("Tzur's third project");
            tzurProject3.setOwnerId(TZUR_SUB);
            tzurProject3 = projectRepository.save(tzurProject3);
            log.info("Created project: {} for Tzur", tzurProject3.getName());

            createTask(taskRepository, tzurProject3, "Tzur Task 12", "Task 12 description", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject3, "Tzur Task 13", "Task 13 description", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject3, "Tzur Task 14", "Task 14 description", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject3, "Tzur Task 15", "Task 15 description", Task.TaskStatus.TODO, TZUR_SUB);
            createTask(taskRepository, tzurProject3, "Tzur Task 16", "Task 16 description", Task.TaskStatus.TODO, TZUR_SUB);
            createTask(taskRepository, tzurProject3, "Tzur Task 17", "Task 17 description", Task.TaskStatus.TODO, TZUR_SUB);

            // ==================== Create Projects for Eden ====================

            Project edenProject1 = new Project();
            edenProject1.setName("Eden Project 1");
            edenProject1.setDescription("Eden's first project");
            edenProject1.setOwnerId(EDEN_SUB);
            edenProject1 = projectRepository.save(edenProject1);
            log.info("Created project: {} for Eden", edenProject1.getName());

            createTask(taskRepository, edenProject1, "Eden Task 1", "Task 1 description", Task.TaskStatus.DONE, EDEN_SUB);
            createTask(taskRepository, edenProject1, "Eden Task 2", "Task 2 description", Task.TaskStatus.IN_PROGRESS, EDEN_SUB);
            createTask(taskRepository, edenProject1, "Eden Task 3", "Task 3 description", Task.TaskStatus.TODO, EDEN_SUB);
            createTask(taskRepository, edenProject1, "Eden Task 4", "Task 4 description", Task.TaskStatus.TODO, EDEN_SUB);

            Project edenProject2 = new Project();
            edenProject2.setName("Eden Project 2");
            edenProject2.setDescription("Eden's second project");
            edenProject2.setOwnerId(EDEN_SUB);
            edenProject2 = projectRepository.save(edenProject2);
            log.info("Created project: {} for Eden", edenProject2.getName());

            createTask(taskRepository, edenProject2, "Eden Task 5", "Task 5 description", Task.TaskStatus.IN_PROGRESS, EDEN_SUB);
            createTask(taskRepository, edenProject2, "Eden Task 6", "Task 6 description", Task.TaskStatus.TODO, EDEN_SUB);
            createTask(taskRepository, edenProject2, "Eden Task 7", "Task 7 description", Task.TaskStatus.TODO, EDEN_SUB);

            Project edenProject3 = new Project();
            edenProject3.setName("Eden Project 3");
            edenProject3.setDescription("Eden's third project");
            edenProject3.setOwnerId(EDEN_SUB);
            edenProject3 = projectRepository.save(edenProject3);
            log.info("Created project: {} for Eden", edenProject3.getName());

            createTask(taskRepository, edenProject3, "Eden Task 8", "Task 8 description", Task.TaskStatus.DONE, EDEN_SUB);
            createTask(taskRepository, edenProject3, "Eden Task 9", "Task 9 description", Task.TaskStatus.IN_PROGRESS, EDEN_SUB);
            createTask(taskRepository, edenProject3, "Eden Task 10", "Task 10 description", Task.TaskStatus.TODO, EDEN_SUB);
            createTask(taskRepository, edenProject3, "Eden Task 11", "Task 11 description", Task.TaskStatus.TODO, EDEN_SUB);
            createTask(taskRepository, edenProject3, "Eden Task 12", "Task 12 description", Task.TaskStatus.TODO, EDEN_SUB);

            // ==================== Summary ====================

            long userCount = userRepository.count();
            long projectCount = projectRepository.count();
            long taskCount = taskRepository.count();

            log.info("==============================================");
            log.info("Database initialization completed successfully!");
            log.info("Created {} users", userCount);
            log.info("Created {} projects", projectCount);
            log.info("Created {} tasks", taskCount);
            log.info("==============================================");
            log.info("User: Tzur ({}) - Role: USER", TZUR_SUB);
            log.info("  - 3 projects (Tzur Project 1-3)");
            log.info("  - 17 tasks (Tzur Task 1-17)");
            log.info("User: Eden ({}) - Role: USER", EDEN_SUB);
            log.info("  - 3 projects (Eden Project 1-3)");
            log.info("  - 12 tasks (Eden Task 1-12)");
            log.info("Admin: Moveo ({}) - Role: ADMIN", MOVEO_SUB);
            log.info("  - Full admin access to all projects and tasks");
            log.info("==============================================");
            log.info("✅ Each user will see ONLY their own projects when authenticated!");
            log.info("✅ Moveo (admin) can see and manage ALL projects and tasks!");
            log.info("==============================================");
        };
    }

    private void createTask(TaskRepository taskRepository, Project project,
                           String title, String description,
                           Task.TaskStatus status, String assignedTo) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setProject(project);

        // Auto-assign to project owner if assignedTo is null
        if (assignedTo != null && !assignedTo.isEmpty()) {
            task.setAssignedTo(assignedTo);
        } else {
            task.setAssignedTo(project.getOwnerId());
            log.debug("Task '{}' auto-assigned to project owner: {}", title, project.getOwnerId());
        }

        taskRepository.save(task);
        log.debug("Created task: {} ({}) - assigned to: {}", title, status, task.getAssignedTo());
    }
}

