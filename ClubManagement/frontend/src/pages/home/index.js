import React, { useEffect, useState } from 'react'
import { Col, Row, Card, Table } from 'antd'
//import { getData } from '../../api'
import "./home.css"
import * as Icon from "@ant-design/icons";
import {useAuth} from '../../router/auth';
const iconToElement = (name) => React.createElement(Icon[name]);
const Home = () => {
    const userImg = require("../../assets/images/user.png")
    return(
    <Row className="home">
        <Col span={8}>
            <Card hoverable>
                <div className="user">
                    <img src={userImg} />
                    <div className="userinfo">
                        <p className="name">Ben</p>
                        <p className="access">Admin</p>
                    </div>
                </div>
                <div className="login-info">
                    <p>Student ID:<span>1435712</span></p>
                    {/*<p><span>Master of software engineering</span></p>*/}
                </div>
            </Card>
        </Col>
    </Row>

    )
}

export default Home