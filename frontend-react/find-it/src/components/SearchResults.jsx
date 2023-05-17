import React, { useState, useEffect } from 'react';
import axios from 'axios';

import ReactPaginate from 'react-paginate';

import './SearchResults.css';

function SearchResults(props) {
    const [queryResults, setQueryResults] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [queriesPerPage] = useState(10);
    const [numOfPages, setNumOfPages] = useState(0);
    const [queryText] = useState(props.text);

    const pageOnClick = (e) => {
        setCurrentPage(Number(e.selected));
        debugger
    }

    useEffect(() => {
        axios.defaults.baseURL = "http://localhost:8080/api";
        axios.get("/search?q=" + queryText, {
            headers: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept"
            }
        }).then(response => {
            debugger;
            setQueryResults(response.data);
            setNumOfPages((response.data.length / queriesPerPage));
            console.log(response.data);
        }).catch(error => {
            debugger;
        });
    }, [])

    const searchText = (e) => {
        let text = document.getElementById("search-bar").value;
        console.log(encodeURIComponent(text));
        window.location.href = "/" + encodeURIComponent(text);
    }

    const onInputEnter = (e) => {
        let text = document.getElementById("search-bar").value;
        console.log(encodeURIComponent(text));
        window.location.href = "/" + encodeURIComponent(text);
    }

    const redirectToPage = (e) => {
        window.location.href = e.currentTarget.parentElement.getElementsByTagName("a")[0].href;
    }

    return (
        <div id="search-results">
            <div className="container-fluid">
                <div className="row d-flex">
                    <div className="col-3 d-flex align-items-center">
                        <a href="/" className="text-decoration-none">
                            <h1 className="m-0">Boogle It</h1>
                        </a>
                    </div>
                    <div className="col-9 d-flex align-items-center">
                        <input id="search-bar" type="text" className="form-control w-50 " defaultValue={queryText} onKeyPress={e => { if (e.key === 'Enter') onInputEnter(); }} />
                        <button className="ms-3 btn btn-primary" onClick={searchText}>Search</button>
                    </div>
                </div>
                <hr />
                {queryResults !== null && queryResults.length > 0 ?
                    <div className="row mx-3">
                        {queryResults.slice(currentPage * queriesPerPage, ((queryResults.length - currentPage * queriesPerPage) < queriesPerPage) ? (queryResults.length) : (currentPage * queriesPerPage + queriesPerPage)).map((result, index) => {
                            let urlStructure = new URL(result.url);
                            let url = urlStructure.origin + urlStructure.pathname;
                            return (
                                <div className="col-8 my-2" key={index}>
                                    <a href={result.url} className="fs-4 cursor-pointer">{result.title}</a>
                                    <br />
                                    <small className="cursor-pointer" onClick={redirectToPage}>{url}</small>
                                    <p className="m-0">{result.paragraph}</p>
                                </div>
                            );
                        })
                        }
                        <div className="col-12">
                            <ReactPaginate
                                previousLabel={"prev"}
                                nextLabel={"next"}
                                breakLabel={"..."}
                                breakClassName={"break-me"}
                                pageCount={numOfPages}
                                marginPagesDisplayed={2}
                                pageRangeDisplayed={queriesPerPage}
                                onPageChange={pageOnClick}
                                containerClassName={"pagination"}
                                subContainerClassName={"pages pagination"}
                                activeClassName={"active"}
                            />
                        </div>
                    </div> : null
                }
            </div>
        </div>
    );
}

export default SearchResults;