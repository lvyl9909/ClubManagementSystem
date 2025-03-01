import React,{ useState, useEffect }  from "react";
import {useParams} from "react-router";
import {Table, Tag, Space, Button, Col, Row, Input, Form, Modal, Typography, Divider, message} from 'antd';
import {Link} from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import {doCall} from "../../router/api";
const { Column } = Table;
const { Title } = Typography;


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
                const res = await doCall(`${path}/student/userdetailed/club`, 'GET');
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


    const handleManageClick = (clubId) => {
        const isUserManagingClub = clubs.some(club => String(club.id) === String(clubId));
        if (isUserManagingClub) {
            navigate(`/club/manage/${clubId}`, { state: { isAuthorized: true} });
        } else {
            navigate(`/club/manage/${clubId}`, { state: { isAuthorized: false } });
        }
    };

    if (loading) {
        return <p>Loading club information...</p>;
    }

    if (error) {
        return <p style={{ color: 'red' }}>{error}</p>;
    }

    // const handleCancel = () => {
    //     setIsModalVisible(false);
    // };
    //
    // const handleCreate = async (values) => {
    //     try {
    //         const newClub = {
    //             name: values.name,
    //             description: values.description,
    //         };
    //         const res = await doCall(`${path}/student/clubs/save`, 'POST', { newClub });
    //
    //         if (res.ok) {
    //             const createdClub = await res.json();
    //             setClubs([...clubs, createdClub]);
    //             setIsModalVisible(false);
    //             form.resetFields();
    //         } else {
    //             console.error('Failed to create club');
    //         }
    //     } catch (error) {
    //         console.error('Error creating club:', error);
    //     }
    // };
    //
    // if (loading) {
    //     return <p>Loading club information...</p>;
    // }
    //
    // if (error) {
    //     return <p style={{ color: 'red' }}>{error}</p>;
    // }

    return (
        <>
            <Row justify="start" style={{ marginBottom: 16 }}>
                <Divider orientation="left" orientationMargin="0">
                    My Club
                </Divider>
            </Row>
            <Table dataSource={clubs} rowKey="id">
                <Column title="Name" dataIndex="name" key="name" />
                <Column title="Description" dataIndex="description" key="description" />
                <Column
                    title="Action"
                    key="action"
                    render={(text, record) => (
                        <Button type="primary" ghost
                            onClick={() =>
                                handleManageClick(record.id)}>
                            Manage
                        </Button>
                    )}
                />
            </Table>
        </>
    );
}

export default Club;