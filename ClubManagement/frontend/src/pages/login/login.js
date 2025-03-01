import React,{ useState, useEffect }  from 'react'
import { Form, Input, Button } from 'antd';
import "./login.css"
import { useNavigate } from 'react-router-dom'
import {useAuth} from '../../router/auth';


const Login = () => {
    const path = process.env.REACT_APP_API_BASE_URL
    const { login, authenticating, authenticationError, user  } = useAuth();
    // const [username, setUsername] = useState('');
    // const [password, setPassword] = useState('');
    // const [role, setRole] = useState("user");
    const [error, setError] = useState('');
    const navigate = useNavigate()

    useEffect(() => {
        if (user) {
            if (user.authorities?.includes('ROLE_ADMIN')) {
                navigate('/view_funding');
            } else if (user.authorities?.includes('ROLE_USER')) {
                navigate('/home');
            }
        }
    }, [user, navigate]);

    const handleLogin = async (values) => {
        const { username, password } = values;
        setError('');

        try {
            await login(username, password);
            if(authenticationError){
                return;
            }
        } catch (error) {
            setError('Invalid Username or Password! Please try again.');
        }
    };

    return (
        <Form
            className="login-container"
            onFinish={handleLogin}
        >
            <div className="login_title">Login to the System</div>

            {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

            <Form.Item
                label="Account"
                name="username"
                rules={[{ required: true, message: 'Please input your username!' }]}
            >
                <Input placeholder="Please enter username" />
            </Form.Item>
            <Form.Item
                label="Password"
                name="password"
                rules={[{ required: true, message: 'Please input your password!' }]}
            >
                <Input.Password placeholder="Please enter password" />
            </Form.Item>
            <Form.Item className="login-button">
                <Button type="primary" htmlType="submit" loading={authenticating}>
                    {authenticating ? 'Logging in...' : 'Login'}
                </Button>
            </Form.Item>
        </Form>
    );
};

export default Login;