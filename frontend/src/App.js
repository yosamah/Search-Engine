import Home from "./pages/Home";
import Results from "./pages/Results";
import { Switch, Routes, Route } from "react-router-dom";

function App() {
  return (
    <div className="App">
    
      <Routes>
        <Route path="/" element={<Home />} exact></Route>
        <Route path="/search/:id" element={<Results />} ></Route>
      </Routes>
    </div>
  );
}

export default App;
