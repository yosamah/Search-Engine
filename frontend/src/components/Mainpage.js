import axios from "axios";
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import google from "../img/google.png";
import classes from './style/layout.module.css';

const Mainpage = () => {
  const [search, setSearch] = useState('')
  const [results, setResults] = useState([])
  const [suggested, setSuggested] = React.useState([])
  const navigate = useNavigate();

  function handleEnter(e){
    if(e.key==='Enter'){
      sendSearch()
    }
  }
  
  function sendSearch(){
    if(search===''){
      return
    }
    navigate("/search/"+search);
  }
 
  async function getSuggestion(query) {
    console.log(query)
    const response = await axios.get(`http://localhost:8080/database/get-query?query=${query}`).then((response) => {
      setSuggested(response.data);

    })
   .catch(error => {
    // Handle any errors that occurred during the request
    console.error(error);
  });
}
  
function handleChange(e) {
  setSearch(e.target.value)
  getSuggestion(e.target.value)
}

  return (
    <div className={classes.layout}>
        {/*search box container*/}
        <div className={classes.searchBoxContainer}>
          <div className={classes.searchBox}>
            <img src={google} alt='google'/>
            <input onKeyDown={handleEnter} list="suggested" type='text' placeholder='Search' onChange={handleChange} />
            <datalist id="suggested" className={classes.suggestion} >
                  {suggested?.map((sug) => {return <option value={sug} />})}
            </datalist>
            <button onClick={sendSearch} >
              Search
            </button>
          </div>
      </div>
    </div>
  )
}

export default Mainpage