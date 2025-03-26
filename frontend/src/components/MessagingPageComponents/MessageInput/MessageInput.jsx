import React, { useState } from 'react';
import styles from './MessageInput.module.css';

const MessageInput = ({ onSend }) => {  // Receive onSend prop
    const [inputValue, setInputValue] = useState("");

    const handleSend = () => {
        if (inputValue.trim()) {
            onSend(inputValue);  // Call parent's send handler
            setInputValue("");
        }
    }

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSend();
        }
    }

    return (
        <div className={styles.messageInput}>
            <input
                type="text"
                className={styles.inputField}
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                onKeyPress={handleKeyPress}  // Add Enter key support
                placeholder="Type your message..."
            />
            <button className={styles.sendButton} onClick={handleSend}>
                Send
            </button>
        </div>
    );
}

export default MessageInput;