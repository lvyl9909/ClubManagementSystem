import React, { useEffect, useState } from 'react';
import { Col, Row, Card, Table, Button, Input, Space } from 'antd';
import "./club.css";
import {useParams} from "react-router";
import {doCall} from "../../router/api";

const { Search } = Input;

function ManageClub() {
    const path = process.env.REACT_APP_API_BASE_URL
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);


    const id = useParams().id;
    const [clubDetails, setClubDetails] = useState(null);
    const [membersData, setMembersData] = useState([]);

    // 模拟学生数据
    const allStudents = [
        { name: 'David', studentId: '987654' },
        { name: 'Emma', studentId: '123456' },
        { name: 'Frank', studentId: '654987' },
    ];

    const [searchResult, setSearchResult] = useState(null);

    const membersColumns = [
        { title: 'Name', dataIndex: 'name', key: 'name', align: 'center' },
        { title: 'Student Username', dataIndex: 'username', key: 'username', align: 'center' },
        {
            title: 'Action',
            key: 'action',
            align: 'center',
            render: (text, record) => (
                <Button type="primary" danger style={{ width: '120px' }}>Remove Admin</Button>
            ),
        }
    ];

    useEffect(() => {

        const fetchStudents = async () => {
            try {
                // 使用 doCall 发送 GET 请求
                const response = await doCall(`${path}/student/admin/?id=${id}`, 'GET');
                const data = await response.json();
                setMembersData(data);
                setLoading(false);
            } catch (err) {
                setError('Failed to fetch student data');
                setLoading(false);
            }
        };

        fetchStudents();
    }, [id]);

    // 搜索学号的处理函数
    const onSearch = (value) => {
        const foundStudent = allStudents.find(student => student.studentId === value);
        if (foundStudent) {
            setSearchResult(foundStudent);  // 显示搜索结果
        } else {
            setSearchResult(null);  // 清除搜索结果
        }
    };

    // 将搜索的学生添加到成员列表
    const addToMembers = () => {
        if (searchResult) {
            setMembersData([...membersData, { ...searchResult, key: searchResult.studentId }]);
            setSearchResult(null);  // 清除搜索结果
        }
    };

    if (!clubDetails) {
        return (
            <div className="club-management">
                {/* 上方表格区域 */}
                <Row gutter={[16, 16]} style={{ width: '100%' }}>
                    <Col span={16}>
                        <Card title="Members List">
                            <Table dataSource={membersData} columns={membersColumns} pagination={false} />
                        </Card>
                    </Col>
                    {/* 搜索栏 */}
                    <Col span={8}>
                        <Search
                            placeholder="Search by Student ID"
                            onSearch={onSearch}
                            enterButton
                        />
                        {searchResult && (
                            <div style={{ marginTop: 16, padding: '10px', border: '1px solid #d9d9d9', borderRadius: '5px', backgroundColor: '#f5f5f5' }}>
                                <p style={{ fontSize: '16px', fontWeight: 'bold' }}>Name: {searchResult.name}</p>
                                <p style={{ fontSize: '14px', color: '#888' }}>Student ID: {searchResult.studentId}</p>
                                <Button type="primary" onClick={addToMembers}>Set as Admin</Button>
                            </div>
                        )}
                    </Col>
                </Row>

                {/* 下方按钮区域 */}
                <Row gutter={[64, 16]} style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginTop: '30px', width: '100%' }}>
                    {/* 管理事件按钮 */}
                    <Col style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                        <button className="rectangle-button">
                            <img src={require("../../assets/images/eventmanagement.png")} alt="Manage Events" />
                            <span>Manage Events</span>
                        </button>
                        <button className="rectangle-button">
                            <img src={require("../../assets/images/fundings.png")} alt="Funding Application" />
                            <span>Funding Application</span>
                        </button>
                    </Col>
                </Row>
            </div>
        );
    }

    return (
        <div>
            <h1>{clubDetails.name} - Management Page</h1>
            <p>{clubDetails.description}</p>
        </div>
    );
}

export default ManageClub;
