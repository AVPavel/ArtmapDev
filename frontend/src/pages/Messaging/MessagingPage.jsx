import React, { useEffect, useRef, useState } from 'react';
import styles from './MessagingPage.module.css';
import Navbar from "../../components/Navbar/Navbar/Navbar";
import SockJS from 'sockjs-client';
import {Client, Stomp} from '@stomp/stompjs';
import axios from 'axios';
import EventListItem from "../../components/MessagingPageComponents/EventListItem/EventListItem";
import ChatHeader from "../../components/MessagingPageComponents/ChatHeader/ChatHeader";
import MessageHistory from "../../components/MessagingPageComponents/MessageHistory/MessageHistory";
import MessageInput from "../../components/MessagingPageComponents/MessageInput/MessageInput";

const MessagingPage = () => {
    const [selectedGroup, setSelectedGroup] = useState(null); // Renamed from selectedEvent
    const [messages, setMessages] = useState([]);
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);
    const chatAreaRef = useRef(null);       // Add this line
    const messagesEndRef = useRef(null);    // Add this line
    const [error, setError] = useState(null);
    const currentUser = localStorage.getItem("username");
    const stompClientRef = useRef(null);

    useEffect(() => {
        const fetchGroups = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/groups/all', {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("jwt")}`
                    }
                });

                const transformedGroups = response.data.map(group => ({
                    id: group.id,
                    groupName: group.name,
                    eventId: group.eventId,
                    eventName: group.eventName
                }));

                setGroups(transformedGroups);
                setLoading(false);
            } catch (err) {
                setError(err.message);
                setLoading(false);
                console.error('Error fetching groups:', err);
            }
        };

        fetchGroups();
    }, []);

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            connectHeaders: {
                Authorization: `Bearer ${localStorage.getItem("jwt")}`,
            },
            onConnect: () => {
                console.log("Connected to WebSocket");
                console.log("Subscribing to:", `/topic/events/${selectedGroup?.eventId}`);
                console.log(`sent with auth: Bearer ${localStorage.getItem("jwt")}`)
            },
            onStompError: (frame) => {
                console.error('WebSocket error:', frame.headers['message'], frame.body);
            },
        });

        stompClient.activate();
        stompClientRef.current = stompClient;

        return () => {
            stompClientRef.current?.deactivate();
        };
    }, []);

    useEffect(() => {
        if (!selectedGroup || !stompClientRef.current) return;

        setMessages([]);

        const subscription = stompClientRef.current.subscribe(
            `/topic/events/${selectedGroup.eventId}`,
            (message) => {
                const msgBody = JSON.parse(message.body);
                console.log("Received WebSocket message:", msgBody);
                if (!msgBody.content || !msgBody.sender) {
                    console.error("Invalid message format:", msgBody);
                    return;
                }
                setMessages(prev => [...prev, {
                    ...msgBody,
                    id: msgBody.id,
                    sender: msgBody.sender,
                    content: msgBody.content,
                    timestamp: msgBody.timestamp
                }]);
            }
        );

        const fetchMessages = async () => {
            try {
                const response = await fetch(
                    `http://localhost:8080/api/messages/${selectedGroup.eventId}`,
                    {
                        headers: {
                            'Authorization': `Bearer ${localStorage.getItem("jwt")}`
                        }
                    }
                );

                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                const data = await response.json();
                setMessages(data);
            } catch (err) {
                console.error('Error fetching messages:', err);
            }
        };

        fetchMessages();

        return () => subscription.unsubscribe();
    }, [selectedGroup]);

    const handleGroupClick = (group) => {
        setSelectedGroup(group);
    };

    return (
        <div className={styles.container}>
            <Navbar/>
            <div className={styles.body}>
                <aside className={styles.sidebar}>
                    {loading ? (
                        <div className={styles.loading}>Loading groups...</div>
                    ) : error ? (
                        <div className={styles.error}>Error: {error}</div>
                    ) : (
                        <ul className={styles.eventList}>
                            {groups.map((group) => (
                                <li key={group.id}>
                                    <EventListItem
                                        eventName={group.eventName}
                                        onClick={() => handleGroupClick(group)}
                                        isSelected={selectedGroup?.id === group.id}
                                    />
                                </li>
                            ))}
                        </ul>
                    )}
                </aside>

                <main className={styles.chatArea}>
                    <ChatHeader title={selectedGroup?.eventName || "Select a group"}/>
                    <MessageHistory messages={messages} currentUser={currentUser} containerRef={chatAreaRef} endRef={messagesEndRef}/>
                    <MessageInput onSend={(content) => {
                        const headers = {
                            Authorization: `Bearer ${localStorage.getItem("jwt")}`, // Include JWT in CONNECT headers
                        };
                        stompClientRef.current = Stomp.over(() => new SockJS('http://localhost:8080/ws'));
                        stompClientRef.current.connect(headers, () => {
                            console.log("Connected with JWT!");
                        });
                        console.log(selectedGroup);
                        console.log(stompClientRef.current);
                        if (!selectedGroup || !stompClientRef.current) return;

                        if (!stompClientRef.current.connected) {
                            console.error("STOMP client not connected!");
                            return;
                        }

                        // Construct the STOMP frame with proper CRLF (\r\n) line endings
                        const stompFrame = [
                            'SEND',
                            `destination:/app/chat.sendMessage`,
                            `Authorization:Bearer ${localStorage.getItem("jwt")}`,
                            `content-type:application/json`,
                            '',
                            JSON.stringify({
                                content: content,
                                eventId: selectedGroup.eventId
                            }),
                            '\x00' // Null terminator
                        ].join('\r\n');

                        console.log('Final STOMP Frame:', stompFrame);


                        stompClientRef.current.publish({
                            destination: '/app/chat.sendMessage',
                            headers: {
                                Authorization: `Bearer ${localStorage.getItem("jwt")}`,
                                'content-type': 'application/json'
                            },
                            body: JSON.stringify({
                                content: content,
                                eventId: selectedGroup.eventId
                            })
                        });
                    }}/>
                </main>
            </div>
        </div>
    );
};

export default MessagingPage;