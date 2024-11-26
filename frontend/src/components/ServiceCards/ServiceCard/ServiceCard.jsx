import React from 'react';
import styles from './ServiceCard.module.css'

const ServiceCard = ({ icon, description}) => {
    return (
        <div className={styles.cardContainer}>
            <img src={icon} alt={description} />
            <p>{description}</p>
        </div>
    )
}

export default ServiceCard;