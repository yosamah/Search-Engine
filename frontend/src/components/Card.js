import React from "react";
import classes from "./style/card.module.css";

const Card = (props) => {
  return (
    <div className={classes.searchcard}>
      <a href={props.url} className={classes.searchcardurl} >{props.url}</a>
      <h2 className={classes.searchcardtitle}>{props.title}</h2>
      <div dangerouslySetInnerHTML={{__html: props.content}} className={classes.searchcardcontent}></div>
  </div>
  );
};
export default Card;
