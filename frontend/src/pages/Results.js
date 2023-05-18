import Pagination from '@mui/material/Pagination';
import axios from "axios";
import React, { useEffect } from "react";
import { NavLink, useParams } from "react-router-dom";
import Card from "../components/Card";
import googleB from "../img/googleB.png";
import classes from "./style/results.module.css";

const Results = (props) => {

  const {id}= useParams();
  const [page, setPage] = React.useState(1);
  const [results, setResults] = React.useState([]);
  const [count, setCount] = React.useState(Math.ceil(results.length/10));
  const [startTime, setStartTime] = React.useState(0);
  const [endTime, setEndTime] = React.useState(0);
  const [time, setTime] = React.useState(0);

  //when component mounts, send request to backend with the id from the url
  const start = (page-1)*10;
  const end = page*10;
  const [arrResults, setarrResults] = React.useState(results.slice(start,end));
  // const [arrResults, setParagrahsResults] = React.useState(results.slice(start,end));

  useEffect(() => {
    var config = {
      method: 'get',
      url: "http://localhost:8080/search?searchedWord="+id,
      headers: {}
    };
    var d1 = new Date();
    var n1 = d1.getTime();
    axios(config)
    .then(function (response) {
      console.log(JSON.stringify(response.data));
      setResults(response.data)
      setCount(Math.ceil(response.data.length/10))
      setarrResults(response.data.slice(start,end))
      var d2 = new Date();
      var n2= d2.getTime();
      setTime(((n2-n1)*0.001).toFixed(4))
    })
    .catch(function (error) {
      console.log(error);
    });
    
    //reload the page when the page number changes


  },[]);



  
 const handleChange = (event, value) => {
  setPage(value);
  setarrResults([]); // Clear old results when page changes
};

useEffect(() => {
  const start = (page - 1) * 10;
  const end = page * 10;
  const slicedWebarr = results.slice(start, end);
  setarrResults(slicedWebarr);
}, [page]);

      // useEffect(() => {
      //   if (page !== '') {
      //     window.location.reload();
      //   }
      // }, [page]);

  return (
    <div>
      <div className={classes.main}>
          <div className={classes.resultsArea}>
          <NavLink to="/"><img src={googleB} className={classes.googleLogo} alt="google"/></NavLink>
            <div className={classes.results}>
              <p className={classes.resultsText}> ({time} milli seconds)</p>
            </div>
            {arrResults.map((web) => {
              return(
              <Card key={web.details.url} title={web.details.title} url={web.details.url} content={web.details.content} />
              )
            })}
            <Pagination sx={{"marginLeft":"8rem","marginBottom":"2rem","marginTop":"2rem","color":"white"}} count={count} page={page} onChange={handleChange} variant="outlined" color="secondary" />
          </div>
        </div>
    </div>
  );
};
export default Results;
