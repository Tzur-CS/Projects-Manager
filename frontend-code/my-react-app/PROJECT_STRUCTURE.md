# Project Structure

## 📁 Folder Organization

```
src/
├── components/          # Reusable UI components
│   ├── Header.tsx      # Shared header with logout
│   ├── ProjectList.tsx # Project list component
│   ├── TaskList.tsx    # Task list component
│   ├── ProjectDialog.tsx # Add project dialog
│   ├── TaskDialog.tsx  # Add task dialog
│   └── index.ts        # Barrel exports
│
├── config/             # Configuration & Context
│   ├── queryClient.tsx # React Query client context
│   └── index.ts        # Config exports
│
├── hooks/              # Custom React Query hooks
│   ├── useHealthChecks.ts # Health check API hooks
│   ├── useProjects.ts     # Project CRUD hooks
│   ├── useTasks.ts        # Task CRUD hooks
│   └── index.ts           # Hook exports
│
├── pages/              # Page components
│   ├── Login.tsx       # Login page
│   ├── Home.tsx        # Main app page
│   └── HealthCheck.tsx # Backend health check
│
├── services/           # API services
│   ├── api.ts          # Axios instance
│   ├── auth.ts         # Authentication service
│   └── projectService.ts # Project/Task API
│
├── App.tsx             # Main app component
├── main.tsx            # Entry point
└── config.ts           # App configuration
```

## 🎯 Component Architecture

### **Reusable Components**

All components are **prop-based** and **stateless** for maximum reusability:

#### **Header Component**
```typescript
<Header 
  username="john" 
  title="Task Manager"
  showHealthCheck={true}
  showHome={false}
/>
```

#### **ProjectList Component**
```typescript
<ProjectList
  projects={[...]}
  selectedProjectId={1}
  isLoading={false}
  isError={false}
  onProjectSelect={(id) => {...}}
  onAddProject={() => {...}}
  onDeleteProject={(id) => {...}}
  isDeleting={false}
  isCreating={false}
/>
```

#### **TaskList Component**
```typescript
<TaskList
  projectName="My Project"
  tasks={[...]}
  isLoading={false}
  isError={false}
  onAddTask={() => {...}}
  onDeleteTask={(id) => {...}}
  onStatusChange={(id, status) => {...}}
  isDeleting={false}
  isCreating={false}
  isUpdatingStatus={false}
/>
```

## 🔧 Configuration Context

### **QueryClient Context**

Centralized React Query configuration:

```typescript
import { QueryClientProvider } from "./config/queryClient";

// In App.tsx
<QueryClientProvider>
  <YourApp />
</QueryClientProvider>

// In any component
import { useQueryClientContext } from "./config/queryClient";

const queryClient = useQueryClientContext();
queryClient.clear(); // Clear all cache
```

**Configuration:**
- `staleTime`: 5 minutes
- `gcTime`: 10 minutes
- `retry`: 4 attempts
- `refetchOnWindowFocus`: false

## 🪝 Custom Hooks

### **Health Check Hooks**
```typescript
import { usePingTest, useAuthTest, useMeTest, useStatusTest } from "./hooks";

const pingQuery = usePingTest();
// pingQuery.data, pingQuery.isLoading, pingQuery.isError
```

### **Project Hooks**
```typescript
import { useProjects, useCreateProject, useUpdateProject, useDeleteProject } from "./hooks";

const projectsQuery = useProjects();
const createMutation = useCreateProject();

// Create project
await createMutation.mutateAsync({ name: "New Project", description: "..." });
```

### **Task Hooks**
```typescript
import { useTasks, useCreateTask, useUpdateTaskStatus, useDeleteTask } from "./hooks";

const tasksQuery = useTasks(projectId);
const createMutation = useCreateTask();

// Create task
await createMutation.mutateAsync({
  title: "New Task",
  projectId: 1,
  status: "TODO"
});
```

## ✨ Benefits

### **1. Separation of Concerns**
- **Components**: Pure UI, no business logic
- **Hooks**: Data fetching & state management
- **Services**: API communication
- **Config**: App-wide configuration

### **2. Reusability**
- All components accept props
- No hardcoded data or logic
- Easy to test and maintain

### **3. Type Safety**
- Full TypeScript support
- Type inference from hooks
- Compile-time error checking

### **4. Performance**
- React Query caching
- Automatic refetching
- Optimistic updates

### **5. Developer Experience**
- Barrel exports (`import { ... } from "./components"`)
- Consistent patterns
- Easy to navigate

## 🚀 Usage Example

```typescript
// Home.tsx - Clean and simple
import { Header, ProjectList, TaskList } from "./components";
import { useProjects, useTasks } from "./hooks";

function Home() {
  const projectsQuery = useProjects();
  const tasksQuery = useTasks(selectedProjectId);

  return (
    <>
      <Header username="john" showHealthCheck />
      <ProjectList 
        projects={projectsQuery.data || []}
        isLoading={projectsQuery.isLoading}
        {...otherProps}
      />
      <TaskList 
        tasks={tasksQuery.data || []}
        isLoading={tasksQuery.isLoading}
        {...otherProps}
      />
    </>
  );
}
```

## 📦 Exports

### **Components**
```typescript
export { Header, ProjectList, TaskList, ProjectDialog, TaskDialog } from "./components";
```

### **Hooks**
```typescript
export { 
  usePingTest, useAuthTest, useMeTest, useStatusTest,
  useProjects, useCreateProject, useUpdateProject, useDeleteProject,
  useTasks, useCreateTask, useUpdateTask, useUpdateTaskStatus, useDeleteTask
} from "./hooks";
```

### **Config**
```typescript
export { QueryClientProvider, useQueryClientContext, queryClient } from "./config";
```

## 🎉 Result

- ✅ Clean, modular architecture
- ✅ Reusable components
- ✅ Centralized configuration
- ✅ Type-safe hooks
- ✅ Easy to maintain and scale

