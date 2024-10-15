import React, { useEffect, useState } from 'react'
import { Col, Row, Card } from 'antd'
import { useNavigate } from 'react-router-dom';
//import { getData } from '../../api'
import "./home.css"
import * as Icon from "@ant-design/icons";
import {useAuth} from '../../router/auth';
import {doCall} from "../../router/api";
const iconToElement = (name) => React.createElement(Icon[name]);
const Home = () => {
    const path = process.env.REACT_APP_API_BASE_URL;
    const userImg = require("../../assets/images/user.jpeg")
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    useEffect(() => {

        const fetchUserData = async () => {
            try {
                const res = await doCall(`${path}/student/userdetailed/info`, 'GET');
                if (res.ok === true) {
                    const data = await res.json();
                    setUser(data);
                } else {
                    setError('Failed to fetch user information');
                }
            } catch (error) {
                console.error('Error fetching user data:', error);
                setError('An error occurred while fetching user data');
            } finally {
                setLoading(false);
            }
        };
        fetchUserData();
    }, []);



    if (loading) {
        return <p>Loading...</p>;
    }

    if (error) {
        return <p style={{ color: 'red' }}>{error}</p>;
    }

    const getRoleText = (roles) => {
        const roleNames = roles.map(role => role.authority);
        if (roleNames.includes('ROLE_USER')) return 'Student';
        if (roleNames.includes('ROLE_ADMIN')) return 'Faculty Admin';
        return 'Unknown Role';
    };

    return(
    <Row className="home">
        <Col span={8}>
            <Card hoverable style={{ width: 400, textAlign: 'center' }}>
                <div className="user">
                    <img src={userImg} />
                    <div className="userinfo">
                        <p className="name">{user.name}</p>
                        <p className="access">{getRoleText(user.roles)}</p>
                    </div>
                </div>
                <div className="login-info">
                    <p>User Name:<span>{user.username}</span></p>
                    <p>Email:<span>{user.email}</span></p>
                </div>
            </Card>
        </Col>
    </Row>

    )
}

export default Home