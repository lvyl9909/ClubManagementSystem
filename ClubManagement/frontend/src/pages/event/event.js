import React,{ useState, useEffect }  from "react";
import {useParams} from "react-router";
import {
    Table,
    Tag,
    Space,
    Button,
    Col,
    Row,
    Input,
    Form,
    Modal,
    InputNumber,
    TimePicker,
    DatePicker,
    Select
} from 'antd';
import {Link} from "react-router-dom";
import {doCall} from "../../router/api";
const { Column } = Table;
const { Option } = Select;




function Event() {
    //const { clubId } = useParams();
    const path = process.env.REACT_APP_API_BASE_URL
    //const { id } = useParams();
    const [events, setEvents] = useState([]);
    const [clubs, setClubs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();

    useEffect(() => {
        const fetchEvent = async () => {
            try {


                const res = await doCall(`${path}/student/events/?id=-1`,'GET', );

                // const response = await fetch(`${path}/student/events/?id=-1`, {
                //     method: 'GET',
                //     headers: {
                //         'Content-Type': 'application/json', // 设置内容类型
                //         'Authorization': `${type} ${token}`, // 使用 Bearer token 进行身份验证
                //     }
                // });
                if (res.ok) {
                    const data = await res.json(); // 解析 JSON 数据
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

    useEffect(() => {
        const fetchClubs = async () => {
            try {
                const response = await fetch(`${path}/student/clubs/?id=-1`);
                if (response.ok) {
                    const data = await response.json();
                    setClubs(data);
                } else {
                    setError('Failed to load club information');
                }
            } catch (error) {
                console.error('Error:', error);
                setError('An error occurred while fetching the club information');
            }
        };

        fetchClubs();
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
                time: values.time.format('HH:mm:ss'),
                venueName: values.venueName,
                cost: values.cost,
                clubId: 1
            };
            const res = await doCall(`${path}/student/events/save`,'POST',{newEvent} );

            // const response = await fetch(`${path}/student/events/save`, {
            //     method: 'POST',
            //     headers: {
            //         'Content-Type': 'application/json',
            //         'Authorization': `${type} ${token}`, // 使用 Bearer token 进行身份验证
            //     },
            //     body: JSON.stringify(newEvent),
            // });

            if (res.ok) {
                const createdEvent = await res.json();
                setEvents([...events, createdEvent]);  // Add the new event to the list
                setIsModalVisible(false);
                form.resetFields();
            } else {
                //console.log(clubId)
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
                    <Button  onClick={showModal}>
                        Create Event
                    </Button>
                </Col>
            </Row>
            <Table dataSource={events} rowKey="id">
                <Column title="Club Id" dataIndex="clubId" key="clubId" />
                <Column title="Title" dataIndex="title" key="title" />
                <Column title="Description" dataIndex="description" key="description" />
                <Column title="Date" dataIndex="date" key="date" />
                <Column title="Time" dataIndex="time" key="time" />
                <Column title="Venue" dataIndex="venueName" key="venueName" />
                {/*<Column title="Cost" dataIndex="cost" key="cost" />*/}
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
                        <TimePicker format="HH:mm:ss" />
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
                    {/*<Form.Item*/}
                    {/*    name="clubId"*/}
                    {/*    label="Select Club"*/}
                    {/*    rules={[{ required: true, message: 'Please select a club!' }]}*/}
                    {/*>*/}
                    {/*    <Select*/}
                    {/*        placeholder="Select a club"*/}
                    {/*        // No need for search or filtering here, just display the club names*/}
                    {/*    >*/}
                    {/*        {clubs.map(club => (*/}
                    {/*            <Option key={club.id} value={club.name}>*/}
                    {/*                {club.name}  /!* Display the club name *!/*/}
                    {/*            </Option>*/}
                    {/*        ))}*/}
                    {/*    </Select>*/}
                    {/*</Form.Item>*/}
                    <Form.Item
                        name="clubId"
                        label="Select Club"
                        initialValue={1}  // Default selection to "Tech Enthusiasts"
                        rules={[{ required: true, message: 'Please select a club!' }]}
                    >
                        <Select placeholder="Select a club">
                            <Option value={1}>Tech Enthusiasts</Option>
                        </Select>
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
export default Event;