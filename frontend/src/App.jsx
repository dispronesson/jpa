import React from 'react';
import { Routes, Route } from 'react-router-dom';
import OrdersService from "./components/OrdersService";
import NotFound from "./components/NotFound";

function App() {
    return (
        <Routes>
            <Route path="/" element={<OrdersService />} />
            <Route path="/*" element={<NotFound />} />
        </Routes>
    );
}

export default App;