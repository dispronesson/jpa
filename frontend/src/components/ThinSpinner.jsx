import React from 'react';

const ThinSpinner = ({ size = 40, color = '#1677ff' }) => {
    const borderSize = size * 0.1; // 10% от размера — толщина линии

    const spinnerStyle = {
        width: size,
        height: size,
        border: `${borderSize}px solid ${color}40`, // светлая подложка
        borderTop: `${borderSize}px solid ${color}`, // активная сторона
        borderRadius: '50%',
        animation: 'spin 1s linear infinite',
        boxSizing: 'border-box'
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'flex-start',
            height: '100vh',
            paddingTop: '10vh'
        }}>
            <div style={spinnerStyle}></div>
        </div>
    );
};

export default ThinSpinner;
