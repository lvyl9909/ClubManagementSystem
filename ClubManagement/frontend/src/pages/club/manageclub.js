import React, { useEffect, useState } from 'react';
import {Col, Row, Card, Table, Button, Input, Modal, Form, Tabs, AutoComplete, Divider, Tag} from 'antd';
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
    const [selectedStudent, setSelectedStudent] = useState(null);
    const [studentSearchQuery, setStudentSearchQuery] = useState('');
    const [isAlreadyAdmin, setIsAlreadyAdmin] = useState(false);
    const [user, setUser] = useState({});

    const [allEvents, setAllEvents] = useState([]);
    const [clubEvents, setClubEvents] = useState([]);
    const [selectedRowKeys, setSelectedRowKeys] = useState([]); // Selected rows for multi-select
    const [bulkLoading, setBulkLoading] = useState(false); // Loading state for bulk action

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
            const response = await doCall(`${path}/student/admin/delete?clubId=${id}&studentId=${studentId}`, 'POST');
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
                    // Convert the selectedRowKeys array into a comma-separated string
                    const eventsIds = JSON.stringify(selectedRowKeys);

                    // Pass the event IDs as a query parameter in the URL
                    const res = await doCall(`${path}/student/events/deleteEvent?eventsId=${encodeURIComponent(eventsIds)}`, 'POST');

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
        if (status === 'Ongoing') return 'green';
        if (status === 'Cancelled') return 'red';
        return 'volcano';
    };

    // Edit event handler
    const handleEditEvent = (eventId) => {
        console.log('Edit event:', eventId);
        // You can implement the logic to navigate to an event edit page or open a modal here
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
                                <div style={{ marginBottom: 16 }}>
                                    <Button
                                        type="danger"
                                        onClick={handleBulkDelete}
                                        disabled={!hasSelected}
                                        loading={bulkLoading}
                                    >
                                        {bulkLoading ? 'Deleting...' : 'Delete Selected'}
                                    </Button>
                                    {hasSelected ? ` Selected ${selectedRowKeys.length} ${selectedRowKeys.length === 1 ? 'item' : 'items'}` : ''}
                                </div>

                                <Table
                                    rowSelection={rowSelection} // Enable row selection
                                    dataSource={clubEvents}
                                    rowKey="id"
                                    loading={loading}
                                >
                                    <Column title="Title" dataIndex="title" key="title" />
                                    <Column title="Date" dataIndex="date" key="date" />
                                    <Column title="Venue" dataIndex="venueName" key="venueName" />
                                    <Column
                                        title="Status"
                                        dataIndex="status"
                                        key="status"
                                        render={status => <Tag color={getStatusTagColor(status)}>{status}</Tag>}
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
                </TabPane>

                <TabPane tab="Funding" key="3">
                    <h3>Funding Section</h3>
                </TabPane>
            </Tabs>
        </div>
    );
}

export default ManageClub;
