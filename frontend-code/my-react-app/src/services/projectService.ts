import api from './api';

// Type definitions
export type Project = {
  id: number;
  name: string;
  description?: string;
  ownerId: string;
  taskCount?: number;
  createdAt?: string;
  updatedAt?: string;
};

export type Task = {
  id: number;
  title: string;
  description?: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  projectId: number;
  assignedUserId?: string;
  dueDate?: string;
  createdAt?: string;
  updatedAt?: string;
};

export type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};

export type CreateProjectDTO = {
  name: string;
  description?: string;
};

export type CreateTaskDTO = {
  title: string;
  description?: string;
  projectId: number;
  status?: 'TODO' | 'IN_PROGRESS' | 'DONE';
  assignedUserId?: string;
  dueDate?: string;
};

export const projectService = {
  // Get all projects (admin only)
  async getAllProjects(page = 0, size = 10): Promise<PageResponse<Project>> {
    const response = await api.get(`/api/projects?page=${page}&size=${size}`);
    return response.data;
  },

  // Get user's projects
  async getMyProjects(page = 0, size = 10): Promise<PageResponse<Project>> {
    const response = await api.get(`/api/projects/my-projects?page=${page}&size=${size}`);
    return response.data;
  },

  // Create project
  async createProject(projectData: CreateProjectDTO): Promise<Project> {
    const response = await api.post('/api/projects', projectData);
    return response.data;
  },

  // Update project
  async updateProject(id: number, projectData: CreateProjectDTO): Promise<Project> {
    const response = await api.put(`/api/projects/${id}`, projectData);
    return response.data;
  },

  // Delete project
  async deleteProject(id: number): Promise<void> {
    await api.delete(`/api/projects/${id}`);
  },

  // Search projects
  async searchProjects(name: string, page = 0, size = 10): Promise<PageResponse<Project>> {
    const response = await api.get(`/api/projects/search?name=${name}&page=${page}&size=${size}`);
    return response.data;
  },
};

export const taskService = {
  // Get tasks for a project
  async getTasksByProject(projectId: number, page = 0, size = 10): Promise<PageResponse<Task>> {
    const response = await api.get(`/api/tasks/project/${projectId}?page=${page}&size=${size}`);
    return response.data;
  },

  // Get task by ID
  async getTaskById(id: number): Promise<Task> {
    const response = await api.get(`/api/tasks/${id}`);
    return response.data;
  },

  // Create task
  async createTask(taskData: CreateTaskDTO): Promise<Task> {
    const response = await api.post('/api/tasks', taskData);
    return response.data;
  },

  // Update task
  async updateTask(id: number, taskData: Partial<CreateTaskDTO>): Promise<Task> {
    const response = await api.put(`/api/tasks/${id}`, taskData);
    return response.data;
  },

  // Update task status
  async updateTaskStatus(id: number, status: 'TODO' | 'IN_PROGRESS' | 'DONE'): Promise<Task> {
    const response = await api.patch(`/api/tasks/${id}/status?status=${status}`);
    return response.data;
  },

  // Delete task
  async deleteTask(id: number): Promise<void> {
    await api.delete(`/api/tasks/${id}`);
  },

  // Get my tasks
  async getMyTasks(page = 0, size = 10): Promise<PageResponse<Task>> {
    const response = await api.get(`/api/tasks/my-tasks?page=${page}&size=${size}`);
    return response.data;
  },
};

export default projectService;
