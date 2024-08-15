import React,{ useState, useEffect }  from "react";
import {useParams} from "react-router";
import {Table, Tag, Space, Button, Col, Row, Input, Form, Modal} from 'antd';
import {Link} from "react-router-dom";
const { Column } = Table;



function Club() {
    const path = process.env.REACT_APP_API_BASE_URL
    //const { id } = useParams();
    const [clubs, setClubs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();

    useEffect(() => {
        const fetchClub = async () => {
            try {
                const response = await fetch(`${path}/clubs/?id=-1`);
                console.log(response.ok,'response')
                if (response.ok===true) {
                    const data = await response.json(); // 解析 JSON 数据
                    setClubs(data);
                    console.log(data, 'data------'); // 输出解析后的数据
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

    const showModal = () => {
        setIsModalVisible(true);
    };

    const handleCancel = () => {
        setIsModalVisible(false);
    };

    const handleCreate = async (values) => {
        try {
            const newClub = {
                name: values.name,
                description: values.description,
            };
            const response = await fetch(`${path}/clubs/save`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newClub),
            });

            if (response.ok) {
                const createdClub = await response.json();
                setClubs([...clubs, createdClub]);  // Add the new club to the list
                setIsModalVisible(false);
                form.resetFields();  // Reset the form fields after successful creation
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
            <Row justify="end" style={{ marginBottom: 16 }}>
                <Col>
                    <Button onClick={showModal}>
                        Create Club
                    </Button>
                </Col>
            </Row>
            <Table dataSource={clubs} rowKey="id">
                <Column title="Name" dataIndex="name" key="name" />
                <Column title="Description" dataIndex="description" key="description" />
            </Table>
            <Modal
                title="Create a New Club"
                visible={isModalVisible}
                onCancel={handleCancel}
                footer={null}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleCreate}
                >
                    <Form.Item
                        name="name"
                        label="Club Name"
                        rules={[{ required: true, message: 'Please input the club name!' }]}
                    >
                        <Input placeholder="Enter the club name" />
                    </Form.Item>
                    <Form.Item
                        name="description"
                        label="Description"
                        rules={[{ required: true, message: 'Please input the club description!' }]}
                    >
                        <Input.TextArea placeholder="Enter the club description" />
                    </Form.Item>
                    <Form.Item>
                        <Button htmlType="submit" style={{backgroundColor: "blue", color:"white"}}>
                            Submit
                        </Button>
                        <Button style={{margin: 20}} onClick={handleCancel}>
                            Cancel
                        </Button>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
}


export default Club;