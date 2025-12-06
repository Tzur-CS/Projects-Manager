import { useInfiniteQuery, useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { taskService } from "../services/projectService";
import type { CreateTaskDTO, Task } from "../services/projectService";

const PAGE_SIZE = 10;

// Get tasks by project with infinite scroll
export const useTasks = (projectId: number | undefined) => {
  return useInfiniteQuery({
    queryKey: ["tasks", projectId],
    queryFn: async ({ pageParam = 0 }) => {
      if (!projectId) {
        return { content: [], totalPages: 0, number: 0, totalElements: 0, size: PAGE_SIZE };
      }
      const response = await taskService.getTasksByProject(projectId, pageParam, PAGE_SIZE);
      return response;
    },
    getNextPageParam: (lastPage) => {
      // If current page is less than total pages, return next page number
      if (lastPage.number < lastPage.totalPages - 1) {
        return lastPage.number + 1;
      }
      return undefined; // No more pages
    },
    enabled: !!projectId, // Only run query if projectId exists
    initialPageParam: 0,
  });
};

// Helper to flatten all pages into a single array
export const useTasksFlat = (projectId: number | undefined) => {
  const query = useTasks(projectId);
  const tasks: Task[] = query.data?.pages.flatMap((page) => page.content) || [];
  return {
    ...query,
    data: tasks,
  };
};

// Get single task
export const useTask = (taskId: number) => {
  return useQuery({
    queryKey: ["tasks", "detail", taskId],
    queryFn: async () => {
      return await taskService.getTaskById(taskId);
    },
    enabled: !!taskId,
  });
};

// Create task mutation
export const useCreateTask = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateTaskDTO) => {
      return await taskService.createTask(data);
    },
    onSuccess: (_, variables) => {
      // Invalidate tasks for this project
      queryClient.invalidateQueries({ queryKey: ["tasks", variables.projectId] });
    },
  });
};

// Update task mutation
export const useUpdateTask = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, data }: { id: number; data: Partial<CreateTaskDTO> }) => {
      return await taskService.updateTask(id, data);
    },
    onSuccess: (data) => {
      // Invalidate tasks for the project
      queryClient.invalidateQueries({ queryKey: ["tasks", data.projectId] });
      queryClient.invalidateQueries({ queryKey: ["tasks", "detail", data.id] });
      // Reset to first page
      queryClient.resetQueries({ queryKey: ["tasks", data.projectId] });
    },
  });
};

// Update task status mutation
export const useUpdateTaskStatus = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, status }: { id: number; status: "TODO" | "IN_PROGRESS" | "DONE" }) => {
      return await taskService.updateTaskStatus(id, status);
    },
    onSuccess: (data) => {
      // Invalidate tasks for the project
      queryClient.invalidateQueries({ queryKey: ["tasks", data.projectId] });
      // Reset to first page
      queryClient.resetQueries({ queryKey: ["tasks", data.projectId] });
    },
  });
};

// Delete task mutation
export const useDeleteTask = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id }: { id: number; projectId: number }) => {
      return await taskService.deleteTask(id);
    },
    onSuccess: (_, variables) => {
      // Invalidate tasks for this project
      queryClient.invalidateQueries({ queryKey: ["tasks", variables.projectId] });
    },
  });
};

// Get user's tasks
export const useMyTasks = () => {
  return useQuery({
    queryKey: ["tasks", "my"],
    queryFn: async () => {
      const response = await taskService.getMyTasks();
      return response.content || [];
    },
  });
};

