import { BrowserRouter, Route, Switch } from "react-router-dom";

import SearchPage from "./pages/SearchPage";
import SearchResultsPage from "./pages/SearchResultsPage";
function App() {
  return (
    <BrowserRouter>
      <Switch>
        <Route exact path="/" component={SearchPage} />
        <Route exact path="/:id" component={SearchResultsPage} />
      </Switch>
    </BrowserRouter>
  );
}

export default App;
