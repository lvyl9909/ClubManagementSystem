import React from "react";
import {Layout, Menu} from "antd";
import MenuConfig from "../../config/config";
import * as Icon from "@ant-design/icons"
import { useNavigate } from 'react-router-dom'
import {useAuth} from "../../router/auth";

const { Header, Sider, Content } = Layout;

const iconToElement = (name) => React.createElement(Icon[name]);

const filterMenuItemsByRole = (menuConfig, userRoles) => {
    return menuConfig
        .filter(item => item.roles.some(role => userRoles.includes(role)))
        .map(icon => {
            const child = {
                key: `${icon.path}`,
                icon: iconToElement(icon.icon),
                label: `${icon.label}`,
            };
            if (icon.children) {
                child.children = icon.children
                    .filter(subItem => subItem.roles.some(role => userRoles.includes(role)))
                    .map(subItem => ({
                        key: subItem.path,
                        label: subItem.label,
                    }));
            }
            return child;
        });
};

// const items = MenuConfig.map((icon) => {
//     const child = {
//         key: `${icon.path}`,
//         icon: iconToElement(icon.icon),
//         label: `${icon.label}`
//     }
//     if (icon.children) {
//         child.children = icon.children.map(item => {
//             return {
//                 key: item.path,
//                 label: item.label
//             }
//         })
//     }
//     return child
// })
// const SideBar =() => {
//     const {logout, user} = useAuth()
//     const navigate = useNavigate()
//     const selectMenu = async (e) => {
//         if(e.key === 'logout'){
//             if(user && user.username){
//                 await logout(user.username)
//             }
//             navigate('/login')
//         }else {
//             navigate(e.key)
//         }
//     }
//     return(
//         <Sider trigger={null} collapsible>
//             <h3 className="app-name">Club Management</h3>
//             <Menu
//                 mode="inline"
//                 theme="dark"
//                 style={{
//                     height: '100%',
//                     borderRight: 0,
//                 }}
//                 items={items}
//                 onClick={selectMenu}
//             />
//         </Sider>
//     )
// }

const SideBar = () => {
    const { logout, user } = useAuth();
    const navigate = useNavigate();

    const selectMenu = async (e) => {
        if (e.key === 'logout') {
            if (user && user.username) {
                await logout(user.username);
            }
            navigate('/login');
        } else {
            navigate(e.key);
        }
    };

    // Assuming user.roles is an array of roles like ['ROLE_ADMIN', 'ROLE_USER']
    const userRoles = user?.authorities || [];
    // Filter the menu items based on user roles
    const filteredItems = filterMenuItemsByRole(MenuConfig, userRoles);

    return (
        <Sider trigger={null} collapsible>
            <h3 className="app-name">Club Management</h3>
            <Menu
                mode="inline"
                theme="dark"
                style={{
                    height: '100%',
                    borderRight: 0,
                }}
                items={filteredItems}
                onClick={selectMenu}
            />
        </Sider>
    );
};


export default SideBar