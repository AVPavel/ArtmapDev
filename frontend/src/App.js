import './App.css';
import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import Home from "./pages/Home/Home";
import PageNotReady from "./pages/PageNotReady/PageNotReady";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import UnauthorizedPage from "./pages/UnauthorizedPage/UnauthorizedPage";
import ProtectedRoute from "./ProtectedRoute/ProtectedRoute";
import MessagingPage from "./pages/Messaging/MessagingPage";
import ForgotPassword from "./pages/ForgotPassword/ForgotPassword";
import About from "./pages/About/About";


function App() {
  return (
      <Router>
          <Routes>
              {/* Home Page */}
              <Route path = "/" element={<Home />}/>

              {/* Login Page */}
              <Route path = "/login" element={<Login />}/>

              {/* Register Page */}
              <Route path = "/register" element={<Register />}/>
              <Route path = "/forgot-password" element={<ForgotPassword />}/>

              {/* 404 Page Not Found */}
              <Route path="*" element={<h1>404 Not Found</h1>} />

              {/* Redirect Messages */}
              <Route path="/messages" element={<MessagingPage />} />

              {/* Redirect Recommendations */}
              <Route path="/recommendations" element={<PageNotReady />} />

              {/* Redirect Favorites */}
              <Route path="/favorites" element={<PageNotReady />} />

              <Route path="/about" element={<About/>} />

              {/* Redirect Contact */}
              <Route path="/contact" element={<PageNotReady />} />

              {/* Redirect News */}
              <Route path="/news" element={<PageNotReady />} />

              {/* Unauthorized */}
              <Route path="/unauthorized" element={<UnauthorizedPage />} />
          </Routes>
      </Router>
  );
}

export default App;
