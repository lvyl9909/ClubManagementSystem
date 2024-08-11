import {createBrowserRouter,Navigate} from "react-router-dom";
import Main from "../pages/main";
import Home from "../pages/home";
import Club from "../pages/club";
import event from "../pages/event";
import pageOne from "../pages/other/pageone";
import pageTwo from "../pages/other/pagetwo";
const routes = [
    {
        path:'/',
        Component:Main,
        children:[
            {
                path:'/',
                element: <Navigate to="home" replace/> // redirect
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
                Component:event
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
    }
]

export default createBrowserRouter(routes)