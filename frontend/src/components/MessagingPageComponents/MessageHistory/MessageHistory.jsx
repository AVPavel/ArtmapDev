import React from 'react';
import styles from './MessageHistory.module.css';

const MessageHistory = () => {
    return (
        <div className={styles.messageHistory}>
            {/* This will eventually be populated with messages from DB */}
            <textarea
                className={styles.readOnlyBox}
                readOnly
                value="Message history will appear here..."
            />
        </div>
    );
}

export default MessageHistory;
