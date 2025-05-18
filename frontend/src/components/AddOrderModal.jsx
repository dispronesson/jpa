import React, { useState } from 'react';
import {Modal, Form, Input, Button, message, InputNumber} from 'antd';
import axios from 'axios';

const AddOrderModal = ({ onCancel, onCreate, id }) => {
    const [form] = Form.useForm();
    const [isCreating, setIsCreating] = useState(false);

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();
            setIsCreating(true);
            await axios.post(`/api/users/${id}/order`, values);
            await onCreate();
            message.success("Order created successfully");
            onCancel();
        } catch (error) {
            message.error("Failed to create order");
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <Modal
            open={true}
            title="Create Order"
            onCancel={onCancel}
            footer={null}
            width={400}
        >
            <Form
                form={form}
                layout="vertical"
                onFinish={handleSubmit}
            >
                <Form.Item
                    label="Description"
                    name="description"
                    rules={[
                        { required: true, message: 'Enter order description' },
                        {
                            validator: (_, value) => {
                                if (value && (value.length < 2 || value.length > 50)) {
                                    return Promise.reject('Description must be 2-50 length');
                                }
                                return Promise.resolve();
                            }
                        },
                        { whitespace: true, message: 'Description cannot be blank'}
                    ]}
                >
                    <Input />
                </Form.Item>

                <Form.Item
                    label="Price"
                    name="price"
                    rules={[
                        { required: true, message: 'Enter order price' },
                    ]}
                >
                    <InputNumber
                        style={{ width: '100%' }}
                        min={0.5}
                        step={0.5}
                    />
                </Form.Item>

                <Form.Item>
                    <Button
                        type="primary"
                        htmlType="submit"
                        block
                        loading={isCreating}
                        disabled={isCreating}
                    >
                        Create Order
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default AddOrderModal;
