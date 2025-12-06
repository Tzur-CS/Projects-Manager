import {
  Card,
  CardContent,
  Box,
  Typography,
  Button,
  List,
  ListItem,
  ListItemText,
  IconButton,
  CircularProgress,
  Alert,
} from "@mui/material";
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Edit as EditIcon,
} from "@mui/icons-material";
import type { Project } from "../services/projectService";
import { useEffect, useRef } from "react";

type ProjectListProps = {
  projects: Project[];
  selectedProjectId?: number;
  isLoading: boolean;
  isError: boolean;
  hasNextPage: boolean;
  isFetchingNextPage: boolean;
  onProjectSelect: (projectId: number) => void;
  onAddProject: () => void;
  onEditProject: (project: Project) => void;
  onDeleteProject: (projectId: number) => void;
  onLoadMore: () => void;
  isDeleting: boolean;
  isCreating: boolean;
};

const ProjectList = ({
  projects,
  selectedProjectId,
  isLoading,
  isError,
  hasNextPage,
  isFetchingNextPage,
  onProjectSelect,
  onAddProject,
  onEditProject,
  onDeleteProject,
  onLoadMore,
  isDeleting,
  isCreating,
}: ProjectListProps) => {
  const listRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const listElement = listRef.current;
    if (!listElement) return;

    const handleScroll = () => {
      const { scrollTop, scrollHeight, clientHeight } = listElement;
      // Load more when user scrolls to within 100px of bottom
      if (
        scrollHeight - scrollTop - clientHeight < 100 &&
        hasNextPage &&
        !isFetchingNextPage
      ) {
        onLoadMore();
      }
    };

    listElement.addEventListener("scroll", handleScroll);
    return () => listElement.removeEventListener("scroll", handleScroll);
  }, [hasNextPage, isFetchingNextPage, onLoadMore]);

  return (
    <Card>
      <CardContent>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            mb: 2,
          }}
        >
          <Typography variant="h6">Projects</Typography>
          <Button
            size="small"
            startIcon={<AddIcon />}
            onClick={onAddProject}
            disabled={isCreating}
          >
            Add
          </Button>
        </Box>

        {isLoading ? (
          <Box sx={{ display: "flex", justifyContent: "center", p: 2 }}>
            <CircularProgress size={24} />
          </Box>
        ) : isError ? (
          <Alert severity="error">Failed to load projects</Alert>
        ) : !projects || projects.length === 0 ? (
          <Typography color="text.secondary">No projects yet</Typography>
        ) : (
          <List
            ref={listRef}
            sx={{
              maxHeight: "calc(100vh - 300px)",
              overflowY: "auto",
            }}
          >
            {projects.map((project) => (
              <ListItem
                key={project.id}
                sx={{
                  bgcolor:
                    selectedProjectId === project.id
                      ? "action.selected"
                      : "transparent",
                  borderRadius: 1,
                  cursor: "pointer",
                  mb: 1,
                }}
                onClick={() => onProjectSelect(project.id)}
                secondaryAction={
                  <Box>
                    <IconButton
                      size="small"
                      onClick={(e) => {
                        e.stopPropagation();
                        onEditProject(project);
                      }}
                      disabled={isDeleting}
                    >
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={(e) => {
                        e.stopPropagation();
                        onDeleteProject(project.id);
                      }}
                      disabled={isDeleting}
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </Box>
                }
              >
                <ListItemText
                  primary={project.name}
                  secondary={project.description || "No description"}
                />
              </ListItem>
            ))}
            {isFetchingNextPage && (
              <Box sx={{ display: "flex", justifyContent: "center", p: 2 }}>
                <CircularProgress size={24} />
              </Box>
            )}
            {!hasNextPage && projects.length > 0 && (
              <Box sx={{ textAlign: "center", p: 2 }}>
                <Typography variant="caption" color="text.secondary">
                  No more projects
                </Typography>
              </Box>
            )}
          </List>
        )}
      </CardContent>
    </Card>
  );
};

export default ProjectList;
