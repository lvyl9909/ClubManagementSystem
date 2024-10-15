import React, { useEffect, useState } from 'react';
import {Card, Descriptions, Spin, message, Button, Modal, Row, Col, Table, Tabs,Tag} from 'antd'; // 导入必要组件
import { doCall } from '../../router/api';
import { useAuth } from '../../router/auth';
const { Column } = Table;
const { TabPane } = Tabs;
const ViewFunding = () => {
    const [fundingApplications, setFundingApplications] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { user } = useAuth();
    const path = process.env.REACT_APP_API_BASE_URL;
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [currentApplication, setCurrentApplication] = useState(null); // 存储当前查看的申请详情

    useEffect(() => {
        console.log(user);

        const fetchFundingApplications = async () => {
            setLoading(true);
            try {
                const response = await doCall(`${path}/admin/fundingapplication`, 'GET');
                if (response.ok) {
                    const data = await response.json(); // 解析 JSON 数据
                    console.log(data);
                    setFundingApplications(data); // 将解析后的数据设置为状态
                } else {
                    setError('Failed to load funding applications');
                }
            } catch (err) {
                setError('Failed to fetch funding applications');
                message.error('Failed to load funding applications');
            } finally {
                setLoading(false);
            }
        };

        fetchFundingApplications();

    }, [path]);

    // 打开弹出窗口并设置当前资金申请
    const showModal = (application) => {
        setCurrentApplication(application);
        setIsModalVisible(true);
    };

    // 关闭弹出窗口
    const handleModalClose = () => {
        setIsModalVisible(false);
        setCurrentApplication(null); // 清除当前的资金申请信息
    };

    // Approve Funding Application
    const handleApprove = async (applicationId) => {
        try {
            const response = await doCall(`${path}/admin/fundingapplication/approve?id=${applicationId}`, 'POST');
            if (response.ok) {
                message.success('Funding application approved');
                setFundingApplications(fundingApplications.filter(funding => funding.id !== applicationId)); // 更新UI
                handleModalClose(); // 关闭窗口
            } else {
                message.error('Failed to approve funding application');
            }
        } catch (error) {
            message.error('Failed to approve funding application');
        }
    };

    // Reject Funding Application
    const handleReject = async (applicationId) => {
        try {
            const response = await doCall(`${path}/admin/fundingapplication/reject?id=${applicationId}`, 'POST');
            if (response.ok) {
                message.success('Funding application rejected');
                setFundingApplications(fundingApplications.filter(funding => funding.id !== applicationId)); // 更新UI
                handleModalClose(); // 关闭窗口
            } else {
                message.error('Failed to reject funding application');
            }
        } catch (error) {
            message.error('Failed to reject funding application');
        }
    };

    const getStatusTag = (status) => {
        switch (status) {
            case 'Approved':
                return <Tag color="green">Approved</Tag>;
            case 'Rejected':
                return <Tag color="red">Rejected</Tag>;
            case 'Submitted':
                return <Tag color="purple">Submitted</Tag>;
            default:
                return <Tag>{status}</Tag>;
        }
    };

    if (loading) {
        return <Spin size="large" />;
    }

    if (error) {
        return <div style={{ color: 'red' }}>{error}</div>;
    }



//     return (
//         <div style={{ margin: '20px' }}>
//             {fundingApplications.length > 0 ? (
//                 fundingApplications.map((funding, index) => (
//                     <Card key={index} style={{ marginBottom: '20px' }}>
//                         <Descriptions bordered>
//                             <Descriptions.Item label="Description">{funding.description}</Descriptions.Item>
//                             <Descriptions.Item label="Club">{funding.club?.name || 'N/A'}</Descriptions.Item>
//                         </Descriptions>
//                         {/* View Button */}
//                         <div style={{ marginTop: '20px', textAlign: 'right' }}>
//                             <Button type="primary" onClick={() => showModal(funding)}>
//                                 View
//                             </Button>
//                         </div>
//                     </Card>
//                 ))
//             ) : (
//                 <div>No funding applications available</div>
//             )}
//
//             {/* Modal for showing details with Approve/Reject buttons */}
//             <Modal
//                 title="Funding Application Details"
//                 visible={isModalVisible}
//                 onCancel={handleModalClose}
//                 footer={[
//                     <Button key="reject" danger onClick={() => handleReject(currentApplication.id)}>
//                         Reject
//                     </Button>,
//                     <Button key="approve" type="primary" onClick={() => handleApprove(currentApplication.id)}>
//                         Approve
//                     </Button>
//                 ]}
//                 width={800} // 设置弹窗宽度自适应
//             >
//                 {currentApplication && (
//                     <Descriptions bordered column={1} layout="vertical">
//                         <Descriptions.Item label="Description">{currentApplication.description}</Descriptions.Item>
//                         <Descriptions.Item label="Amount">{currentApplication.amount}</Descriptions.Item>
//                         <Descriptions.Item label="Semester">{currentApplication.semester}</Descriptions.Item>
//                         <Descriptions.Item label="Club">{currentApplication.club?.name || 'N/A'}</Descriptions.Item>
//                         <Descriptions.Item label="Status">{currentApplication.status}</Descriptions.Item>
//                         <Descriptions.Item label="Date">{currentApplication.date}</Descriptions.Item>
//                     </Descriptions>
//                 )}
//             </Modal>
//         </div>
//     );
// };
    const approvedApplications = fundingApplications.filter(app => app.status === 'Approved');
    const rejectedApplications = fundingApplications.filter(app => app.status === 'Rejected');
    const activeApplications = fundingApplications.filter(app => app.status !== 'Approved' && app.status !== 'Rejected');

    return (
        <div style={{ margin: '20px' }}>
            <Tabs defaultActiveKey="1">
                <TabPane tab="Approved Applications" key="1">
                    <Table dataSource={approvedApplications} rowKey="id">
                        <Column title="Club" dataIndex={['club', 'name']} key="club" render={text => text || 'N/A'} />
                        <Column title="Description" dataIndex="description" key="description" />
                        <Column
                            title="Action"
                            key="action"
                            render={(_, record) => (
                                <Button type="primary" ghost onClick={() => showModal(record)}>
                                    View
                                </Button>
                            )}
                        />
                    </Table>
                </TabPane>

                <TabPane tab="Rejected Applications" key="2">
                    <Table dataSource={rejectedApplications} rowKey="id">
                        <Column title="Club" dataIndex={['club', 'name']} key="club" render={text => text || 'N/A'} />
                        <Column title="Description" dataIndex="description" key="description" />
                        <Column
                            title="Action"
                            key="action"
                            render={(_, record) => (
                                <Button type="primary" ghost onClick={() => showModal(record)}>
                                    View
                                </Button>
                            )}
                        />
                    </Table>
                </TabPane>

                <TabPane tab="Active Applications" key="3">
                    <Table dataSource={activeApplications} rowKey="id">
                        <Column title="Club" dataIndex={['club', 'name']} key="club" render={text => text || 'N/A'} />
                        <Column title="Description" dataIndex="description" key="description" />
                        <Column
                            title="Action"
                            key="action"
                            render={(_, record) => (
                                <>
                                    <Button type="primary" ghost onClick={() => showModal(record)} style={{ marginRight: '10px' }}>
                                        View
                                    </Button>
                                </>
                            )}
                        />
                    </Table>
                </TabPane>
            </Tabs>

            {/* Modal for showing detailed funding application information */}
            <Modal
                title="Funding Application Details"
                visible={isModalVisible}
                onCancel={handleModalClose}
                footer={
                    currentApplication && currentApplication.status !== 'Approved' && currentApplication.status !== 'Rejected' ? (
                        <div style={{ textAlign: 'center' }}>
                            <Button
                                key="reject"
                                type="primary" danger
                                onClick={() => handleReject(currentApplication.id)}
                                style={{ marginRight: '10px' }}
                            >
                                Reject
                            </Button>
                            <Button
                                key="approve"
                                type="primary"
                                onClick={() => handleApprove(currentApplication.id)}
                                style={{ backgroundColor: 'ForestGreen', color: 'white' }}
                            >
                                Approve
                            </Button>
                        </div>
                    ) : null
                }
            >
                {currentApplication && (
                    <Descriptions bordered column={1} layout="vertical">
                        <Descriptions.Item label="Club">{currentApplication.club?.name || 'N/A'}</Descriptions.Item>
                        <Descriptions.Item label="Description">{currentApplication.description}</Descriptions.Item>
                        <Descriptions.Item label="Amount">{currentApplication.amount}</Descriptions.Item>
                        <Descriptions.Item label="Semester">{currentApplication.semester}</Descriptions.Item>
                        <Descriptions.Item label="Status">{getStatusTag(currentApplication.status)}</Descriptions.Item>
                        <Descriptions.Item label="Date">{currentApplication.date}</Descriptions.Item>
                    </Descriptions>
                )}
            </Modal>
        </div>
    );
};

export default ViewFunding;
