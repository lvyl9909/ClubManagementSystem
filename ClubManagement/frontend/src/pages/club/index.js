import React,{ useState, useEffect }  from "react";
import {useParams} from "react-router";
import { Table, Tag, Space } from 'antd';
import {Link} from "react-router-dom";
const { Column } = Table;



function Club() {
    const path = process.env.REACT_APP_API_BASE_URL
    //const { id } = useParams();
    const [clubs, setClubs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

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

    if (loading) {
        return <p>Loading club information...</p>;
    }

    if (error) {
        return <p style={{ color: 'red' }}>{error}</p>;
    }

    return (
            <Table dataSource={clubs} loading={loading}>
                <Column title="Name" dataIndex="name" key="name" />
                <Column title="Description" dataIndex="description" key="description" />
            </Table>
        );
}


export default Club;