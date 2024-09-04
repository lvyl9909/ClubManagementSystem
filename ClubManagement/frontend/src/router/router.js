import { createBrowserRouter, Navigate } from "react-router-dom";
import Main from "../pages/main";
import Home from "../pages/home/home";
import Club from "../pages/club/club";
import Event from "../pages/event/event";
import PageOne from "../pages/other/pageone";
import PageTwo from "../pages/other/pagetwo";
import Login from "../pages/login/login";
import PrivateRoute from "./PrivateRoute"; // 引入 PrivateRoute 组件

// 更新 routes 配置
const routes = createBrowserRouter([
    {
        path: '/',
        element: <Main />, // 使用 JSX 元素
        children: [
            {
                path: '/',
                element: <Navigate to="/login" replace /> // 默认重定向到 login
            },
            {
                path: 'home',
                element: (
                    <PrivateRoute>
                        <Home />
                    </PrivateRoute>
                ) // 保护 Home 路由
            },
            {
                path: 'club',
                element: (
                    <PrivateRoute>
                        <Club />
                    </PrivateRoute>
                ) // 保护 Club 路由
            },
            {
                path: 'event',
                element: (
                    <PrivateRoute>
                        <Event />
                    </PrivateRoute>
                ) // 保护 Event 路由
            },
            {
                path: 'other',
                children: [
                    {
                        path: 'pageOne',
                        element: (
                            <PrivateRoute>
                                <PageOne />
                            </PrivateRoute>
                        ) // 保护 PageOne 路由
                    },
                    {
                        path: 'pageTwo',
                        element: (
                            <PrivateRoute>
                                <PageTwo />
                            </PrivateRoute>
                        ) // 保护 PageTwo 路由
                    }
                ]
            }
        ]
    },
    {
        path: '/login',
        element: <Login /> // 登录页面不需要保护
    }
]);

export default routes;