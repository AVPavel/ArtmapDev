// NewsPageElement.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './NewsPageElement.module.css';

const NewsPageElement = ({ title, details, newsId, photo }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/news/${newsId}`); // Navigate to the news detail page
    };

    return (
        <div className={styles.newsContainer} onClick={handleClick}>
            <div className={styles.newsImageContainer}>
                {photo ? (
                    <img
                        src={`data:image/jpeg;base64,${photo}`}
                        alt={title}
                        className={styles.newsImage}
                    />
                ) : (
                    <div className={styles.noImage}>No photo available</div>
                )}
            </div>
            <div className={styles.newsTextContainer}>
                <h2 className={styles.newsTitle}>{title}</h2>
                <p className={styles.newsDetails}>{details}</p>
            </div>
        </div>
    );
};

export default NewsPageElement;
