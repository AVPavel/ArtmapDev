import React, { useState } from 'react';
import styles from './MessagingPage.module.css';
import Navbar from "../../components/Navbar/Navbar";

import EventListItem from "../../components/MessagingPageComponents/EventListItem/EventListItem";
import ChatHeader from "../../components/MessagingPageComponents/ChatHeader/ChatHeader";
import MessageHistory from "../../components/MessagingPageComponents/MessageHistory/MessageHistory";
import MessageInput from "../../components/MessagingPageComponents/MessageInput/MessageInput";

const initialEvents = ["Event 1", "Event 2", "Event 3", "Event 4"];

const MessagingPage = () => {
    const [selectedEvent, setSelectedEvent] = useState(null);

    const handleEventClick = (eventName) => {
        setSelectedEvent(eventName);
    }


    return (
        <div className={styles.container}>
            <Navbar/>
            <div className={styles.body}>z
                <aside className={styles.sidebar}>
                    <ul className={styles.eventList}>
                        {initialEvents.map((eventName, index) => (
                            <li key={index}>
                                <EventListItem
                                    eventName={eventName}
                                    onClick={() => handleEventClick(eventName)}
                                    isSelected={selectedEvent === eventName}
                                />
                            </li>
                        ))}
                    </ul>
                </aside>
                <main className={styles.chatArea}>
                    <ChatHeader title={selectedEvent || "Select an event"}/>
                    <MessageHistory/>
                    <MessageInput/>
                </main>
            </div>
        </div>
    )
}

export default MessagingPage;
