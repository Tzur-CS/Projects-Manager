import { useInfiniteQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { projectService } from "../services/projectService";
import type { CreateProjectDTO, Project } from "../services/projectService";

const PAGE_SIZE = 10;

// Get all user's projects with infinite scroll
export const useProjects = () => {
  return useInfiniteQuery({
    queryKey: ["projects"],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await projectService.getMyProjects(pageParam, PAGE_SIZE);
      return response;
    },
    getNextPageParam: (lastPage) => {
      // If current page is less than total pages, return next page number
      if (lastPage.number < lastPage.totalPages - 1) {
        return lastPage.number + 1;
      }
      return undefined; // No more pages
    },
    initialPageParam: 0,
  });
};

// Helper to flatten all pages into a single array
export const useProjectsFlat = () => {
  const query = useProjects();
  const projects: Project[] = query.data?.pages.flatMap((page) => page.content) || [];
  return {
    ...query,
    data: projects,
  };
};

// Get single project
export const useProject = (projectId: number) => {
  return useQuery({
    queryKey: ["projects", projectId],
    queryFn: async () => {
      return await projectService.getProjectById(projectId);
    },
    enabled: !!projectId,
  });
};

// Create project mutation
export const useCreateProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateProjectDTO) => {
      return await projectService.createProject(data);
    },
    onSuccess: () => {
      // Invalidate projects query to refetch
      queryClient.invalidateQueries({ queryKey: ["projects"] });
      // Reset to first page
      queryClient.resetQueries({ queryKey: ["projects"] });
    },
  });
};

// Update project mutation
export const useUpdateProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, data }: { id: number; data: CreateProjectDTO }) => {
      return await projectService.updateProject(id, data);
    },
    onSuccess: (_, variables) => {
      // Invalidate both projects list and specific project
      queryClient.invalidateQueries({ queryKey: ["projects"] });
      queryClient.invalidateQueries({ queryKey: ["projects", variables.id] });
      // Reset to first page
      queryClient.resetQueries({ queryKey: ["projects"] });
    },
  });
};

// Delete project mutation
export const useDeleteProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: number) => {
      return await projectService.deleteProject(id);
    },
    onSuccess: () => {
      // Invalidate projects list
      queryClient.invalidateQueries({ queryKey: ["projects"] });
      // Reset to first page
      queryClient.resetQueries({ queryKey: ["projects"] });
    },
  });
};

