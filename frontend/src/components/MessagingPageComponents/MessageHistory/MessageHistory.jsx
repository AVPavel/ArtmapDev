import React from 'react';
import { useEffect } from 'react';
import styles from './MessageHistory.module.css';

const MessageHistory = ({ messages, currentUser, containerRef, endRef }) => {
    useEffect(() => {
        if (endRef.current) {
            endRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    }, [messages]);

    return (
        <div className={styles.messageHistory} ref={containerRef}>
            <div className={styles.messagesContainer}>
                {messages?.map((message, index) => {
                    const isUserMessage = message.sender === currentUser;
                    const messageDate = new Date(message.timestamp);

                    return (
                        <div
                            key={message.id || index}
                            className={`${styles.message} ${isUserMessage ? styles.userMessage : ''}`}
                        >
                            <div className={styles.messageHeader}>
                                <span className={styles.sender}>
                                    {!isUserMessage && message.sender}
                                </span>
                                <span className={styles.timestamp}>
                                    {messageDate.toLocaleDateString()} {messageDate.toLocaleTimeString()}
                                </span>
                            </div>
                            <div className={styles.messageContent}>
                                {message.content}
                            </div>
                        </div>
                    );
                })}
                {messages.length === 0 && (
                    <div className={styles.noMessages}>
                        No messages yet. Be the first to start the conversation!
                    </div>
                )}
                <div ref={endRef} />
            </div>
        </div>
    );
}

export default MessageHistory;