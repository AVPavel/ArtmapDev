import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Navbar from '../../components/Navbar/Navbar/Navbar';
import Footer from '../../components/Footer/Footer';
import FavEventElement from '../../components/FavEventElement/FavEventElement';
import styles from './FavoritePage.module.css';

const FavoritePage = () => {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchFavoriteEvents = async () => {
            try {
                const jwt = localStorage.getItem('jwt');
                if (!jwt) {
                    setError('JWT token not found');
                    setLoading(false);
                    return;
                }

                const response = await axios.get('http://localhost:8080/api/favorites', {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });

                console.log('Fetched favorite events:', response.data);

                // Map the API response to the expected structure
                const mappedEvents = response.data.map(event => ({
                    id: event.id, // Include the event ID
                    image: 'default_image_path.jpg', // Replace with actual image path if available
                    title: event.title,
                    details: [
                        `Location: ${event.location}`,
                        `Date: ${new Date(event.date).toLocaleString()}`,
                        `Category: ${event.categoryName}`,
                        `Cheapest Ticket: ${event.cheapestTicket}`
                    ]
                }));

                setEvents(mappedEvents);
                setLoading(false);
            } catch (err) {
                console.error('Error fetching favorite events:', err);
                setError('Failed to fetch favorite events');
                setLoading(false);
            }
        };

        fetchFavoriteEvents();
    }, []);

    const handleToggleFavorite = async (eventId, isAdding) => {
        try {
            const jwt = localStorage.getItem('jwt');
            if (!jwt) {
                setError('JWT token not found');
                return;
            }

            const endpoint = isAdding ? 'add' : 'remove';
            const response = await axios.post(`http://localhost:8080/api/favorites/${endpoint}`, null, {
                params: { eventId },
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            });

            console.log(`Event ${isAdding ? 'added to' : 'removed from'} favorites:`, response.data);

            // Update the local state
            setEvents(prevEvents =>
                isAdding
                    ? [...prevEvents, { id: eventId }] // Add the event to the list
                    : prevEvents.filter(event => event.id !== eventId) // Remove the event from the list
            );
        } catch (err) {
            console.error(`Error ${isAdding ? 'adding to' : 'removing from'} favorites:`, err);
            setError('Failed to update favorite events');
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div className={styles.pageContainer}>
            <Navbar />
            <div className={styles.container}>
                <h1 className={styles.title}>Favourite Page</h1>
                <p className={styles.description}>
                    Here are your most loved events.
                    Click on the heart symbol to delete an event from the list.
                </p>
                <div className={styles.eventsList}>
                    {events.map((event, index) => (
                        <FavEventElement
                            key={index}
                            image={event.image}
                            title={event.title}
                            details={event.details}
                            onToggleFavorite={(isAdding) => handleToggleFavorite(event.id, isAdding)}
                        />
                    ))}
                </div>
            </div>
            <Footer />
        </div>
    );
};

export default FavoritePage;
