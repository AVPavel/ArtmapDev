import './App.css';
import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import Home from "./pages/Home/Home";
import myPhoto from "./assets/images/LOGO_artmap-01.svg"
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";

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

              {/* 404 Page Not Found */}
              <Route path="*" element={<h1>404 Not Found</h1>} />
          </Routes>
      </Router>
  );
}

export default App;
