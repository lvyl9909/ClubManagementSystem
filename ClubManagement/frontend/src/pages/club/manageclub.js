import React, { useEffect, useState } from 'react';
import {
    Col,
    Row,
    Card,
    Table,
    Button,
    Input,
    Modal,
    Form,
    Tabs,
    AutoComplete,
    Divider,
    Tag,
    DatePicker,
    message, InputNumber, TimePicker, Select
} from 'antd';
import "./club.css";
import {useLocation, useParams,useNavigate} from "react-router";
import {doCall} from "../../router/api";
import moment from "moment";
// import { handleSearchStudent, handleSelectStudent } from '../event/event';
const { Column } = Table;
const { TabPane } = Tabs;

const { Search } = Input;
const { Option } = Select;


function ManageClub() {
    const navigate = useNavigate();
    const path = process.env.REACT_APP_API_BASE_URL
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);


    const id = useParams().id;
    const [userManagedClubs, setUserManagedClubs] = useState([]);
    const { state } = useLocation();
    const [clubDetails, setClubDetails] = useState(null);
    const [adminData, setAdminData] = useState([]);

    const [isModalVisible, setIsModalVisible] = useState(false);


    const [searching, setSearching] = useState(false);
    const [searchResults, setSearchResults] = useState([]);
    const [selectedStudent, setSelectedStudent] = useState(null);
    const [studentSearchQuery, setStudentSearchQuery] = useState('');
    const [isAlreadyAdmin, setIsAlreadyAdmin] = useState(false);
    const [user, setUser] = useState({});

    const [allEvents, setAllEvents] = useState([]);
    const [clubEvents, setClubEvents] = useState([]);
    const [selectedRowKeys, setSelectedRowKeys] = useState([]); // Selected rows for multi-select
    const [bulkLoading, setBulkLoading] = useState(false); // Loading state for bulk action

    const [editModalVisible, setEditModalVisible] = useState(false); // Modal visibility state
    const [currentEvent, setCurrentEvent] = useState(null); // Event being edited
    const [form] = Form.useForm(); // Ant Design form instance
    const [venues, setVenues] = useState([]); // State to hold the list of venues
    const [createModalVisible, setCreateModalVisible] = useState(false);

    const [fundingApplications, setFundingApplications] = useState([]);
    const [createFundingModalVisible, setCreateFundingModalVisible] = useState(false); // Modal for creating new funding


    const checkAuthorization = () => {
        if (state && state.isAuthorized) {
            return true;
        } else {
            message.error("You do not have permission to manage this club.");
            navigate('/club');
            return false;
        }
    };


    const fetchStudents = async () => {
        if (checkAuthorization()) {
            try {
                const response = await doCall(`${path}/student/admin/?id=${id}`, 'GET');
                const data = await response.json();
                setAdminData(data);
                setLoading(false);
            } catch (err) {
                setError('Failed to fetch student data');
                setLoading(false);
            }
        }
    };
    useEffect(() => {
        fetchStudents();
    }, [id]);



    const handleRemoveAdmin = async (studentId) => {
        try {
            const response = await doCall(`${path}/student/admin/delete?clubId=${id}&studentId=${studentId}`, 'DELETE');
            const data = await response.json();
            setAdminData(data);
            setLoading(false);
        } catch (err) {
            setError('Failed to fetch admin data');
            setLoading(false);
        }
    };

    const confirmRemoveAdmin = (studentId) => {
        Modal.confirm({
            title: 'Are you sure you want to remove this admin?',
            content: 'This action cannot be undone.',
            okText: 'Yes',
            cancelText: 'No',
            onOk: async () => {
                await handleRemoveAdmin(studentId);
                fetchStudents();
            },
        });
    };

    const handleAddAdmin = async () => {
        if (!selectedStudent) return;
        try {
            await doCall(`${path}/student/admin/add?clubId=${id}&studentId=${selectedStudent.id}`, 'POST');
            setAdminData([...adminData, { ...selectedStudent, studentId: selectedStudent.id }]);
            setSelectedStudent(null); // Clear selection after adding
            setStudentSearchQuery(''); // Reset search query
            setIsAlreadyAdmin(false); // Reset admin check
        } catch (error) {
            setError('Failed to add admin');
        }
    };

    const handleSearchStudent = async (value) => {
        setStudentSearchQuery(value);
        if (value) {
            const res = await doCall(`${path}/student/students/?query=${value}`, 'GET');
            const data = await res.json();
            setSearchResults(data); // List of students returned from search
        }
    };

    // Handle selecting a student from search results
    const handleSelectStudent = (value) => {
        const student = searchResults.find(student => student.email === value);
        if (student) {
            setSelectedStudent(student);
            const isAdmin = adminData.some(admin => admin.id === student.id);
            setIsAlreadyAdmin(isAdmin);
        }
    };

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const res = await doCall(`${path}/student/userdetailed/info`, 'GET');
                if (res.ok === true) {
                    const data = await res.json();
                    setUser(data); // Store the current user's information
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
    }, [id]);




        const fetchClubEvents = async () => {
            try {
                const res = await doCall(`${path}/student/events/?id=-1`, 'GET');
                if (res.ok) {
                    const data = await res.json();
                    const filteredEvents = data.filter(event => String(event.clubId) === String(id));
                    setAllEvents(data);
                    setClubEvents(filteredEvents);
                } else {
                    setError('Failed to load events');
                }
            } catch (error) {
                console.error('Error fetching events:', error);
                setError('An error occurred while fetching events');
            } finally {
                setLoading(false);
            }
        };
        useEffect(() => {
            fetchClubEvents(); // Fetch events for the current club when the component loads
        }, []);


    const onSelectChange = (newSelectedRowKeys) => {
        console.log('selectedRowKeys changed: ', newSelectedRowKeys);
        setSelectedRowKeys(newSelectedRowKeys); // Set the selected rows
    };

    const rowSelection = {
        selectedRowKeys,
        onChange: onSelectChange,
    };

    const hasSelected = selectedRowKeys.length > 0;

    // Handle bulk delete for selected events
    const handleBulkDelete = () => {
        Modal.confirm({
            title: 'Are you sure you want to delete the selected events?',
            content: `This action will delete ${selectedRowKeys.length} event(s).`,
            okText: 'Yes',
            cancelText: 'No',
            onOk: async () => {
                setBulkLoading(true);
                try {
                    const res = await doCall(`${path}/student/events/delete`, 'POST', { eventsIds: selectedRowKeys });

                    if (res.ok) {
                        setSelectedRowKeys([]); // Clear the selection after performing the action
                        fetchClubEvents(); // Refresh the events after deletion
                    } else {
                        setError('Failed to delete events');
                    }
                } catch (error) {
                    console.error('Error deleting events:', error);
                    setError('An error occurred while deleting events');
                } finally {
                    setBulkLoading(false);
                }
            },
        });
    };

    const getStatusTagColor = (status) => {
        if (status === 'Ongoing'||status === 'Approved') return 'green';
        if (status === 'Cancelled'||status === 'Rejected') return 'red';
        if (status === 'Submitted') return 'purple';
        if (status === 'Reviewed') return 'blue'
        return 'volcano';
    };


    const fetchVenues = async () => {
        try {
            const res = await doCall(`${path}/student/venues/getAllVenue`, 'GET');
            if (res.ok) {
                const data = await res.json();
                setVenues(data); // Set the venues for dropdown
            } else {
                message.error('Failed to fetch venue data');
            }
        } catch (error) {
            console.error('Error fetching venue data:', error);
            message.error('An error occurred while fetching venue data');
        }
    };
    useEffect(() => {
        fetchVenues(); // Fetch venues when the component loads
    }, []);
    // Edit event handler

    const handleEditEvent = async (event) => {
        setCurrentEvent(event);
        // try {
            // // Fetch all venues from the backend
            // const res = await doCall(`${path}/student/venues/getAllVenue`, 'GET');
            // if (res.ok) {
            //     const venueData = await res.json();
            //     setVenues(venueData);
            //
            //     // Find the venue name based on venueId
            //     const currentVenue = venueData.find(venue => venue.id === event.venueId);
            //
            //     // Set form fields with event and venue details
        const selectedVenue = venues.find(venue => venue.id === event.venueId);

        // Set form fields with event details, including the preselected venue ID
        form.setFieldsValue({
            title: event.title,
            description: event.description,
            date: moment(event.date, 'YYYY-MM-DD'),
            time: moment(event.time, 'HH:mm:ss'),
            venueId: selectedVenue ? selectedVenue.id : '',  // Prefill the venue ID
            cost: event.cost,
            capacity: event.capacity,
        });

        // Show the modal
        setEditModalVisible(true);
    };
    //         } else {
    //             message.error('Failed to fetch venue details');
    //         }
    //     } catch (error) {
    //         console.error('Error fetching venue details:', error);
    //         message.error('An error occurred while fetching venue details');
    //     }
    //
    //     // Show modal after setting form values
    //     setEditModalVisible(true);
    // };


    // Handle updating the event
    const handleUpdateEvent = async (values) => {
        try {
            const updatedEvent = {
                ...currentEvent,
                title: values.title,
                description: values.description,
                date: values.date.format('YYYY-MM-DD'),  // Format date for backend
                time: values.time.format('HH:mm:ss'),    // Format time for backend
                venueId: values.venueId,  // Correctly send the venue ID
                cost: values.cost,
                capacity: values.capacity,
                clubId: currentEvent.clubId,  // Ensure clubId is included
            };
            console.log("events",updatedEvent)

            const res = await doCall(`${path}/student/events/update`, 'POST', updatedEvent);
            if (res.ok) {
                message.success('Event updated successfully');
                setEditModalVisible(false); // Close the modal
                fetchClubEvents(); // Refresh the events
            } else {
                message.error('Failed to update event');
            }
        } catch (error) {
            console.error('Error updating event:', error);
            message.error('An error occurred while updating the event');
        }
    };


    const handleCreateNewEvent = () => {
        form.resetFields(); // Reset form for a new event
        setCreateModalVisible(true); // Show create modal
    };

    // Handle event creation submission
    const handleCreateEvent = async (values) => {
        try {
            const newEvent = {
                title: values.title,
                description: values.description,
                date: values.date.format('YYYY-MM-DD'),
                time: values.time.format('HH:mm:ss'),
                venueId: values.venueId,
                cost: values.cost,
                capacity: values.capacity,
                clubId: id,
            };

            const res = await doCall(`${path}/student/events/save`, 'POST', newEvent);
            if (res.ok) {
                message.success('Event created successfully');
                setCreateModalVisible(false); // Close the modal
                fetchClubEvents(); // Refresh the events list
            } else {
                throw new Error('Failed to create event');
            }
        } catch (error) {
            const errorData = await error.response.json();
            if (errorData.reason && errorData.reason === 'Budget not enough') {
                message.error('The event cost exceeds the club budget.');
            } else {
                message.error(errorData.message || 'An error occurred while creating the event');
            }
        }
    };

    const fetchFundingApplications = async () => {
        try {
            const res = await doCall(`${path}/student/fundingappliction?clubid=${id}`, 'GET');
            if (res.ok) {
                const data = await res.json();
                setFundingApplications(data);  // Set the funding applications in state
            } else {
                message.error('Failed to fetch funding applications');
            }
        } catch (error) {
            console.error('Error fetching funding applications:', error);
            message.error('An error occurred while fetching funding applications');
        }
    };
    useEffect(() => {
        fetchFundingApplications();
    }, [id]);

    const handleCreateFundingApplication = async (values) => {
        try {
            const newFunding = {
                title: values.title,
                description: values.description,
                amount: values.amount,
                semester: values.semester,
                date: moment().format('YYYY-MM-DD'),
                clubId: id,
            };

            const res = await doCall(`${path}/student/fundingappliction/save`, 'POST', newFunding);
            if (res.ok) {
                message.success('Funding application submitted successfully');
                setCreateFundingModalVisible(false);  // Close the modal
                fetchFundingApplications();  // Refresh the funding list
            } else {
                message.error('Failed to submit funding application');
            }
        } catch (error) {
            console.error('Error submitting funding application:', error);
            message.error('An error occurred while submitting the funding application');
        }
    };

    return (
        <div className="club-management">
            <Tabs defaultActiveKey="1">
                <TabPane tab="Members" key="1">
                    <Row gutter={[16, 16]} style={{ width: '100%' }}>
                        <Col span={16}>
                            <Card title="Current Admin">
                                <Table dataSource={adminData} rowKey="id">
                                    <Column title="Name" dataIndex="name" key="name" />
                                    <Column title="Username" dataIndex="username" key="username" />
                                    <Column title="Email" dataIndex="email" key="email" />
                                    <Column title="Action" key="action" render={(text, record) => (
                                        <Button
                                            onClick={() => confirmRemoveAdmin(record.id)}
                                            type="link"
                                            disabled={user && user.id === record.id}  // Disable the button if it's the current user
                                        >
                                            Remove Admin
                                        </Button>
                                    )} />
                                </Table>
                            </Card>
                        </Col>
                        <Col span={8}>
                            <Card title="Add Admin">
                                <AutoComplete
                                    placeholder="Search by Email"
                                    value={studentSearchQuery}
                                    onChange={handleSearchStudent}
                                    onSelect={handleSelectStudent}
                                    options={searchResults.map(student => ({
                                        label: `${student.name} (${student.email})`,
                                        value: student.email,
                                    }))}
                                    style={{ width: '100%', marginBottom: 16 }}
                                />
                                {selectedStudent && (
                                    <div style={{ marginTop: 15 }}>
                                        <p><strong>Name:</strong> {selectedStudent.name}</p>
                                        <p><strong>Email:</strong> {selectedStudent.email}</p>
                                        <Divider />
                                        <Button
                                            type="primary"
                                            onClick={handleAddAdmin}
                                            disabled={isAlreadyAdmin}
                                            style={{ marginTop: 16 }}
                                        >
                                            {isAlreadyAdmin ? 'Already an Admin' : 'Confirm Add Admin'}
                                        </Button>
                                    </div>
                                )}
                            </Card>
                        </Col>
                    </Row>
                </TabPane>

                <TabPane tab="Events" key="2">
                    <Row gutter={[16, 16]} style={{ width: '100%' }}>
                        <Col span={24}>
                            <Card title="Club Events">
                                <Button
                                    danger
                                    style={{ marginBottom: '16px' }}
                                    onClick={handleBulkDelete}
                                    disabled={!hasSelected}
                                    loading={bulkLoading}
                                >
                                    {bulkLoading ? 'Deleting...' : 'Cancel Selected'}
                                </Button>
                                {hasSelected ? ` Selected ${selectedRowKeys.length} ${selectedRowKeys.length === 1 ? 'item' : 'items'}` : ''}
                                <Button type="primary" style={{ marginLeft: '20px' }} onClick={handleCreateNewEvent}>
                                    Create New Event
                                </Button>



                                <Table
                                    rowSelection={rowSelection} // Enable row selection
                                    dataSource={clubEvents}
                                    rowKey="id"
                                    loading={loading}
                                >
                                    <Column title="Title" dataIndex="title" key="title" />
                                    <Column title="Date" dataIndex="date" key="date" />
                                    {/*<Column title="Venue" dataIndex="venueName" key="venueName" />*/}
                                    <Column
                                        title="Status"
                                        dataIndex="status"
                                        key="status"
                                        render={status => <Tag color={getStatusTagColor(status)}>{status}</Tag>}
                                    />
                                    <Column
                                        title="Action"
                                        key="action"
                                        render={(text, record) => (
                                            <Button
                                                type="primary" ghost
                                                onClick={() => handleEditEvent(record)}
                                                disabled={record.status === 'Cancelled'}  // Disable if status is "Cancelled"
                                            >
                                                Edit
                                            </Button>
                                        )}
                                    />
                                    {/*<Column*/}
                                    {/*    title="Action"*/}
                                    {/*    key="action"*/}
                                    {/*    render={(text, record) => (*/}
                                    {/*        <>*/}
                                    {/*            <Button*/}
                                    {/*                type="primary"*/}
                                    {/*                style={{ marginRight: 8 }}*/}
                                    {/*                onClick={() => handleEditEvent(record.eventId)}*/}
                                    {/*            >*/}
                                    {/*                Edit*/}
                                    {/*            </Button>*/}
                                    {/*            <Button*/}
                                    {/*                type="danger"*/}
                                    {/*                onClick={() => handleBulkDelete([record.eventId])}*/}
                                    {/*            >*/}
                                    {/*                Delete*/}
                                    {/*            </Button>*/}
                                    {/*        </>*/}
                                    {/*    )}*/}
                                    {/*/>*/}
                                </Table>
                            </Card>
                        </Col>
                    </Row>
                    <Modal
                        visible={editModalVisible}
                        title="Edit Event"
                        onCancel={() => setEditModalVisible(false)}
                        footer={null}
                    >
                        <Form form={form} onFinish={handleUpdateEvent}>
                            <Form.Item name="title" label="Title" rules={[{ required: true, message: 'Please input the title' }]}>
                                <Input />
                            </Form.Item>
                            <Form.Item name="description" label="Description" rules={[{ required: true, message: 'Please input the description' }]}>
                                <Input.TextArea rows={3} />
                            </Form.Item>
                            <Form.Item name="date" label="Date" rules={[{ required: true, message: 'Please select the date' }]}>
                                <DatePicker />
                            </Form.Item>
                            <Form.Item name="time" label="Time" rules={[{ required: true, message: 'Please select the time' }]}>
                                <TimePicker format="HH:mm:ss" />
                            </Form.Item>
                            <Form.Item name="venueId" label="Venue Name" rules={[{ required: true, message: 'Please select a venue' }]}>
                                <Select placeholder="Select a venue">
                                    {venues.map(venue => (
                                        <Option key={venue.id} value={venue.id}>
                                            {venue.name}  {/* Display venue name but return venue ID */}
                                        </Option>
                                    ))}
                                </Select>
                            </Form.Item>
                            <Form.Item name="cost" label="Cost" rules={[{ required: true, message: 'Please input the cost' }]}>
                                <InputNumber min={0} precision={2} style={{ width: '100%' }} />
                            </Form.Item>
                            <Form.Item name="capacity" label="Capacity" rules={[{ required: true, message: 'Please input the capacity' }]}>
                                <InputNumber min={1} style={{ width: '100%' }} />
                            </Form.Item>
                            <Form.Item>
                                <Button type="primary" htmlType="submit">
                                    Update Event
                                </Button>
                            </Form.Item>
                        </Form>
                    </Modal>
                    <Modal
                        visible={createModalVisible}
                        title="Create New Event"
                        onCancel={() => setCreateModalVisible(false)}
                        footer={null}
                    >
                        <Form form={form} onFinish={handleCreateEvent}>
                            <Form.Item name="title" label="Title" rules={[{ required: true, message: 'Please enter the event title' }]}>
                                <Input />
                            </Form.Item>
                            <Form.Item name="description" label="Description" rules={[{ required: true, message: 'Please enter the event description' }]}>
                                <Input.TextArea rows={3} />
                            </Form.Item>
                            <Form.Item name="date" label="Date" rules={[{ required: true, message: 'Please select the date' }]}>
                                <DatePicker />
                            </Form.Item>
                            <Form.Item name="time" label="Time" rules={[{ required: true, message: 'Please select the time' }]}>
                                <TimePicker format="HH:mm:ss" />
                            </Form.Item>
                            <Form.Item name="venueId" label="Venue Name" rules={[{ required: true, message: 'Please select a venue' }]}>
                                <Select placeholder="Select a venue">
                                    {venues.map(venue => (
                                        <Option key={venue.id} value={venue.id}>
                                            {venue.name}  {/* Display venue name but return venue ID */}
                                        </Option>
                                    ))}
                                </Select>
                            </Form.Item>
                            <Form.Item name="cost" label="Cost" rules={[{ required: true, message: 'Please enter the cost' }]}>
                                <InputNumber min={0} precision={2} style={{ width: '100%' }} />
                            </Form.Item>
                            <Form.Item name="capacity" label="Capacity" rules={[{ required: true, message: 'Please enter the capacity' }]}>
                                <InputNumber min={1} style={{ width: '100%' }} />
                            </Form.Item>
                            <Form.Item>
                                <Button type="primary" htmlType="submit">
                                    Create Event
                                </Button>
                            </Form.Item>
                        </Form>
                    </Modal>
                </TabPane>

                <TabPane tab="Funding" key="3">
                    <Row gutter={[16, 16]} style={{ width: '100%' }}>
                        <Col span={24}>
                            <Card title="Club Funding Applications">
                                <Button
                                    type="primary"
                                    style={{ marginBottom: '16px' }}
                                    onClick={() => setCreateFundingModalVisible(true)}
                                >
                                    Create New Funding Application
                                </Button>
                                <Table dataSource={fundingApplications} rowKey="id">
                                    <Column title="Description" dataIndex="description" key="description" />
                                    <Column title="Amount" dataIndex="amount" key="username" />
                                    <Column title="Semester" dataIndex="semester" key="semester" />
                                    <Column title="date" dataIndex="date" key="date" />
                                    <Column title="status" dataIndex="status" key="status"
                                            render={status => <Tag color={getStatusTagColor(status)}>{status}</Tag>}/>
                                </Table>
                            </Card>
                        </Col>
                    </Row>

                    {/* Modal for creating new funding application */}
                    <Modal
                        visible={createFundingModalVisible}
                        title="Create New Funding Application"
                        onCancel={() => setCreateFundingModalVisible(false)}
                        footer={null}
                    >
                        <Form form={form} onFinish={handleCreateFundingApplication}>
                            <Form.Item name="title" label="Title" rules={[{ required: true, message: 'Please enter the title' }]}>
                                <Input />
                            </Form.Item>
                            <Form.Item name="description" label="Description" rules={[{ required: true, message: 'Please enter the description' }]}>
                                <Input.TextArea rows={3} />
                            </Form.Item>
                            <Form.Item name="amount" label="Amount" rules={[{ required: true, message: 'Please enter the amount' }]}>
                                <InputNumber min={0} precision={2} style={{ width: '100%' }} />
                            </Form.Item>
                            <Form.Item name="semester" label="Semester" rules={[{ required: true, message: 'Please select a semester' }]}>
                                <Select placeholder="Select a semester">
                                    <Option value={1}>1</Option>
                                    <Option value={2}>2</Option>
                                </Select>
                            </Form.Item>
                            {/* The date will be set to today's date automatically */}
                            <Form.Item>
                                <Button type="primary" htmlType="submit">
                                    Submit Application
                                </Button>
                            </Form.Item>
                        </Form>
                    </Modal>
                </TabPane>
            </Tabs>
        </div>
    );
}

export default ManageClub;
