import React, { useEffect, useState } from 'react';
import {List, Typography, Collapse, Col, Row, Button} from 'antd';
import axios from 'axios';
import CreateUserModal from "./CreateUser";

const { Text } = Typography;
const { Panel } = Collapse;

function MainPage() {
    const [users, setUsers] = useState([]);
    const [activeModal, setActiveModal] = useState(null);

    const fetchUsers = async () => {
        try {
            const response = await axios.get('/api/users');
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users: ', error);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const openModal = (type) => setActiveModal(type);

    const closeModal = () => setActiveModal(null);

    return (
        <div>
            <div>
                <Button type="primary"
                        onClick={() => openModal('createUser')}
                        style={{ marginBottom: '16px' }}>
                    Create User
                </Button>
            </div>
            <Collapse accordion >
                {users.map(user => (
                    <Panel
                        key={user.id}
                        header={
                            <Col>
                                <Row>
                                    <Text strong>User: {user.name}</Text>
                                </Row>
                                <Row>
                                    <Text type="secondary">Email: {user.email}</Text>
                                </Row>
                            </Col>
                        }
                    >
                        <List
                            dataSource={user.orders}
                            renderItem={(order, index) => (
                                <List.Item key={order.id}>
                                    <Col>
                                        <Row>
                                            <Text><b>{index + 1}.</b> Description: {order.description}</Text>
                                        </Row>
                                        <Row>
                                            <Text style={{ padding: '0 16px' }}>Price: <Text>{order.price} $</Text></Text>
                                        </Row>
                                    </Col>
                                </List.Item>
                            )}
                        />
                    </Panel>
                ))}
            </Collapse>
            {activeModal === 'createUser' && (
                <CreateUserModal
                    onCancel={closeModal}
                    onCreateUser={() => {
                        fetchUsers();
                        closeModal();
                    }}
                />
            )}
        </div>
    );
}

export default MainPage;
