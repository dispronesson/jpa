import React, { useState } from 'react';
import { Modal, Form, Input, Button, notification } from 'antd';
import axios from 'axios';

const CreateUserModal = ({ onCancel, onCreateUser }) => {
    const [form] = Form.useForm();
    const [emailError, setEmailError] = useState('');
    const [isCreating, setIsCreating] = useState(false);

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();

            const email = values.email;

            const response = await axios.get(`/api/users/email/${email}`);

            if (response.data === true) {
                setEmailError('User with this email already exists.');
                return;
            }

            setIsCreating(true);

            await axios.post('/api/users', values);

            notification.success({
                message: 'User created successfully',
            });

            onCreateUser();

        } catch (error) {
            if (error.response?.status === 400) {
                notification.error({
                    message: 'Failed to create user',
                    description: 'There was an issue creating the user.',
                });
            }
        } finally {
            setIsCreating(false);
        }
    };

    const handleEmailChange = () => {
        setEmailError('');
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
                    <Input onChange={handleEmailChange} />
                </Form.Item>

                {emailError && (
                    <div style={{ color: 'red', marginTop: '-22px'}}>
                        {emailError}
                    </div>
                )}

                <Form.Item>
                    <Button
                        type="primary"
                        htmlType="submit"
                        block
                        loading={isCreating}
                        disabled={isCreating}
                    >
                        Create
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default CreateUserModal;
