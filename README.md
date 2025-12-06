# Projects Manager - Task Management System

A full-stack web application for managing projects and tasks with user authentication, built with Spring Boot (Java) backend and React (TypeScript) frontend.

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)

## 🎯 Overview

This is a full-stack task management application that allows users to:
- Create and manage projects
- Create and assign tasks within projects
- Track task status and priorities
- User authentication via AWS Cognito
- RESTful API backend with Swagger documentation

## ✨ Features

### Backend (Spring Boot)
- RESTful API with CRUD operations for Projects and Tasks
- User management and authentication
- AWS Cognito integration for secure authentication
- MySQL database integration
- Health check endpoints
- Swagger UI for API documentation
- Multiple environment profiles (local, dev, prod)

### Frontend (React + TypeScript)
- Modern React with TypeScript and Vite
- Material-UI components for sleek UI
- React Query for efficient data fetching and caching
- React Router for navigation
- Axios for HTTP requests
- Toast notifications for user feedback
- Health check monitoring

## 🛠 Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA** - Database operations
- **MySQL** - Database
- **AWS Cognito** - Authentication
- **Maven** - Dependency management
- **Swagger/OpenAPI** - API documentation

### Frontend
- **React 18** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Material-UI (MUI)** - Component library
- **React Query (TanStack Query)** - Data fetching
- **React Router** - Routing
- **Axios** - HTTP client
- **React Toastify** - Notifications

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

### Required
- **Java Development Kit (JDK) 17** or higher
- **Node.js 18+** and **npm**
- **MySQL 8.0+** or compatible database
- **Maven 3.6+** (or use the included Maven wrapper)
- **Git** (for version control)

### Optional
- AWS Cognito account (for authentication features)

## 📥 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/Tzur-CS/Projects-Manager.git
cd Projects-Manager
```

### 2. Backend Setup

#### Configure Database
1. Make sure MySQL is running on your machine
2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moveo-db?createDatabaseIfNotExist=true
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

#### Configure AWS Cognito (Optional)
If using authentication, update in `application.properties`:
```properties
aws.cognito.userPoolId=YOUR_USER_POOL_ID
aws.cognito.clientId=YOUR_CLIENT_ID
aws.cognito.region=YOUR_REGION
```

#### Install Backend Dependencies
```bash
mvn clean install
```

### 3. Frontend Setup

#### Navigate to Frontend Directory
```bash
cd frontend-code/my-react-app
```

#### Install Dependencies
```bash
npm install
```

#### Configure Backend API URL
Update `src/config.ts` with your backend URL if needed:
```typescript
export const API_BASE_URL = 'http://localhost:8080';
```

## 🚀 Running the Application

### Method 1: Using Batch Scripts (Windows)

#### Start Backend - Local Mode (No Authentication)
```bash
# From project root
.\start-local.bat
```

#### Start Backend - Dev Mode (With AWS Cognito)
```bash
# From project root
.\start-dev.bat
```

#### Start Frontend
```bash
# From project root
.\setup-frontend.bat
# Or manually:
cd frontend-code/my-react-app
npm run dev
```

### Method 2: Manual Commands

#### Start Backend
```bash
# From project root
mvn spring-boot:run -Dspring-boot.run.profiles=local
# Or for dev profile:
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Start Frontend
```bash
# From frontend-code/my-react-app
npm run dev
```

### Access the Application

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 📁 Project Structure

```
moveo-project/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java          # Main Spring Boot application
│   │   │   ├── config/                       # Configuration classes
│   │   │   ├── exception/                    # Exception handlers
│   │   │   ├── health/                       # Health check endpoints
│   │   │   ├── project/                      # Project domain
│   │   │   │   ├── Project.java              # Project entity
│   │   │   │   ├── ProjectController.java    # REST controller
│   │   │   │   ├── ProjectService.java       # Business logic
│   │   │   │   └── ProjectRepository.java    # Data access
│   │   │   ├── task/                         # Task domain
│   │   │   │   ├── Task.java                 # Task entity
│   │   │   │   ├── TaskController.java       # REST controller
│   │   │   │   ├── TaskService.java          # Business logic
│   │   │   │   └── TaskRepository.java       # Data access
│   │   │   └── user/                         # User domain
│   │   └── resources/
│   │       ├── application.properties         # Main configuration
│   │       ├── application-local.properties   # Local profile
│   │       ├── application-dev.properties     # Dev profile
│   │       └── application-prod.properties    # Prod profile
│   └── test/                                  # Unit and integration tests
├── frontend-code/
│   └── my-react-app/
│       ├── src/
│       │   ├── components/                    # React components
│       │   │   ├── Header.tsx
│       │   │   ├── ProjectList.tsx
│       │   │   ├── TaskList.tsx
│       │   │   └── FormDialog.tsx
│       │   ├── pages/                         # Page components
│       │   │   ├── Home.tsx
│       │   │   ├── Login.tsx
│       │   │   └── HealthCheck.tsx
│       │   ├── hooks/                         # Custom React hooks
│       │   │   ├── useProjects.ts
│       │   │   ├── useTasks.ts
│       │   │   └── useHealthChecks.ts
│       │   ├── services/                      # API services
│       │   │   ├── api.ts
│       │   │   ├── auth.ts
│       │   │   └── projectService.ts
│       │   ├── config/                        # Configuration
│       │   │   └── queryClient.tsx
│       │   ├── App.tsx                        # Main App component
│       │   └── main.tsx                       # Entry point
│       ├── package.json
│       └── vite.config.ts
├── pom.xml                                    # Maven configuration
├── .gitignore
└── README.md                                  # This file
```

## 📚 API Documentation

Once the backend is running, access the interactive API documentation:

### Swagger UI
Navigate to: http://localhost:8080/swagger-ui.html

### Main Endpoints

#### Projects
- `GET /api/projects` - Get all projects
- `GET /api/projects/{id}` - Get project by ID
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

#### Tasks
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `GET /api/tasks/project/{projectId}` - Get tasks by project
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

#### Health
- `GET /api/health` - Check application health
- `GET /api/health/db` - Check database connection

## ⚙️ Configuration

### Environment Profiles

The application supports multiple profiles:

#### Local Profile (`local`)
- No authentication required
- Local MySQL database
- Best for development

#### Dev Profile (`dev`)
- AWS Cognito authentication enabled
- Local or remote MySQL database
- Requires valid JWT tokens

#### Prod Profile (`prod`)
- Full authentication and security
- Production database
- Optimized settings

### Environment Variables

You can override properties using environment variables:

```bash
# Database
export DB_URL=jdbc:mysql://localhost:3306/moveo-db
export DB_USERNAME=root
export DB_PASSWORD=yourpassword

# AWS Cognito
export COGNITO_USER_POOL_ID=your-pool-id
export COGNITO_CLIENT_ID=your-client-id
export COGNITO_REGION=your-region
```

## 🔧 Development

### Running Tests

#### Backend Tests
```bash
mvn test
```

#### Frontend Tests
```bash
cd frontend-code/my-react-app
npm test
```

### Building for Production

#### Backend
```bash
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend-code/my-react-app
npm run build
# Build output will be in dist/
```

## 🐛 Troubleshooting

### Backend Issues

1. **Database Connection Failed**
   - Ensure MySQL is running
   - Check credentials in `application.properties`
   - Verify database exists or set `createDatabaseIfNotExist=true`

2. **Port 8080 Already in Use**
   - Change port in `application.properties`: `server.port=8081`
   - Or kill the process using port 8080

### Frontend Issues

1. **Cannot Connect to Backend**
   - Verify backend is running on http://localhost:8080
   - Check CORS configuration
   - Update API URL in `src/config.ts`

2. **Module Not Found**
   - Run `npm install` again
   - Delete `node_modules` and `package-lock.json`, then reinstall

## 📝 License

This project is private and proprietary.

## 👥 Contributors

- Tzur CS ([@Tzur-CS](https://github.com/Tzur-CS))

## 📧 Support

For questions or issues, please open an issue on GitHub or contact the development team.

---

**Happy Coding! 🚀**

