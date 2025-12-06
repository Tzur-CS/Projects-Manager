import { AppBar, Toolbar, Typography, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { authService } from "../services/auth";
import { toast } from "react-toastify";
import { useQueryClientContext } from "../config/queryClient";

type HeaderProps = {
  username: string;
  title?: string;
  showHealthCheck?: boolean;
  showHome?: boolean;
};

const Header = ({
  username,
  title = "Projects Manager",
  showHealthCheck = false,
  showHome = false,
}: HeaderProps) => {
  const navigate = useNavigate();
  const queryClient = useQueryClientContext();

  const handleLogout = async () => {
    try {
      await authService.signOut();

      // Clear all React Query cache on logout
      queryClient.clear();

      toast.success("Logged out successfully");
      navigate("/login");
    } catch (error) {
      console.error("Logout error:", error);
      toast.error("Error logging out");
    }
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>
          {title} - {username}
        </Typography>

        {showHealthCheck && (
          <Button
            color="inherit"
            sx={{ mr: 2 }}
            onClick={() => navigate("/health")}
          >
            Health Check
          </Button>
        )}

        {showHome && (
          <Button
            color="inherit"
            sx={{ mr: 2 }}
            onClick={() => navigate("/home")}
          >
            Go to App
          </Button>
        )}

        <Button color="inherit" onClick={handleLogout}>
          Logout
        </Button>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
