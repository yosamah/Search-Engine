import React from 'react';
import { BrowserRouter, useParams } from 'react-router-dom';

import SearchResults from '../components/SearchResults';

function SearchResultsPage() {
    const { id } = useParams();
    return (
        <BrowserRouter>
            <div>
                <title>{id}</title>
                <SearchResults text={id} />
            </div>
        </BrowserRouter>
    )
}

export default SearchResultsPage;