import React from "react";
import {Layout, Menu} from "antd";
import MenuConfig from "../../config/config";
import * as Icon from "@ant-design/icons"
import { useNavigate } from 'react-router-dom'
const { Header, Sider, Content } = Layout;

const iconToElement = (name) => React.createElement(Icon[name]);
const items = MenuConfig.map((icon) => {
    const child = {
        key: `${icon.path}`,
        icon: iconToElement(icon.icon),
        label: `${icon.label}`
    }
    if (icon.children) {
        child.children = icon.children.map(item => {
            return {
                key: item.path,
                label: item.label
            }
        })
    }
    return child
})
const SideBar =() => {
    const navigate = useNavigate()
    const selectMenu = (e) => {
        navigate(e.key)
    }
    return(
        <Sider trigger={null} collapsible>
            <h3 className="app-name">Club Management</h3>
            <Menu
                mode="inline"
                theme="dark"
                style={{
                    height: '100%',
                    borderRight: 0,
                }}
                items={items}
                onClick={selectMenu}
            />
        </Sider>
    )
}

export default SideBar