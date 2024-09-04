import {createBrowserRouter,Navigate} from "react-router-dom";
import Main from "../pages/main";
import Home from "../pages/home/home";
import Club from "../pages/club/club";
import Event from "../pages/event/event";
import pageOne from "../pages/other/pageone";
import pageTwo from "../pages/other/pagetwo";
import Login from "../pages/login/login";
const routes = [
    {
        path:'/',
        Component:Main,
        children:[
            {
                path:'/',
                element: <Navigate to="login" replace/> // redirect
            },
            {
                path:'home',
                Component:Home
            },
            {
                path:'club',
                    Component:Club
            },
            {
                path:'event',
                Component:Event
            },
            {
                path: 'other',
                children: [
                    {
                        path: 'pageOne',
                        Component: pageOne
                    },
                    {
                        path: 'pageTwo',
                        Component: pageTwo
                    }
                ]
            }
        ]
    },
    {
        path: '/login',
        Component: Login
    }
]

export default createBrowserRouter(routes)