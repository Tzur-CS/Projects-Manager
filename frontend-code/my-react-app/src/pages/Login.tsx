import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  CircularProgress,
  Chip,
} from "@mui/material";
import { authService } from "../services/auth";
import { USE_COGNITO } from "../config";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!username || !password) {
      setError("Please enter username and password");
      return;
    }

    setLoading(true);

    try {
      const result = await authService.signIn(username, password);
      console.log("=== LOGIN SUCCESS ===");
      console.log("User:", result);

      // Get access token
      const token = await authService.getAccessToken();
      console.log("Access Token:", token);
      console.log("====================");

      // Redirect to app home page
      navigate("/home");
    } catch (err: any) {
      console.error("Login error:", err);

      if (err.name === "UserNotFoundException") {
        setError("User not found");
      } else if (err.name === "NotAuthorizedException") {
        setError("Incorrect username or password");
      } else if (err.name === "UserNotConfirmedException") {
        setError("Please confirm your account first");
      } else {
        setError(err.message || "Login failed. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        bgcolor: "#f5f5f5",
      }}
    >
      <Card sx={{ width: 400, p: 2 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom align="center">
            Projects Manager Login
          </Typography>

          <Box sx={{ display: "flex", justifyContent: "center", mb: 2 }}>
            <Chip
              label={USE_COGNITO ? "AWS Cognito Mode" : "Local Mode"}
              color={USE_COGNITO ? "primary" : "default"}
              size="small"
            />
          </Box>

          {!USE_COGNITO && (
            <Alert severity="info" sx={{ mb: 2 }}>
              Local mode: Use any username with password ≥ 4 chars
            </Alert>
          )}

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleLogin}>
            <TextField
              fullWidth
              label="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              margin="normal"
              disabled={loading}
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              margin="normal"
              disabled={loading}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              disabled={loading}
              sx={{ mt: 2 }}
            >
              {loading ? <CircularProgress size={24} /> : "Login"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Login;
