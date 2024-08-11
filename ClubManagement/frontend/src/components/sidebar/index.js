import React from "react";
import {Layout, Menu} from "antd";
import MenuConfig from "../../config";
import * as Icon from "@ant-design/icons"
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
    return(
        <Sider trigger={null} collapsible>
            <h3 className="app-name">Club Management</h3>
            <Menu
                theme="dark"
                mode="inline"
                defaultSelectedKeys={['1']}
                items={items}
                style={{
                    height: '100%'
                }}
            />
        </Sider>
    )
}

export default SideBar