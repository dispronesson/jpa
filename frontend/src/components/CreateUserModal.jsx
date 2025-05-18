import React, { useState } from 'react';
import {Modal, Form, Input, Button, message} from 'antd';
import axios from 'axios';

const CreateUserModal = ({ onCancel, onCreateUser }) => {
    const [form] = Form.useForm();
    const [isCreating, setIsCreating] = useState(false);

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();
            setIsCreating(true);
            await axios.post('/api/users', values);
            await onCreateUser();
            message.success("User created successfully");
            onCancel();
        } catch (error) {
            if (error.response?.status === 409) {
                message.error('The specified email is already taken')
            } else {
                message.error("Failed to create user");
            }
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <Modal
            open={true}
            title="Create User"
            onCancel={onCancel}
            footer={null}
            width={400}
        >
            <Form form={form} layout="vertical" onFinish={handleSubmit}>
                <Form.Item
                    label="Name"
                    name="name"
                    rules={[
                        { required: true, message: 'Enter a name' },
                        {
                            validator: (_, value) => {
                                if (value && (value.length < 2 || value.length > 50)) {
                                    return Promise.reject('Name must be 2-50 length');
                                }
                                return Promise.resolve();
                            }
                        },
                        { whitespace: true, message: 'Name cannot be blank'}
                    ]}
                >
                    <Input />
                </Form.Item>

                <Form.Item
                    label="Email"
                    name="email"
                    rules={[
                        { required: true, message: 'Enter a email' },
                        { type: 'email', message: 'Invalid email format' }
                    ]}
                >
                    <Input />
                </Form.Item>

                <Form.Item>
                    <Button
                        type="primary"
                        htmlType="submit"
                        block
                        loading={isCreating}
                        disabled={isCreating}
                    >
                        Create User
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default CreateUserModal;
