import React, { useState } from 'react';
import {Modal, Form, Input, Button, message} from 'antd';
import axios from 'axios';

const EditUserModal = ({ onCancel, onSaveUser, user }) => {
    const [form] = Form.useForm();
    const [isUpdating, setIsUpdating] = useState(false);

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();

            const payload = {};
            if (values.name !== user.name) payload.name = values.name;
            if (values.email !== user.email) payload.email = values.email;

            if (Object.keys(payload).length === 0) {
                message.info("No changes to save");
                return;
            }

            setIsUpdating(true);
            await axios.patch(`/api/users/${user.id}`, payload);
            await onSaveUser();
            message.success("User updated successfully");
            onCancel();
        } catch (error) {
            if (error.response?.status === 409) {
                message.error('The specified email is already taken')
            } else {
                message.error("Failed to create user");
            }
        } finally {
            setIsUpdating(false);
        }
    };

    return (
        <Modal
            open={true}
            title="Edit User"
            onCancel={onCancel}
            footer={null}
            width={400}
        >
            <Form
                form={form}
                layout="vertical"
                onFinish={handleSubmit}
                initialValues={{
                    name: user.name,
                    email: user.email,
                }}
            >
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
                        loading={isUpdating}
                        disabled={isUpdating}
                    >
                        Save
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default EditUserModal;
