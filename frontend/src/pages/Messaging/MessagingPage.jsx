import React, {useEffect, useRef, useState} from 'react';
import styles from './MessagingPage.module.css';
import Navbar from "../../components/Navbar/Navbar";

import SockJS from 'sockjs-client';
import {Client} from '@stomp/stompjs';

import EventListItem from "../../components/MessagingPageComponents/EventListItem/EventListItem";
import ChatHeader from "../../components/MessagingPageComponents/ChatHeader/ChatHeader";
import MessageHistory from "../../components/MessagingPageComponents/MessageHistory/MessageHistory";
import MessageInput from "../../components/MessagingPageComponents/MessageInput/MessageInput";

const initialEvents = [
    { id: 1, name: "Event 1" },
    { id: 2, name: "Event 2" },
    { id: 3, name: "Event 3" },
    { id: 4, name: "Event 4" }
];

const MessagingPage = () => {
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [messages, setMessages] = useState([]);
    const currentUser = localStorage.getItem("username"); // Or from your auth context


    //reference for the stomp client
    const stompClientRef = useRef(null);

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');

        const stompClient = new Client({
            webSocketFactory: () => socket, // Fix: Wrap in function
            reconnectDelay: 5000,
            connectHeaders: {
                Authorization: `Bearer ${localStorage.getItem("jwt")}`,
            },
            onConnect: () => {
                console.log("Connected!");
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
        });

        stompClient.activate();
        stompClientRef.current = stompClient;

        return () => {
            stompClientRef.current?.deactivate();
        };
    }, []);

    useEffect(() =>{
        if(!selectedEvent || !stompClientRef.current) return;

        setMessages([]);

        //subscribe to the topic
        const subscription = stompClientRef.current.subscribe(
            `/topic/events/${selectedEvent.id}`,
            (message) => {
                const msgBody = JSON.parse(message.body);
                setMessages(prev => [...prev, {
                    ...msgBody,
                    id: msgBody.id,          // From MessageDTO
                    sender: msgBody.sender,   // From MessageDTO
                    content: msgBody.content, // From MessageDTO
                    timestamp: msgBody.timestamp
                }]);

            }
        );

        //Fetch history of messages
        // Update your message history fetch call
        fetch(`http://localhost:8080/api/messages/${selectedEvent.id}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem("jwt")}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => setMessages(data))
            .catch(err => console.error('Error fetching messages:', err));
        //cleanup
        return () => {
            subscription.unsubscribe();
        }

    },[selectedEvent])

    const handleEventClick = (event) => {
        setSelectedEvent(event);
    }


    return (

// Update the render section
        <div className={styles.container}>
            <Navbar/>
            <div className={styles.body}>
                <aside className={styles.sidebar}>
                    <ul className={styles.eventList}>
                        {initialEvents.map((event) => (
                            <li key={event.id}>
                                <EventListItem
                                    eventName={event.name}
                                    onClick={() => handleEventClick(event)}
                                    isSelected={selectedEvent?.id === event.id}
                                />
                            </li>
                        ))}
                    </ul>
                </aside>
                <main className={styles.chatArea}>
                    <ChatHeader title={selectedEvent?.name || "Select an event"}/>
                    <MessageHistory messages={messages} currentUser={currentUser}/>
                    <MessageInput onSend={(content) => {
                        if (!selectedEvent || !stompClientRef.current) return;

                        const message = {
                            content,
                            eventId: selectedEvent.id,
                            sender: currentUser
                        };

                        stompClientRef.current.publish({
                            destination: `/app/chat.sendMessage`,
                            body: JSON.stringify(message),
                            headers: {
                                'content-type': 'application/json'
                            }
                        });
                    }}/>
                </main>
            </div>
        </div>
    )
}

export default MessagingPage;
