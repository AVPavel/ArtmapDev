// NewsPageElement.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './NewsPageElement.module.css';

const NewsPageElement = ({ title, details, newsId }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/news/${newsId}`); // Navigate to the news detail page
    };

    return (
        <div className={styles.newsContainer} onClick={handleClick}>
            <h2 className={styles.newsTitle}>{title}</h2>
            <p className={styles.newsDetails}>{details}</p>
        </div>
    );
};

export default NewsPageElement;
