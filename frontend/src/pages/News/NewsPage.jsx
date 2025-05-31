// NewsPage.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Navbar from '../../components/Navbar/Navbar/Navbar';
import Footer from '../../components/Footer/Footer';
import NewsPageElement from '../../components/NewsPageElement/NewsPageElement';
import styles from './NewsPage.module.css';

const NewsPage = () => {
    const [news, setNews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchNews = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/news');
                console.log('Fetched news:', response.data);

                // Map the API response to the expected structure
                const mappedNews = response.data.map(newsItem => ({
                    id: newsItem.id, // Include the news ID
                    title: newsItem.title,
                    details: newsItem.content ? newsItem.content.substring(0, 100) + '...' : 'No content available.', // Show a preview of the news details or a default message
                    photo: newsItem.photo // Include the photo
                }));

                setNews(mappedNews);
                setLoading(false);
            } catch (err) {
                console.error('Error fetching news:', err);
                setError('Failed to fetch news');
                setLoading(false);
            }
        };

        fetchNews();
    }, []);

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
                <h1 className={styles.title}>News Page</h1>
                <div className={styles.newsList}>
                    {news.map((newsItem, index) => (
                        <NewsPageElement
                            key={index}
                            title={newsItem.title}
                            details={newsItem.details}
                            newsId={newsItem.id}
                            photo={newsItem.photo} // Pass the photo to NewsPageElement
                        />
                    ))}
                </div>
            </div>
            <Footer />
        </div>
    );
};

export default NewsPage;
