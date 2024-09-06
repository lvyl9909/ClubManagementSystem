import React,{ useState, useEffect }  from "react";
import {useParams} from "react-router";
import {Table, Tag, Space, Button, Col, Row, Input, Form, Modal} from 'antd';
import {Link} from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import {doCall} from "../../router/api";
const { Column } = Table;


function Club() {
    const path = process.env.REACT_APP_API_BASE_URL;
    const [clubs, setClubs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchClub = async () => {
            try {
                const res = await doCall(`${path}/student/clubs/?id=-1`, 'GET');
                if (res.ok === true) {
                    const data = await res.json();
                    setClubs(data);
                } else {
                    setError('Failed to load club information');
                }
            } catch (error) {
                console.error('Error:', error);
                setError('An error occurred while fetching the club information');
            } finally {
                setLoading(false);
            }
        };
        fetchClub();
    }, []);

    const handleCancel = () => {
        setIsModalVisible(false);
    };

    const handleCreate = async (values) => {
        try {
            const newClub = {
                name: values.name,
                description: values.description,
            };
            const res = await doCall(`${path}/student/clubs/save`, 'POST', { newClub });

            if (res.ok) {
                const createdClub = await res.json();
                setClubs([...clubs, createdClub]);
                setIsModalVisible(false);
                form.resetFields();
            } else {
                console.error('Failed to create club');
            }
        } catch (error) {
            console.error('Error creating club:', error);
        }
    };

    if (loading) {
        return <p>Loading club information...</p>;
    }

    if (error) {
        return <p style={{ color: 'red' }}>{error}</p>;
    }

    return (
        <>
            <Row justify="start" style={{ marginBottom: 16 }}>
                <Col>
                    <h2 style={{ fontSize: "32px", fontWeight: "bold" }}>My Managed Club</h2>
                </Col>
            </Row>
            <Table dataSource={clubs} rowKey="id">
                <Column title="Name" dataIndex="name" key="name" />
                <Column title="Description" dataIndex="description" key="description" />
                <Column
                    title="Action"
                    key="action"
                    render={(text, record) => (
                        <Button
                            style={{ backgroundColor: "#1890ff", borderColor: "#1890ff", color: "#fff" }}
                            onClick={() => navigate(`/club/manage`)}  // 跳转到管理页面
                        >
                            Manage
                        </Button>
                    )}
                />
            </Table>
        </>
    );
}

export default Club;