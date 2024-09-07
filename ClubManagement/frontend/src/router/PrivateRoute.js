import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';


const isAuthenticated = () => {
    const token = localStorage.getItem('accessToken');

    return !!token;
};

const PrivateRoute = ({ children }) => {
    const [isAuth, setIsAuth] = useState(null);

    useEffect(() => {
        const token = isAuthenticated();
        setIsAuth(token);
    }, []);

    if (isAuth === null) {
        return <div>Loading...</div>;
    }


    if (!isAuth) {
        return <Navigate to="/login" replace />;
    }


    return children;
};


export default PrivateRoute;