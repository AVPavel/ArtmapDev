import React from 'react';
import {Navigate} from "react-router-dom";

const ProtectedRoute = ({ element, ...rest }) => {
    const token = localStorage.getItem("jwt");

    if(!token){
        return <Navigate to="/unauthorized" replace/>
    }

    return element;
}

export default ProtectedRoute;