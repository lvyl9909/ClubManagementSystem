import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

// 检查用户是否已经登录
const isAuthenticated = () => {
    const token = localStorage.getItem('token');
    return !!token; // 如果 token 存在，返回 true
};

const PrivateRoute = ({ children }) => {
    const [isAuth, setIsAuth] = useState(false);

    useEffect(() => {
        // 在组件挂载时检查 token 是否存在
        const token = isAuthenticated();
        setIsAuth(token); // 设置认证状态
    }, []);

    // 如果未登录，重定向到登录页面
    if (!isAuth) {
        return <Navigate to="/login" replace />;
    }

    // 如果已登录，显示受保护的页面
    return children;
};

export default PrivateRoute;