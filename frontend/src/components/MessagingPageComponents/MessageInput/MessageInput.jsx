import React, { useState } from 'react';
import styles from './MessageInput.module.css';

const MessageInput = () => {
    const [inputValue, setInputValue] = useState("");

    const handleSend = () => {
        setInputValue("");
    }

    return (
        <div className={styles.messageInput}>
            <input
                type="text"
                className={styles.inputField}
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="Type your message..."
            />
            <button className={styles.sendButton} onClick={handleSend}>
                Send
            </button>
        </div>
    );
}

export default MessageInput;
