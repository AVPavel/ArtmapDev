import './App.css';
import Login from "./components/Login/Login";
import myPhoto from "./assets/images/LOGO_artmap-01.svg"

function App() {
  return (
      <div className="App">
          <Login/>
          <img src={myPhoto} alt="Artmap Logo" style={{maxWidth: "1200px"}}/>
      </div>
  );
}

export default App;
