import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Spin, message, Button } from 'antd'; // 导入 Button 组件
import { doCall } from '../../router/api';
import { useAuth } from '../../router/auth';

const ViewFunding = () => {
    const [fundingApplications, setFundingApplications] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { user } = useAuth();
    const path = process.env.REACT_APP_API_BASE_URL;

    useEffect(() => {

        console.log(user)
        const fetchFundingApplications = async () => {
            setLoading(true);
            try {
                // 调用后端 API 获取所有资金申请数据
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

    // Approve Funding Application
    const handleApprove = async (applicationId) => {
        try {
            const response = await doCall(`${path}/admin/fundingapplication/approve?id=${applicationId}`, 'POST');
            if (response.ok) {
                message.success('Funding application approved');
                setFundingApplications(fundingApplications.filter(funding => funding.id !== applicationId)); // 更新UI
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
            } else {
                message.error('Failed to reject funding application');
            }
        } catch (error) {
            message.error('Failed to reject funding application');
        }
    };

    if (loading) {
        return <Spin size="large" />;
    }

    if (error) {
        return <div style={{ color: 'red' }}>{error}</div>;
    }

    return (
        <div style={{ margin: '20px' }}>
            {fundingApplications.length > 0 ? (
                fundingApplications.map((funding, index) => (
                    <Card key={index} style={{ marginBottom: '20px' }}>
                        <Descriptions bordered>
                            <Descriptions.Item label="Description">{funding.description}</Descriptions.Item>
                            <Descriptions.Item label="Amount">{funding.amount}</Descriptions.Item>
                            <Descriptions.Item label="Semester">{funding.semester}</Descriptions.Item>
                            <Descriptions.Item label="Club">{funding.club?.name || 'N/A'}</Descriptions.Item>
                            <Descriptions.Item label="Status">{funding.status}</Descriptions.Item>
                            <Descriptions.Item label="Date">{funding.date}</Descriptions.Item>
                        </Descriptions>
                        {/* Approve and Reject Buttons */}
                        <div style={{ marginTop: '20px', textAlign: 'right' }}>
                            <Button type="primary" onClick={() => handleApprove(funding.id)} style={{ marginRight: '10px' }}>
                                Approve
                            </Button>
                            <Button type="danger" onClick={() => handleReject(funding.id)}>
                                Reject
                            </Button>
                        </div>
                    </Card>
                ))
            ) : (
                <div>No funding applications available</div>
            )}
        </div>
    );
};

export default ViewFunding;
