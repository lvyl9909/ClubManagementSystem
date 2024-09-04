import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

// 检查用户是否已经登录
const isAuthenticated = () => {
    const token = localStorage.getItem('accessToken');

    return !!token; // 如果 token 存在，返回 true
};

const PrivateRoute = ({ children }) => {
    const [isAuth, setIsAuth] = useState(null); // 初始状态设为 null，表示加载中

    useEffect(() => {
        const token = isAuthenticated();
        setIsAuth(token); // 更新认证状态
    }, []);

    if (isAuth === null) {
        // 如果正在检查身份验证状态，返回加载中的状态或空白页面
        return <div>Loading...</div>;
    }

    // 如果未登录，重定向到登录页面
    if (!isAuth) {
        return <Navigate to="/login" replace />;
    }

    // 如果已登录，显示受保护的页面
    return children;
};


export default PrivateRoute;