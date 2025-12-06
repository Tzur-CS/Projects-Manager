import { createContext, useContext } from "react";
import type { ReactNode } from "react";
import {
  QueryClient,
  QueryClientProvider as TanStackQueryClientProvider,
} from "@tanstack/react-query";

// Create QueryClient with configuration
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false, // Don't refetch when window regains focus
      retry: 4, // Retry failed requests 4 times
      staleTime: 5 * 60 * 1000, // Data stays fresh for 5 minutes
      gcTime: 10 * 60 * 1000, // Cache data for 10 minutes (formerly cacheTime)
    },
  },
});

// Create context for QueryClient
const QueryClientContext = createContext<QueryClient | undefined>(undefined);

// Provider component
type QueryClientProviderProps = {
  children: ReactNode;
};

export const QueryClientProvider = ({ children }: QueryClientProviderProps) => {
  return (
    <QueryClientContext.Provider value={queryClient}>
      <TanStackQueryClientProvider client={queryClient}>
        {children}
      </TanStackQueryClientProvider>
    </QueryClientContext.Provider>
  );
};

// Hook to use QueryClient
export const useQueryClientContext = () => {
  const context = useContext(QueryClientContext);
  if (context === undefined) {
    throw new Error(
      "useQueryClientContext must be used within QueryClientProvider"
    );
  }
  return context;
};

export { queryClient };
