import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Container, Grid, CircularProgress } from "@mui/material";
import { authService } from "../services/auth";
import {
  useProjectsFlat,
  useCreateProject,
  useUpdateProject,
  useDeleteProject,
  useTasksFlat,
  useCreateTask,
  useUpdateTask,
  useUpdateTaskStatus,
  useDeleteTask,
  useProjects,
  useTasks,
} from "../hooks";
import type {
  CreateProjectDTO,
  CreateTaskDTO,
  Project,
  Task,
} from "../services/projectService";
import { toast } from "react-toastify";
import { Header, ProjectList, TaskList, FormDialog } from "../components";

const Home = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [authLoading, setAuthLoading] = useState(true);
  const [selectedProjectId, setSelectedProjectId] = useState<
    number | undefined
  >();

  // Dialogs
  const [projectDialog, setProjectDialog] = useState(false);
  const [taskDialog, setTaskDialog] = useState(false);
  const [editingProject, setEditingProject] = useState<Project | null>(null);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [projectForm, setProjectForm] = useState<Record<string, string>>({
    name: "",
    description: "",
  });
  const [taskForm, setTaskForm] = useState<Record<string, string>>({
    title: "",
    description: "",
  });

  // React Query hooks - use infinite queries for pagination
  const projectsInfiniteQuery = useProjects();
  const tasksInfiniteQuery = useTasks(selectedProjectId);

  // Flattened data for easy access
  const projectsQuery = useProjectsFlat();
  const tasksQuery = useTasksFlat(selectedProjectId);

  const createProjectMutation = useCreateProject();
  const updateProjectMutation = useUpdateProject();
  const deleteProjectMutation = useDeleteProject();
  const createTaskMutation = useCreateTask();
  const updateTaskMutation = useUpdateTask();
  const updateTaskStatusMutation = useUpdateTaskStatus();
  const deleteTaskMutation = useDeleteTask();

  // Check authentication on mount
  useEffect(() => {
    checkAuth();
  }, []);

  // Auto-select first project
  useEffect(() => {
    if (
      projectsQuery.data &&
      projectsQuery.data.length > 0 &&
      !selectedProjectId
    ) {
      setSelectedProjectId(projectsQuery.data[0].id);
    }
  }, [projectsQuery.data, selectedProjectId]);

  const checkAuth = async () => {
    try {
      const user = await authService.getCurrentUser();
      if (!user) {
        navigate("/login");
        return;
      }
      setUsername(user.username);
    } catch (err) {
      console.error("Auth check failed:", err);
      navigate("/login");
    } finally {
      setAuthLoading(false);
    }
  };

  const handleOpenProjectDialog = (project?: Project) => {
    if (project) {
      setEditingProject(project);
      setProjectForm({
        name: project.name,
        description: project.description || "",
      });
    } else {
      setEditingProject(null);
      setProjectForm({ name: "", description: "" });
    }
    setProjectDialog(true);
  };

  const handleCloseProjectDialog = () => {
    setProjectDialog(false);
    setEditingProject(null);
    setProjectForm({ name: "", description: "" });
  };

  const handleProjectSubmit = async () => {
    if (!projectForm.name) {
      toast.error("Project name is required");
      return;
    }

    try {
      if (editingProject) {
        await updateProjectMutation.mutateAsync({
          id: editingProject.id,
          data: projectForm as CreateProjectDTO,
        });
        toast.success("Project updated successfully");
      } else {
        await createProjectMutation.mutateAsync(
          projectForm as CreateProjectDTO
        );
        toast.success("Project created successfully");
      }
      handleCloseProjectDialog();
    } catch (error) {
      toast.error(
        editingProject ? "Failed to update project" : "Failed to create project"
      );
      console.error(error);
    }
  };

  const handleDeleteProject = async (id: number) => {
    if (!confirm("Are you sure you want to delete this project?")) return;

    try {
      await deleteProjectMutation.mutateAsync(id);
      if (selectedProjectId === id) {
        setSelectedProjectId(undefined);
      }
      toast.success("Project deleted successfully");
    } catch (error) {
      toast.error("Failed to delete project");
      console.error(error);
    }
  };

  const handleOpenTaskDialog = (task?: Task) => {
    if (!selectedProjectId && !task) {
      toast.error("Please select a project first");
      return;
    }

    if (task) {
      setEditingTask(task);
      setTaskForm({
        title: task.title,
        description: task.description || "",
      });
    } else {
      setEditingTask(null);
      setTaskForm({ title: "", description: "" });
    }
    setTaskDialog(true);
  };

  const handleCloseTaskDialog = () => {
    setTaskDialog(false);
    setEditingTask(null);
    setTaskForm({ title: "", description: "" });
  };

  const handleTaskSubmit = async () => {
    if (!taskForm.title) {
      toast.error("Task title is required");
      return;
    }

    try {
      if (editingTask) {
        await updateTaskMutation.mutateAsync({
          id: editingTask.id,
          data: {
            title: taskForm.title,
            description: taskForm.description,
          },
        });
        toast.success("Task updated successfully");
      } else {
        if (!selectedProjectId) {
          toast.error("Please select a project first");
          return;
        }
        const taskData: CreateTaskDTO = {
          title: taskForm.title,
          description: taskForm.description,
          projectId: selectedProjectId,
          status: "TODO",
        };
        await createTaskMutation.mutateAsync(taskData);
        toast.success("Task created successfully");
      }
      handleCloseTaskDialog();
    } catch (error) {
      toast.error(
        editingTask ? "Failed to update task" : "Failed to create task"
      );
      console.error(error);
    }
  };

  const handleDeleteTask = async (taskId: number) => {
    if (
      !selectedProjectId ||
      !confirm("Are you sure you want to delete this task?")
    )
      return;

    try {
      await deleteTaskMutation.mutateAsync({
        id: taskId,
        projectId: selectedProjectId,
      });
      toast.success("Task deleted successfully");
    } catch (error) {
      toast.error("Failed to delete task");
      console.error(error);
    }
  };

  const handleChangeStatus = async (taskId: number, currentStatus: string) => {
    const statusOrder = ["TODO", "IN_PROGRESS", "DONE"];
    const currentIndex = statusOrder.indexOf(currentStatus);
    const nextStatus = statusOrder[(currentIndex + 1) % statusOrder.length] as
      | "TODO"
      | "IN_PROGRESS"
      | "DONE";

    try {
      await updateTaskStatusMutation.mutateAsync({
        id: taskId,
        status: nextStatus,
      });
      toast.success(`Status updated to ${nextStatus.replace("_", " ")}`);
    } catch (error) {
      toast.error("Failed to update status");
      console.error(error);
    }
  };

  if (authLoading) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          minHeight: "100vh",
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  const selectedProject = projectsQuery.data?.find(
    (p) => p.id === selectedProjectId
  );

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "#f5f5f5" }}>
      <Header username={username} showHealthCheck={true} />

      <Container sx={{ py: 4 }}>
        <Grid container spacing={3}>
          {/* Projects List */}
          <Grid item xs={12} md={4}>
            <ProjectList
              projects={projectsQuery.data || []}
              selectedProjectId={selectedProjectId}
              isLoading={projectsQuery.isLoading}
              isError={projectsQuery.isError}
              hasNextPage={projectsInfiniteQuery.hasNextPage || false}
              isFetchingNextPage={projectsInfiniteQuery.isFetchingNextPage}
              onProjectSelect={setSelectedProjectId}
              onAddProject={() => handleOpenProjectDialog()}
              onEditProject={handleOpenProjectDialog}
              onDeleteProject={handleDeleteProject}
              onLoadMore={() => projectsInfiniteQuery.fetchNextPage()}
              isDeleting={deleteProjectMutation.isPending}
              isCreating={createProjectMutation.isPending}
            />
          </Grid>

          {/* Tasks List */}
          <Grid item xs={12} md={8}>
            <TaskList
              projectName={selectedProject?.name}
              tasks={tasksQuery.data || []}
              isLoading={tasksQuery.isLoading}
              isError={tasksQuery.isError}
              hasNextPage={tasksInfiniteQuery.hasNextPage || false}
              isFetchingNextPage={tasksInfiniteQuery.isFetchingNextPage}
              onAddTask={() => handleOpenTaskDialog()}
              onEditTask={handleOpenTaskDialog}
              onDeleteTask={handleDeleteTask}
              onStatusChange={handleChangeStatus}
              onLoadMore={() => tasksInfiniteQuery.fetchNextPage()}
              isDeleting={deleteTaskMutation.isPending}
              isCreating={createTaskMutation.isPending}
              isUpdatingStatus={updateTaskStatusMutation.isPending}
            />
          </Grid>
        </Grid>
      </Container>

      {/* Project Dialog */}
      <FormDialog
        open={projectDialog}
        title={editingProject ? "Edit Project" : "Add New Project"}
        onClose={handleCloseProjectDialog}
        onSubmit={handleProjectSubmit}
        fields={[
          { name: "name", label: "Project Name", autoFocus: true },
          {
            name: "description",
            label: "Description",
            multiline: true,
            rows: 3,
          },
        ]}
        formData={projectForm}
        onFieldChange={(name, value) =>
          setProjectForm({ ...projectForm, [name]: value })
        }
        isPending={
          createProjectMutation.isPending || updateProjectMutation.isPending
        }
        submitLabel={editingProject ? "Update" : "Create"}
      />

      {/* Task Dialog */}
      <FormDialog
        open={taskDialog}
        title={editingTask ? "Edit Task" : "Add New Task"}
        onClose={handleCloseTaskDialog}
        onSubmit={handleTaskSubmit}
        fields={[
          { name: "title", label: "Task Title", autoFocus: true },
          {
            name: "description",
            label: "Description",
            multiline: true,
            rows: 3,
          },
        ]}
        formData={taskForm}
        onFieldChange={(name, value) =>
          setTaskForm({ ...taskForm, [name]: value })
        }
        isPending={createTaskMutation.isPending || updateTaskMutation.isPending}
        submitLabel={editingTask ? "Update" : "Create"}
      />
    </Box>
  );
};

export default Home;
