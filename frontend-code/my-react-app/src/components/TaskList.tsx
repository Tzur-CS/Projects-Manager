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
  Chip,
  CircularProgress,
  Alert,
} from "@mui/material";
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Edit as EditIcon,
} from "@mui/icons-material";
import type { Task } from "../services/projectService";
import { useEffect, useRef } from "react";

type TaskListProps = {
  projectName?: string;
  tasks: Task[];
  isLoading: boolean;
  isError: boolean;
  hasNextPage: boolean;
  isFetchingNextPage: boolean;
  onAddTask: () => void;
  onEditTask: (task: Task) => void;
  onDeleteTask: (taskId: number) => void;
  onStatusChange: (taskId: number, currentStatus: string) => void;
  onLoadMore: () => void;
  isDeleting: boolean;
  isCreating: boolean;
  isUpdatingStatus: boolean;
};

const TaskList = ({
  projectName,
  tasks,
  isLoading,
  isError,
  hasNextPage,
  isFetchingNextPage,
  onAddTask,
  onEditTask,
  onDeleteTask,
  onStatusChange,
  onLoadMore,
  isDeleting,
  isCreating,
  isUpdatingStatus,
}: TaskListProps) => {
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

  const getStatusColor = (status: string) => {
    switch (status) {
      case "TODO":
        return "default";
      case "IN_PROGRESS":
        return "primary";
      case "DONE":
        return "success";
      default:
        return "default";
    }
  };

  return (
    <Card>
      <CardContent>
        {projectName ? (
          <>
            <Box
              sx={{
                display: "flex",
                justifyContent: "space-between",
                mb: 2,
              }}
            >
              <Typography variant="h6">{projectName}</Typography>
              <Button
                size="small"
                startIcon={<AddIcon />}
                onClick={onAddTask}
                disabled={isCreating}
              >
                Add Task
              </Button>
            </Box>

            {isLoading ? (
              <Box sx={{ display: "flex", justifyContent: "center", p: 2 }}>
                <CircularProgress size={24} />
              </Box>
            ) : isError ? (
              <Alert severity="error">Failed to load tasks</Alert>
            ) : !tasks || tasks.length === 0 ? (
              <Typography color="text.secondary">No tasks yet</Typography>
            ) : (
              <List
                ref={listRef}
                sx={{
                  maxHeight: "calc(100vh - 300px)",
                  overflowY: "auto",
                }}
              >
                {tasks.map((task) => (
                  <ListItem
                    key={task.id}
                    secondaryAction={
                      <Box>
                        <IconButton
                          size="small"
                          onClick={() => onEditTask(task)}
                          disabled={isDeleting}
                        >
                          <EditIcon fontSize="small" />
                        </IconButton>
                        <IconButton
                          size="small"
                          onClick={() => onDeleteTask(task.id)}
                          disabled={isDeleting}
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Box>
                    }
                  >
                    <ListItemText
                      primary={task.title}
                      secondary={task.description}
                    />
                    <Chip
                      label={task.status.replace("_", " ")}
                      size="small"
                      color={getStatusColor(task.status) as any}
                      onClick={() => onStatusChange(task.id, task.status)}
                      sx={{ mr: 4, cursor: "pointer" }}
                      disabled={isUpdatingStatus}
                    />
                  </ListItem>
                ))}
                {isFetchingNextPage && (
                  <Box sx={{ display: "flex", justifyContent: "center", p: 2 }}>
                    <CircularProgress size={24} />
                  </Box>
                )}
                {!hasNextPage && tasks.length > 0 && (
                  <Box sx={{ textAlign: "center", p: 2 }}>
                    <Typography variant="caption" color="text.secondary">
                      No more tasks
                    </Typography>
                  </Box>
                )}
              </List>
            )}
          </>
        ) : (
          <Typography color="text.secondary" align="center">
            Select a project to view tasks
          </Typography>
        )}
      </CardContent>
    </Card>
  );
};

export default TaskList;
