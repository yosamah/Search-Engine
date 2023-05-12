import { useState } from 'react';
import './App.css';
import logo from './logo.jpg';
import search from './search-icon.svg';


function App() {
  const [query, setQuery] = useState("");
  return (
    <div className='parent-Div'>
      <div className='logo-div'>
        <img src={logo} alt="text" height={60}/>
        <p className='header'>Find-it</p>
      </div>
      <div className='search-div'>
        <img src={search} alt="text"/>
        <input type='search' placeholder='Search Find-it' className='search-bar' onChange={e=> setQuery(e.target.value)} />
      </div>
    </div>
  );
}
export default App;
