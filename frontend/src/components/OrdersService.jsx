import React from "react";
import { Layout } from "antd";
import Header from "./Header";
import MainPage from "./MainPage";

const { Content } = Layout

function OrdersService() {
    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Header />
            <Content style={{ padding: '16px 24px' }}>
                <MainPage />
            </Content>
        </Layout>
    )
}

export default OrdersService