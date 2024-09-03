import React,{ useState }  from 'react'
import { Form, Input, Button, message } from 'antd';
import "./login.css"
import { useNavigate, Navigate } from 'react-router-dom'
import { useAuth } from '../../router/auth';


const Login = () => {
    const path = process.env.REACT_APP_API_BASE_URL
    const { login, authenticating, authenticationError } = useAuth();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState("user");
    const [error, setError] = useState('');
    const navigate = useNavigate()

    const handleLogin = async () => {
        await login(username, password);
        if (!authenticationError) {
            // Navigate to the dashboard or home page
            window.location.href = '/Home';
        }
    };


    return (
        <Form
            className="login-container"
            onFinish={handleLogin}
        >
            <div className="login_title">Login to the System</div>
            <Form.Item
                label="Account"
                name="username" rules={[{ required: true, message: 'Please input your username!' }]}
            >
                <Input placeholder="Please enter username" />
            </Form.Item>
            <Form.Item
                label="password"
                name="password" rules={[{ required: true, message: 'Please input your password!' }]}
            >
                <Input.Password placeholder="Please enter password" />
            </Form.Item>
            <Form.Item className="login-button">
                <Button type="primary" htmlType="submit">Login</Button>
            </Form.Item>
        </Form>
    )
}

export default Login;