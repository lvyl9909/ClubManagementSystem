import React,{ useState, useEffect }  from "react";
import {useParams} from "react-router";
import {Table, Tag, Space, Button, Col, Row, Input, Form, Modal, InputNumber, TimePicker, DatePicker} from 'antd';
import {Link} from "react-router-dom";
const { Column } = Table;



function Event() {
    const path = process.env.REACT_APP_API_BASE_URL
    //const { id } = useParams();
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();

    useEffect(() => {
        const fetchEvent = async () => {
            try {
                const response = await fetch(`${path}/events/?id=-1`);
                console.log(response.ok,'response')
                if (response.ok===true) {
                    const data = await response.json(); // 解析 JSON 数据
                    setEvents(data);
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

        fetchEvent();
    }, []);

    const showModal = () => {
        setIsModalVisible(true);
    };

    const handleCancel = () => {
        setIsModalVisible(false);
    };

    const handleCreate = async (values) => {
        try {
            const newEvent = {
                title: values.title,
                description: values.description,
                date: values.date.format('YYYY-MM-DD'),
                time: values.time.format('HH:mm'),
                venueName: values.venueName,
                cost: values.cost,
                //clubId: clubId,
            };
            const response = await fetch(`${path}/events/save`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newEvent),
            });

            if (response.ok) {
                setIsModalVisible(false);
                form.resetFields();

            } else {
                console.error('Failed to create event');
            }
        } catch (error) {
            console.error('Error creating event:', error);
        }
    };

    return (
        <>
            <Row justify="end" style={{ marginBottom: 16 }}>
                <Col>
                    <Button onClick={showModal}>
                        Create Event
                    </Button>
                </Col>
            </Row>
            <Table dataSource={events} rowKey="id">
                <Column title="Title" dataIndex="title" key="title" />
                <Column title="Description" dataIndex="description" key="description" />
                <Column title="Date" dataIndex="date" key="date" />
                <Column title="Time" dataIndex="time" key="time" />
                <Column title="Venue" dataIndex="venueName" key="venueName" />
                <Column title="Cost" dataIndex="cost" key="cost" />
            </Table>
            <Modal
                title="Create a New Event"
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
                        name="title"
                        label="Event Title"
                        rules={[{ required: true, message: 'Please input the event title!' }]}
                    >
                        <Input placeholder="Enter the event title" />
                    </Form.Item>
                    <Form.Item
                        name="description"
                        label="Description"
                        rules={[{ required: true, message: 'Please input the event description!' }]}
                    >
                        <Input.TextArea placeholder="Enter the event description" />
                    </Form.Item>
                    <Form.Item
                        name="date"
                        label="Date"
                        rules={[{ required: true, message: 'Please select the event date!' }]}
                    >
                        <DatePicker />
                    </Form.Item>
                    <Form.Item
                        name="time"
                        label="Time"
                        rules={[{ required: true, message: 'Please select the event time!' }]}
                    >
                        <TimePicker format="HH:mm" />
                    </Form.Item>
                    <Form.Item
                        name="venueName"
                        label="Venue Name"
                        rules={[{ required: true, message: 'Please input the venue name!' }]}
                    >
                        <Input placeholder="Enter the venue name" />
                    </Form.Item>
                    <Form.Item
                        name="cost"
                        label="Cost"
                        rules={[{ required: true, message: 'Please input the event cost!' }]}
                    >
                        <InputNumber min={0} step={0.01} placeholder="Enter the event cost" />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit">
                            Submit
                        </Button>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
}

export default Event