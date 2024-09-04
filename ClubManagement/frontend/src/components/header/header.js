import React from "react";
import { Button, Layout, Dropdown, Avatar, Space } from 'antd';
import './index.css'
import {MenuFoldOutlined} from "@ant-design/icons";

const { Header, Sider, Content } = Layout;

const UpperHeader =() => {
    const items = [
        {
            key: '1',
            label: (
                <a target="_blank" rel="noopener noreferrer">
                    Account
                </a>
            ),
        },
        {
            key: '2',
            label: (
                <a onClick={() => logout} target="_blank" rel="noopener noreferrer" >
                    Logout
                </a>
            ),
        }
    ]
    const logout = () => {
        localStorage.removeItem('token')
        //navigate('/login')
    }
    return (
        <Header className="header-container">
            <Button
                type="text"
                icon={<MenuFoldOutlined/>}
                style={{
                    fontSize: '16px',
                    width: 64,
                    height: 32,
                    backgroundColor:"grey"
                }}
            />
            <Dropdown
                menu={{items}}
            >
                <a onClick={(e) => e.preventDefault()}>
                    <Avatar size={36} src={<img src={require("../../assets/images/user.png")}/> }/>
                </a>
            </Dropdown>
        </Header>
    )
}

export default UpperHeader