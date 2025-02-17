import React from 'react';
import styles from './ServiceCard.module.css';

const ServiceCard = ({ image, title, date, address }) => {
    return (
        <div className={styles.cardContainer}>
            <div className={styles.imageContainer}>
                <img src={image} alt={title} />
            </div>
            <div className={styles.content}>
                <h3 className={styles.title}>{title}</h3>
                <div className={styles.date}>{date}</div>
                <div className={styles.address}>{address}</div>
            </div>
        </div>
    );
};

export default ServiceCard;