import React from 'react';
import styles from './ChatHeader.module.css';

const ChatHeader = ({ title }) => {
    return (
        <div className={styles.chatHeader}>
            {title}
        </div>
    );
}

export default ChatHeader;
