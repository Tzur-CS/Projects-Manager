import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { ThemeProvider, createTheme, CssBaseline } from "@mui/material";
import { QueryClientProvider } from "./config/queryClient";
import Login from "./pages/Login";
import Home from "./pages/Home";
import HealthCheck from "./pages/HealthCheck";

const theme = createTheme({
  palette: {
    mode: "light",
  },
});

function App() {
  return (
    <QueryClientProvider>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Router>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/health" element={<HealthCheck />} />
            <Route path="/home" element={<Home />} />
            <Route path="/" element={<Navigate to="/login" replace />} />
          </Routes>
        </Router>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
