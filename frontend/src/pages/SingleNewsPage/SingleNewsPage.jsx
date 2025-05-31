// SingleNewsPage.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Navbar from '../../components/Navbar/Navbar/Navbar';
import Footer from '../../components/Footer/Footer';
import styles from './SingleNewsPage.module.css';
import { useParams } from 'react-router-dom';

const SingleNewsPage = () => {
    const { id } = useParams();
    const [news, setNews] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [newsList, setNewsList] = useState([]);

    useEffect(() => {
        const fetchNews = async () => {
            try {
                const jwt = localStorage.getItem('jwt');
                if (!jwt) {
                    setError('JWT token not found');
                    setLoading(false);
                    return;
                }

                const response = await axios.get(`http://localhost:8080/api/news/${id}`, {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });

                const allNewsResponse = await axios.get('http://localhost:8080/api/news', {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });

                setNews(response.data);
                setNewsList(allNewsResponse.data);
                setLoading(false);
            } catch (err) {
                console.error('Error fetching news:', err);
                setError('Failed to fetch news');
                setLoading(false);
            }
        };

        fetchNews();
    }, [id]);

    const handleNext = () => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % newsList.length);
    };

    const handlePrev = () => {
        setCurrentIndex((prevIndex) => (prevIndex - 1 + newsList.length) % newsList.length);
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    const currentNews = newsList[currentIndex];

    return (
        <div className={styles.pageContainer}>
            <Navbar />
            <div className={styles.container}>
                <h1 className={styles.title}>{currentNews.title}</h1>
                <div className={styles.content}>
                    {currentNews.photo ? (
                        <img
                            src={`data:image/jpeg;base64,${currentNews.photo}`}
                            alt={currentNews.title}
                            className={styles.image}
                        />
                    ) : (
                        <div>No photo available</div>
                    )}
                    <p className={styles.details}>{currentNews.content}</p>
                </div>
                <div className={styles.navigation}>
                    <button onClick={handlePrev} className={styles.navButton}>Previous Article</button>
                    <button onClick={handleNext} className={styles.navButton}>Next Article</button>
                </div>
            </div>
            <Footer />
        </div>
    );
};

export default SingleNewsPage;
