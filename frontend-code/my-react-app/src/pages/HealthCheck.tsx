import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Container,
  Card,
  CardContent,
  Grid,
  Alert,
  CircularProgress,
  Divider,
  Chip,
  Button,
  Typography,
} from "@mui/material";
import {
  CheckCircle as SuccessIcon,
  Error as ErrorIcon,
  Refresh as RefreshIcon,
} from "@mui/icons-material";
import { authService } from "../services/auth";
import { usePingTest, useAuthTest, useMeTest, useStatusTest } from "../hooks";
import { useQueryClientContext } from "../config/queryClient";
import { Header } from "../components";

const HealthCheck = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClientContext();
  const [username, setUsername] = useState("");
  const [loading, setLoading] = useState(true);

  // Use React Query hooks
  const pingQuery = usePingTest();
  const authQuery = useAuthTest();
  const meQuery = useMeTest();
  const statusQuery = useStatusTest();

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const user = await authService.getCurrentUser();
      if (!user) {
        navigate("/login");
        return;
      }
      setUsername(user.username);
    } catch (err) {
      navigate("/login");
    } finally {
      setLoading(false);
    }
  };

  const handleRefreshAll = () => {
    // Invalidate all health check queries to force refetch
    queryClient.invalidateQueries({ queryKey: ["health"] });
  };

  if (loading) {
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

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "#f5f5f5" }}>
      <Header username={username} title="Health Check" showHome={true} />

      <Container sx={{ py: 4 }}>
        <Box
          sx={{
            mb: 3,
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <Typography variant="h4">Backend Connection Test</Typography>
          <Button
            variant="contained"
            startIcon={<RefreshIcon />}
            onClick={handleRefreshAll}
            disabled={
              pingQuery.isLoading ||
              authQuery.isLoading ||
              meQuery.isLoading ||
              statusQuery.isLoading
            }
          >
            {pingQuery.isFetching ||
            authQuery.isFetching ||
            meQuery.isFetching ||
            statusQuery.isFetching
              ? "Refreshing..."
              : "Run All Tests"}
          </Button>
        </Box>

        <Grid container spacing={3}>
          {/* Test 1: Ping */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                  {pingQuery.isSuccess ? (
                    <SuccessIcon color="success" sx={{ mr: 1 }} />
                  ) : pingQuery.isError ? (
                    <ErrorIcon color="error" sx={{ mr: 1 }} />
                  ) : (
                    <CircularProgress size={24} sx={{ mr: 1 }} />
                  )}
                  <Typography variant="h6">1. Ping Test (Public)</Typography>
                  {pingQuery.isFetching && !pingQuery.isLoading && (
                    <Chip label="Updating..." size="small" sx={{ ml: 1 }} />
                  )}
                </Box>

                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ mb: 2 }}
                >
                  Tests basic connectivity to backend without authentication
                </Typography>

                {pingQuery.isError ? (
                  <Alert severity="error">
                    {pingQuery.error instanceof Error
                      ? pingQuery.error.message
                      : "Unknown error"}
                  </Alert>
                ) : pingQuery.isSuccess ? (
                  <Box sx={{ p: 2, bgcolor: "#f5f5f5", borderRadius: 1 }}>
                    <pre style={{ margin: 0, fontSize: 12 }}>
                      {JSON.stringify(pingQuery.data, null, 2)}
                    </pre>
                  </Box>
                ) : (
                  <Typography color="text.secondary">Testing...</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Test 2: Auth Check */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                  {authQuery.isSuccess ? (
                    <SuccessIcon color="success" sx={{ mr: 1 }} />
                  ) : authQuery.isError ? (
                    <ErrorIcon color="error" sx={{ mr: 1 }} />
                  ) : (
                    <CircularProgress size={24} sx={{ mr: 1 }} />
                  )}
                  <Typography variant="h6">2. Auth Test</Typography>
                  {authQuery.isFetching && !authQuery.isLoading && (
                    <Chip label="Updating..." size="small" sx={{ ml: 1 }} />
                  )}
                </Box>

                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ mb: 2 }}
                >
                  Tests authentication with JWT token
                </Typography>

                {authQuery.isError ? (
                  <Alert severity="error">
                    {authQuery.error instanceof Error
                      ? authQuery.error.message
                      : "Unknown error"}
                  </Alert>
                ) : authQuery.isSuccess ? (
                  <Box>
                    <Chip
                      label={
                        authQuery.data?.authenticated
                          ? "Authenticated"
                          : "Not Authenticated"
                      }
                      color={
                        authQuery.data?.authenticated ? "success" : "error"
                      }
                      sx={{ mb: 2 }}
                    />
                    <Box sx={{ p: 2, bgcolor: "#f5f5f5", borderRadius: 1 }}>
                      <pre style={{ margin: 0, fontSize: 12 }}>
                        {JSON.stringify(authQuery.data, null, 2)}
                      </pre>
                    </Box>
                  </Box>
                ) : (
                  <Typography color="text.secondary">Testing...</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Test 3: Current User */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                  {meQuery.isSuccess ? (
                    <SuccessIcon color="success" sx={{ mr: 1 }} />
                  ) : meQuery.isError ? (
                    <ErrorIcon color="error" sx={{ mr: 1 }} />
                  ) : (
                    <CircularProgress size={24} sx={{ mr: 1 }} />
                  )}
                  <Typography variant="h6">3. Current User (/me)</Typography>
                  {meQuery.isFetching && !meQuery.isLoading && (
                    <Chip label="Updating..." size="small" sx={{ ml: 1 }} />
                  )}
                </Box>

                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ mb: 2 }}
                >
                  Gets current user information from backend
                </Typography>

                {meQuery.isError ? (
                  <Alert severity="error">
                    {meQuery.error instanceof Error
                      ? meQuery.error.message
                      : "Unknown error"}
                  </Alert>
                ) : meQuery.isSuccess ? (
                  <Box>
                    {meQuery.data?.authenticated && (
                      <Box sx={{ mb: 2 }}>
                        <Typography variant="subtitle2">User ID:</Typography>
                        <Typography
                          variant="body2"
                          color="primary"
                          sx={{ mb: 1 }}
                        >
                          {meQuery.data.userId}
                        </Typography>
                        <Typography variant="subtitle2">Username:</Typography>
                        <Typography
                          variant="body2"
                          color="primary"
                          sx={{ mb: 1 }}
                        >
                          {meQuery.data.username}
                        </Typography>
                        <Typography variant="subtitle2">Email:</Typography>
                        <Typography variant="body2" color="primary">
                          {meQuery.data.email}
                        </Typography>
                      </Box>
                    )}
                    <Divider sx={{ my: 2 }} />
                    <Box
                      sx={{
                        p: 2,
                        bgcolor: "#f5f5f5",
                        borderRadius: 1,
                        maxHeight: 300,
                        overflow: "auto",
                      }}
                    >
                      <pre style={{ margin: 0, fontSize: 12 }}>
                        {JSON.stringify(meQuery.data, null, 2)}
                      </pre>
                    </Box>
                  </Box>
                ) : (
                  <Typography color="text.secondary">Testing...</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Test 4: System Status */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                  {statusQuery.isSuccess ? (
                    <SuccessIcon color="success" sx={{ mr: 1 }} />
                  ) : statusQuery.isError ? (
                    <ErrorIcon color="error" sx={{ mr: 1 }} />
                  ) : (
                    <CircularProgress size={24} sx={{ mr: 1 }} />
                  )}
                  <Typography variant="h6">4. System Status</Typography>
                  {statusQuery.isFetching && !statusQuery.isLoading && (
                    <Chip label="Updating..." size="small" sx={{ ml: 1 }} />
                  )}
                </Box>

                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ mb: 2 }}
                >
                  Gets backend system status and configuration
                </Typography>

                {statusQuery.isError ? (
                  <Alert severity="error">
                    {statusQuery.error instanceof Error
                      ? statusQuery.error.message
                      : "Unknown error"}
                  </Alert>
                ) : statusQuery.isSuccess ? (
                  <Box
                    sx={{
                      p: 2,
                      bgcolor: "#f5f5f5",
                      borderRadius: 1,
                      maxHeight: 300,
                      overflow: "auto",
                    }}
                  >
                    <pre style={{ margin: 0, fontSize: 12 }}>
                      {JSON.stringify(statusQuery.data, null, 2)}
                    </pre>
                  </Box>
                ) : (
                  <Typography color="text.secondary">Testing...</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Summary */}
        <Card sx={{ mt: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Connection Summary
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6} md={3}>
                <Box sx={{ textAlign: "center", p: 2 }}>
                  {pingQuery.isSuccess ? (
                    <SuccessIcon color="success" sx={{ fontSize: 40 }} />
                  ) : (
                    <ErrorIcon color="error" sx={{ fontSize: 40 }} />
                  )}
                  <Typography variant="body2">Backend Connectivity</Typography>
                </Box>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <Box sx={{ textAlign: "center", p: 2 }}>
                  {authQuery.data?.authenticated ? (
                    <SuccessIcon color="success" sx={{ fontSize: 40 }} />
                  ) : (
                    <ErrorIcon color="error" sx={{ fontSize: 40 }} />
                  )}
                  <Typography variant="body2">Authentication</Typography>
                </Box>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <Box sx={{ textAlign: "center", p: 2 }}>
                  {meQuery.data?.authenticated ? (
                    <SuccessIcon color="success" sx={{ fontSize: 40 }} />
                  ) : (
                    <ErrorIcon color="error" sx={{ fontSize: 40 }} />
                  )}
                  <Typography variant="body2">User Info</Typography>
                </Box>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <Box sx={{ textAlign: "center", p: 2 }}>
                  {statusQuery.isSuccess ? (
                    <SuccessIcon color="success" sx={{ fontSize: 40 }} />
                  ) : (
                    <ErrorIcon color="error" sx={{ fontSize: 40 }} />
                  )}
                  <Typography variant="body2">System Status</Typography>
                </Box>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
};

export default HealthCheck;
