import { useQuery } from "@tanstack/react-query";
import api from "../services/api";

// Hook for ping test
export const usePingTest = () => {
  return useQuery({
    queryKey: ["health", "ping"],
    queryFn: async () => {
      const response = await api.get("/api/health/ping");
      return response.data;
    },
  });
};

// Hook for auth test
export const useAuthTest = () => {
  return useQuery({
    queryKey: ["health", "auth"],
    queryFn: async () => {
      const response = await api.get("/api/health/auth");
      return response.data;
    },
  });
};

// Hook for current user test
export const useMeTest = () => {
  return useQuery({
    queryKey: ["health", "me"],
    queryFn: async () => {
      const response = await api.get("/api/health/me");
      return response.data;
    },
  });
};


