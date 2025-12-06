import axios from 'axios';
import { API_BASE_URL } from '../config';
import { authService } from './auth';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to every request
api.interceptors.request.use(
  async (config) => {
    try {
      const token = await authService.getAccessToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log('Request with token:', config.url);
      }
    } catch (error) {
      console.error('Error adding token to request:', error);
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.status, error.response?.data);
    if (error.response?.status === 401) {
      // Redirect to login on unauthorized
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// API functions
export const projectApi = {
  // Get my projects
  getMyProjects: async (page = 0, size = 10) => {
    const response = await api.get(`/api/projects/my-projects?page=${page}&size=${size}`);
    return response.data;
  },

  // Get project by ID
  getProject: async (id: number) => {
    const response = await api.get(`/api/projects/${id}`);
    return response.data;
  },

  // Create project
  createProject: async (data: { name: string; description?: string }) => {
    const response = await api.post('/api/projects', data);
    return response.data;
  },

  // Update project
  updateProject: async (id: number, data: { name: string; description?: string }) => {
    const response = await api.put(`/api/projects/${id}`, data);
    return response.data;
  },

  // Delete project
  deleteProject: async (id: number) => {
    await api.delete(`/api/projects/${id}`);
  },
};

export const taskApi = {
  // Get tasks for project
  getTasksByProject: async (projectId: number, page = 0, size = 100) => {
    const response = await api.get(`/api/tasks/project/${projectId}?page=${page}&size=${size}`);
    return response.data;
  },

  // Create task
  createTask: async (data: { title: string; description?: string; projectId: number; status?: string }) => {
    const response = await api.post('/api/tasks', data);
    return response.data;
  },

  // Update task status
  updateTaskStatus: async (id: number, status: string) => {
    const response = await api.patch(`/api/tasks/${id}/status?status=${status}`);
    return response.data;
  },

  // Delete task
  deleteTask: async (id: number) => {
    await api.delete(`/api/tasks/${id}`);
  },
};

export default api;
