import React from 'react';

import './Search.css';

function Search() {
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
    return (
        <div id="search-div">
            <div className="container-fluid p-0 m-0">
                <div id="google-it-logo" className="row d-flex justify-content-center align-items-center vh-100 vw-100 p-0 m-0">
                    <div className="col-12 p-0 m-0 justify-content-center">
                        <h1 className="text-center m-auto w-fit-content mb-3">Boogle It</h1>
                        <div className="vw-100">
                            <input type="text" className="form-control w-50 m-auto" id="search-bar" onKeyPress={e => { if (e.key === 'Enter') onInputEnter(); }} />
                            <div className="d-flex justify-content-center mt-3">
                                <button className="btn btn-primary" onClick={searchText}>Search</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Search;