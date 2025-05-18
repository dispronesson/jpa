import React, { useEffect, useState } from 'react';
import {List, Typography, Collapse, Col, Row, Button, Empty, Tooltip, Space, message} from 'antd';
import axios from 'axios';
import CreateUserModal from "./CreateUserModal";
import {DeleteOutlined, EditOutlined, PlusOutlined} from "@ant-design/icons";
import EditUserModal from "./EditUserModal";
import AddOrderModal from "./AddOrderModal";
import EditOrderModal from "./EditOrderModal";
import ThinSpinner from "./ThinSpinner";

const { Text } = Typography;
const { Panel } = Collapse;

function MainPage() {
    const [users, setUsers] = useState([]);
    const [activeModal, setActiveModal] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);
    const [currentOrder, setCurrentOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [deletingUserId, setDeletingUserId] = useState(null);
    const [deletingOrderId, setDeletingOrderId] = useState(null);

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
        setTimeout(() => {
            setLoading(false);
        }, 400);
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

            {loading ? (
                <ThinSpinner size={40} color="#1677ff" />
            ) : users.length === 0 ? (
                <Empty description="No data"/>
            ) : (
                <Collapse accordion >
                    {users.map(user => (
                        <Panel
                            key={user.id}
                            header={
                                <div>
                                    <Row>
                                        <Space size={10}>
                                            <Text>
                                                <strong>User: {user.name}</strong>
                                            </Text>
                                            <Text type="secondary">
                                                {user.orders.length} orders
                                            </Text>
                                        </Space>
                                    </Row>
                                    <Row>
                                        <Text type="secondary">
                                            Email: {user.email}
                                        </Text>
                                    </Row>
                                </div>
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
                                            loading={deletingUserId === user.id}
                                            icon={<DeleteOutlined />}
                                            onClick={async (e) => {
                                                e.stopPropagation();
                                                setDeletingUserId(user.id);
                                                await handleDeleteUser(user.id);
                                                setDeletingUserId(null);
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
                                                <Text>
                                                    <strong>Description:</strong> {order.description}
                                                </Text>
                                            </Row>
                                            <Row>
                                                <Text>
                                                    <strong>Price:</strong> {order.price} $
                                                </Text>
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
                                                        loading={deletingOrderId === order.id}
                                                        icon={<DeleteOutlined />}
                                                        onClick={async (e) => {
                                                            e.stopPropagation();
                                                            setDeletingOrderId(order.id);
                                                            await handleDeleteOrder(order.id);
                                                            setDeletingOrderId(null);
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
                    onCreateUser={async () => {
                        await fetchUsers();
                    }}
                />
            )}

            {activeModal === 'editUser' && (
                <EditUserModal
                    onCancel={closeModal}
                    onSaveUser={async () => {
                        await fetchUsers();
                        closeModal();
                    }}
                    user={currentUser}
                />
            )}

            {activeModal === 'addOrder' && (
                <AddOrderModal
                    onCancel={closeModal}
                    onCreate={async () => {
                        await fetchUsers();
                        closeModal();
                    }}
                    id={currentUser.id}
                />
            )}

            {activeModal === 'editOrder' && (
                <EditOrderModal
                    onCancel={closeModal}
                    onSaveOrder={async () => {
                        await fetchUsers();
                        closeModal();
                    }}
                    order={currentOrder}
                />
            )}
        </div>
    );
}

export default MainPage;
