import { createBrowserRouter, Navigate } from "react-router-dom";
import Main from "../pages/main";
import Home from "../pages/home/home";
import Club from "../pages/club/club";
import ManageClub from "../pages/club/manageclub";
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
                )
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
                path: '/club/manage/:id',
                element: (
                    <PrivateRoute>
                        <ManageClub />
                    </PrivateRoute>
                )
            },
            {
                path: 'event',
                element: (
                    <PrivateRoute>
                        <Event />
                    </PrivateRoute>
                )
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
                        )
                    },
                    {
                        path: 'pageTwo',
                        element: (
                            <PrivateRoute>
                                <PageTwo />
                            </PrivateRoute>
                        )
                    }
                ]
            }
        ]
    },
    {
        path: '/login',
        element: <Login />
    }
]);

export default routes;