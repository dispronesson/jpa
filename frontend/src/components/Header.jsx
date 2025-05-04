import React from 'react';
import { Layout, Typography } from 'antd';

const { Header: AntHeader } = Layout;
const { Title } = Typography;

function Header() {
    const handleClick = () => {
        window.location.reload();
    };

    return (
        <AntHeader style={{ background: '#fff', padding: '6px 22px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
            <Title level={1} style={{ margin: 0, cursor: 'pointer' }} onClick={handleClick}>
                OrdersService
            </Title>
        </AntHeader>
    );
}

export default Header;
