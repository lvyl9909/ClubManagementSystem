import { createBrowserRouter, Navigate } from "react-router-dom";
import Main from "../pages/main";
import Home from "../pages/home/home";
import Club from "../pages/club/club";
import ManageClub from "../pages/club/manageclub";
import Event from "../pages/event/event";
import PageOne from "../pages/other/pageone";
import PageTwo from "../pages/other/pagetwo";
import Login from "../pages/login/login";
import PrivateRoute from "./PrivateRoute";
import ViewFunding from "../pages/view_funding/view_funding";

// 更新 routes 配置
const routes = createBrowserRouter([
    {
        path: '/',
        element: <Main />,
        children: [
            {
                path: '/',
                element: <Navigate to="/login" replace />
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
                )
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
                path: 'view_funding',
                element: (
                    <PrivateRoute>
                        <ViewFunding />
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