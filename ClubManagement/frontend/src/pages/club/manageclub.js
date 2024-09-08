import React, { useEffect, useState } from 'react';
import {Col, Row, Card, Table, Button, Input, Modal, Form, Tabs, AutoComplete, Divider} from 'antd';
import "./club.css";
import {useParams} from "react-router";
import {doCall} from "../../router/api";
// import { handleSearchStudent, handleSelectStudent } from '../event/event';
const { Column } = Table;
const { TabPane } = Tabs;

const { Search } = Input;


function ManageClub() {
    const path = process.env.REACT_APP_API_BASE_URL
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);


    const id = useParams().id;
    const [clubDetails, setClubDetails] = useState(null);
    const [adminData, setAdminData] = useState([]);

    const [isModalVisible, setIsModalVisible] = useState(false);


    const [searching, setSearching] = useState(false);
    const [searchResults, setSearchResults] = useState([]);
    const [selectedStudent, setSelectedStudent] = useState(null); // Holds selected student's details (email and studentId)
    const [studentSearchQuery, setStudentSearchQuery] = useState('');
    const [isAlreadyAdmin, setIsAlreadyAdmin] = useState(false);
    const [user, setUser] = useState({}); // Current user info

    const [events, setEvents] = useState([]);

    const [isAddEventVisible, setAddEventVisible] = useState(false);
    const [newEvent, setNewEvent] = useState({ title: '', date: '', venue: '' });




        const fetchStudents = async () => {
            try {
                const response = await doCall(`${path}/student/admin/?id=${id}`, 'GET');
                const data = await response.json();
                setAdminData(data);
                setLoading(false);
            } catch (err) {
                setError('Failed to fetch student data');
                setLoading(false);
            }
        };
    useEffect(() => {
        fetchStudents();
    }, [id]);



    const handleRemoveAdmin = async (studentId) => {
        try {
            const response = await doCall(`${path}/student/admin/delete?clubId=${id}&studentId=${studentId}`, 'GET');
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

    const addEvent = () => {
        setEvents([...events, { key: Date.now().toString(), ...newEvent }]);
        setAddEventVisible(false);
        setNewEvent({ title: '', date: '', venue: '' });
    };

    const modifyEvent = (key) => {
        alert(`Modify event with key: ${key}`);
    };

    const deleteEvent = (key) => {
        setEvents(events.filter((event) => event.key !== key));
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
                        <Col span={16}>
                            {/*<Card title="Events List">*/}
                            {/*    <Table dataSource={events} rowKey="key">*/}
                            {/*        <Column title="Title" dataIndex="title" key="title" />*/}
                            {/*        <Column title="Date" dataIndex="date" key="date" />*/}
                            {/*        <Column title="Venue" dataIndex="venue" key="venue" />*/}
                            {/*        <Column title="Action" key="action" render={(text, record) => (*/}
                            {/*            <>*/}
                            {/*                <Button onClick={() => modifyEvent(record.key)} type="link">Modify</Button>*/}
                            {/*                <Button onClick={() => deleteEvent(record.key)} type="link">Delete</Button>*/}
                            {/*            </>*/}
                            {/*        )} />*/}
                            {/*    </Table>*/}
                            {/*    <Button type="primary" onClick={() => setAddEventVisible(true)}>*/}
                            {/*        Add Event*/}
                            {/*    </Button>*/}
                            {/*    <Modal*/}
                            {/*        title="Add Event"*/}
                            {/*        visible={isAddEventVisible}*/}
                            {/*        onCancel={() => setAddEventVisible(false)}*/}
                            {/*        onOk={addEvent}*/}
                            {/*    >*/}
                            {/*        <Form>*/}
                            {/*            <Form.Item label="Title">*/}
                            {/*                <Input value={newEvent.title} onChange={(e) => setNewEvent({ ...newEvent, title: e.target.value })} />*/}
                            {/*            </Form.Item>*/}
                            {/*            <Form.Item label="Date">*/}
                            {/*                <Input value={newEvent.date} onChange={(e) => setNewEvent({ ...newEvent, date: e.target.value })} />*/}
                            {/*            </Form.Item>*/}
                            {/*            <Form.Item label="Venue">*/}
                            {/*                <Input value={newEvent.venue} onChange={(e) => setNewEvent({ ...newEvent, venue: e.target.value })} />*/}
                            {/*            </Form.Item>*/}
                            {/*        </Form>*/}
                            {/*    </Modal>*/}
                            {/*</Card>*/}
                        </Col>
                    </Row>
                </TabPane>

                <TabPane tab="Funding" key="3">
                    <h3>Funding Section</h3>
                </TabPane>
            </Tabs>
        </div>
    );
}

export default ManageClub;
