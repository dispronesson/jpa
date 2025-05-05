import React, { useEffect, useState } from 'react';
import {List, Typography, Collapse, Col, Row, Button, Empty, Tooltip, Space, message} from 'antd';
import axios from 'axios';
import CreateUserModal from "./CreateUserModal";
import {DeleteOutlined, EditOutlined, PlusOutlined} from "@ant-design/icons";
import EditUserModal from "./EditUserModal";
import AddOrderModal from "./AddOrderModal";
import EditOrderModal from "./EditOrderModal";

const { Text } = Typography;
const { Panel } = Collapse;

function MainPage() {
    const [users, setUsers] = useState([]);
    const [activeModal, setActiveModal] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);
    const [currentOrder, setCurrentOrder] = useState(null);

    const fetchUsers = async () => {
        try {
            const response = await axios.get('/api/users');
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users: ', error);
        }
    };

    const handleDeleteUser = async (id) => {
        try {
            await axios.delete(`/api/users/${id}`);
            await fetchUsers();
            message.success("User deleted successfully");
        } catch (error) {
            console.error('Error deleting user: ', error);
        }
    };

    const handleDeleteOrder = async (id) => {
        try {
            await axios.delete(`/api/orders/${id}`);
            await fetchUsers();
            message.success("Order deleted successfully");
        } catch (error) {
            console.error('Error deleting order: ', error);
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
            {users.length === 0 ? (
                <Empty description="No data"/>
            ) : (
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
                            extra={
                                <Space>
                                    <Tooltip title="Create order" mouseEnterDelay={0.5}>
                                        <Button
                                            type="text"
                                            icon={<PlusOutlined />}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                setCurrentUser(user);
                                                openModal('addOrder');
                                            }}
                                        />
                                    </Tooltip>
                                    <Tooltip title="Edit user" mouseEnterDelay={0.5}>
                                        <Button
                                            type="text"
                                            icon={<EditOutlined />}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                setCurrentUser(user);
                                                openModal('editUser');
                                            }}
                                        />
                                    </Tooltip>
                                    <Tooltip title="Delete user" mouseEnterDelay={0.5}>
                                        <Button
                                            type="text"
                                            danger
                                            icon={<DeleteOutlined />}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleDeleteUser(user.id);
                                            }}
                                        />
                                    </Tooltip>
                                </Space>
                            }
                        >
                            <List
                                dataSource={user.orders}
                                renderItem={(order, index) => (
                                    <List.Item key={order.id}>
                                        <Col>
                                            <Row>
                                                <Text><strong>{index + 1}. Description:</strong> {order.description}</Text>
                                            </Row>
                                            <Row>
                                                <Text style={{ padding: '0 13px' }}><strong>Price:</strong> <Text>{order.price} $</Text></Text>
                                            </Row>
                                        </Col>
                                        <Col>
                                            <Space>
                                                <Tooltip title="Edit order" mouseEnterDelay={0.5}>
                                                    <Button
                                                        type="text"
                                                        icon={<EditOutlined />}
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            setCurrentOrder(order);
                                                            openModal('editOrder');
                                                        }}
                                                    />
                                                </Tooltip>
                                                <Tooltip title="Delete order" mouseEnterDelay={0.5}>
                                                    <Button
                                                        type="text"
                                                        danger
                                                        icon={<DeleteOutlined />}
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            handleDeleteOrder(order.id);
                                                        }}
                                                    />
                                                </Tooltip>
                                            </Space>
                                        </Col>
                                    </List.Item>
                                )}
                            />
                        </Panel>
                    ))}
                </Collapse>
            )}

            {activeModal === 'createUser' && (
                <CreateUserModal
                    onCancel={closeModal}
                    onCreateUser={() => {
                        fetchUsers();
                        closeModal();
                    }}
                />
            )}

            {activeModal === 'editUser' && (
                <EditUserModal
                    onCancel={closeModal}
                    onSaveUser={() => {
                        fetchUsers();
                        closeModal();
                    }}
                    user={currentUser}
                />
            )}

            {activeModal === 'addOrder' && (
                <AddOrderModal
                    onCancel={closeModal}
                    onCreate={() => {
                        fetchUsers();
                        closeModal();
                    }}
                    id={currentUser.id}
                />
            )}

            {activeModal === 'editOrder' && (
                <EditOrderModal
                    onCancel={closeModal}
                    onSaveOrder={() => {
                        fetchUsers();
                        closeModal();
                    }}
                    order={currentOrder}
                />
            )}
        </div>
    );
}

export default MainPage;
