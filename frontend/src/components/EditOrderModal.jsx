import React, { useState } from 'react';
import {Modal, Form, Input, Button, message, InputNumber} from 'antd';
import axios from 'axios';

const EditOrderModal = ({ onCancel, onSaveOrder, order }) => {
    const [form] = Form.useForm();
    const [isUpdating, setIsUpdating] = useState(false);

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();

            const payload = {};
            if (values.description !== order.description) payload.description = values.description;
            if (values.price !== order.price) payload.price = values.price;

            if (Object.keys(payload).length === 0) {
                message.info("No changes to save");
                return;
            }

            setIsUpdating(true);
            await axios.patch(`/api/orders/${order.id}`, payload);
            await onSaveOrder();
            message.success("Order updated successfully");
            onCancel();
        } catch (error) {
            message.error("Failed to update order");
        } finally {
            setIsUpdating(false);
        }
    };

    return (
        <Modal
            open={true}
            title="Edit Order"
            onCancel={onCancel}
            footer={null}
            width={400}
        >
            <Form
                form={form}
                layout="vertical"
                onFinish={handleSubmit}
                initialValues={{
                    description: order.description,
                    price: order.price,
                }}
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

export default EditOrderModal;
