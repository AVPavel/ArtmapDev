import React, { useState } from 'react';
import styles from './FavEventElement.module.css';

const FavEventElement = ({ image, title, details, onToggleFavorite }) => {
    const [isChecked, setIsChecked] = useState(true);

    const toggleChecked = () => {
        const newState = !isChecked;
        setIsChecked(newState);
        onToggleFavorite(newState); // Call the callback with the new state
    };

    return (
        <div className={styles.eventContainer}>
            <img src={image} alt={title} className={styles.eventImage} />
            <div className={styles.eventDetails}>
                <h2 className={styles.eventTitle}>{title}</h2>
                <ul className={styles.detailsList}>
                    {details.map((detail, index) => (
                        <li key={index} className={styles.detailItem}>
                            {detail}
                        </li>
                    ))}
                </ul>
                <button
                    className={`${styles.deleteButton} ${isChecked ? styles.checked : styles.unchecked}`}
                    onClick={toggleChecked}
                >
                    {isChecked ? '❤' : '♡'}
                </button>
            </div>
        </div>
    );
};

export default FavEventElement;
