import React from 'react';
import { BrowserRouter } from 'react-router-dom';

import Search from '../components/Search';

function SearchPage() {
    return (
        <BrowserRouter>
            <div>
                <title>Toogle It | Homepage</title>
                <Search />
            </div>
        </BrowserRouter>
    )
}

export default SearchPage;