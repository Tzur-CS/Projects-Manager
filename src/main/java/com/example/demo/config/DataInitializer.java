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
 * Creates 2 users (Tzur and Eden) with their actual Cognito sub IDs
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
            tzur.setName("Tzur");
            tzur.setEmail("tzur@example.com");
            tzur.setUsername("tzur");
            tzur.setCognitoSub(TZUR_SUB);
            tzur.setRole(User.UserRole.USER);
            tzur = userRepository.save(tzur);
            log.info("Created user: Tzur ({}) - Role: USER", TZUR_SUB);

            User eden = new User();
            eden.setName("Eden");
            eden.setEmail("eden@example.com");
            eden.setUsername("eden");
            eden.setCognitoSub(EDEN_SUB);
            eden.setRole(User.UserRole.USER);
            eden = userRepository.save(eden);
            log.info("Created user: Eden ({}) - Role: USER", EDEN_SUB);

            User moveo = new User();
            moveo.setName("Moveo");
            moveo.setEmail("moveo@example.com");
            moveo.setUsername("moveo");
            moveo.setCognitoSub(MOVEO_SUB);
            moveo.setRole(User.UserRole.ADMIN);
            moveo = userRepository.save(moveo);
            log.info("Created admin user: Moveo ({}) - Role: ADMIN", MOVEO_SUB);

            // ==================== Create Projects for Tzur ====================
            // Tzur has 3 projects with tasks

            Project tzurProject1 = new Project();
            tzurProject1.setName("Tzur project 1");
            tzurProject1.setDescription("Building a modern Tzur project 1 with React and Spring Boot");
            tzurProject1.setOwnerId(TZUR_SUB);
            tzurProject1 = projectRepository.save(tzurProject1);
            log.info("Created project: {} for Tzur", tzurProject1.getName());

            // Tasks for Tzur project 1
            createTask(taskRepository, tzurProject1, "Setup React Frontend",
                      "Initialize React app with TypeScript and Vite", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Design Database Schema",
                      "Create ER diagram and MySQL tables", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Implement Product Catalog",
                      "Build product listing and detail pages", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Add Shopping Cart",
                      "Implement cart functionality with Redux", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Payment Integration",
                      "Integrate Stripe payment gateway", Task.TaskStatus.TODO, TZUR_SUB);
            createTask(taskRepository, tzurProject1, "Order Management",
                      "Admin panel for order processing", Task.TaskStatus.TODO, null);

            Project tzurProject2 = new Project();
            tzurProject2.setName("Tzur project 2");
            tzurProject2.setDescription("Cross-platform mobile app for task management using React Native");
            tzurProject2.setOwnerId(TZUR_SUB);
            tzurProject2 = projectRepository.save(tzurProject2);
            log.info("Created project: {} for Tzur", tzurProject2.getName());

            // Tasks for Tzur project 2
            createTask(taskRepository, tzurProject2, "Setup React Native",
                      "Initialize React Native project with Expo", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject2, "Design UI Screens",
                      "Create mockups for all app screens", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject2, "Implement Authentication",
                      "Add login/signup with AWS Cognito", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject2, "Task CRUD Operations",
                      "Create, read, update, delete tasks", Task.TaskStatus.TODO, null);
            createTask(taskRepository, tzurProject2, "Push Notifications",
                      "Implement push notifications for task reminders", Task.TaskStatus.TODO, null);

            Project tzurProject3 = new Project();
            tzurProject3.setName("Tzur project 3");
            tzurProject3.setDescription("Modernize company website with new branding and improved UX");
            tzurProject3.setOwnerId(TZUR_SUB);
            tzurProject3 = projectRepository.save(tzurProject3);
            log.info("Created project: {} for Tzur", tzurProject3.getName());

            // Tasks for Website Redesign
            createTask(taskRepository, tzurProject3, "Gather Requirements",
                      "Interview stakeholders and collect requirements", Task.TaskStatus.DONE, TZUR_SUB);
            createTask(taskRepository, tzurProject3, "Create Wireframes",
                      "Design wireframes for all pages", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject3, "Content Writing",
                      "Write copy for all website sections", Task.TaskStatus.IN_PROGRESS, TZUR_SUB);
            createTask(taskRepository, tzurProject3, "Frontend Development",
                      "Implement responsive design with Tailwind CSS", Task.TaskStatus.TODO, null);
            createTask(taskRepository, tzurProject3, "SEO Optimization",
                      "Optimize for search engines", Task.TaskStatus.TODO, null);
            createTask(taskRepository, tzurProject3, "Performance Testing",
                      "Test and optimize website performance", Task.TaskStatus.TODO, null);

            // ==================== Create Projects for Eden ====================
            // Eden has 3 projects with tasks

            Project edenProject1 = new Project();
            edenProject1.setName("Customer Analytics Dashboard");
            edenProject1.setDescription("Real-time analytics dashboard for customer behavior tracking");
            edenProject1.setOwnerId(EDEN_SUB);
            edenProject1 = projectRepository.save(edenProject1);
            log.info("Created project: {} for Eden", edenProject1.getName());

            // Tasks for Analytics Dashboard
            createTask(taskRepository, edenProject1, "Setup Data Pipeline",
                      "Configure data collection and ETL process", Task.TaskStatus.DONE, EDEN_SUB);
            createTask(taskRepository, edenProject1, "Design Dashboard UI",
                      "Create interactive charts with Chart.js", Task.TaskStatus.IN_PROGRESS, EDEN_SUB);
            createTask(taskRepository, edenProject1, "Implement Real-time Updates",
                      "Add WebSocket for live data updates", Task.TaskStatus.TODO, EDEN_SUB);
            createTask(taskRepository, edenProject1, "User Segmentation",
                      "Build user segmentation and filtering", Task.TaskStatus.TODO, null);

            Project edenProject2 = new Project();
            edenProject2.setName("Internal HR Portal");
            edenProject2.setDescription("Employee self-service portal for HR operations");
            edenProject2.setOwnerId(EDEN_SUB);
            edenProject2 = projectRepository.save(edenProject2);
            log.info("Created project: {} for Eden", edenProject2.getName());

            // Tasks for HR Portal
            createTask(taskRepository, edenProject2, "Employee Profile Management",
                      "Allow employees to update their information", Task.TaskStatus.IN_PROGRESS, EDEN_SUB);
            createTask(taskRepository, edenProject2, "Leave Request System",
                      "Implement leave request and approval workflow", Task.TaskStatus.TODO, EDEN_SUB);
            createTask(taskRepository, edenProject2, "Payslip Generation",
                      "Automatic monthly payslip generation", Task.TaskStatus.TODO, null);

            Project edenProject3 = new Project();
            edenProject3.setName("Inventory Management System");
            edenProject3.setDescription("Warehouse inventory tracking and management system");
            edenProject3.setOwnerId(EDEN_SUB);
            edenProject3 = projectRepository.save(edenProject3);
            log.info("Created project: {} for Eden", edenProject3.getName());

            // Tasks for Inventory System
            createTask(taskRepository, edenProject3, "Database Design",
                      "Design inventory database schema", Task.TaskStatus.DONE, EDEN_SUB);
            createTask(taskRepository, edenProject3, "Barcode Scanning",
                      "Implement barcode scanning functionality", Task.TaskStatus.IN_PROGRESS, EDEN_SUB);
            createTask(taskRepository, edenProject3, "Stock Alerts",
                      "Low stock notification system", Task.TaskStatus.TODO, null);
            createTask(taskRepository, edenProject3, "Reports Generation",
                      "Generate inventory reports (PDF/Excel)", Task.TaskStatus.TODO, null);
            createTask(taskRepository, edenProject3, "Supplier Management",
                      "Manage supplier information and orders", Task.TaskStatus.TODO, null);

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
            log.info("  - 3 projects with 17 tasks");
            log.info("User: Eden ({}) - Role: USER", EDEN_SUB);
            log.info("  - 3 projects with 12 tasks");
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

