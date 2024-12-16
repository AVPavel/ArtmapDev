import React from 'react';
import styles from './EventListItem.module.css';

const EventListItem = ({ eventName, onClick, isSelected }) => {
    return (
        <div
            className={`${styles.eventItem} ${isSelected ? styles.selected : ''}`}
            onClick={onClick}
        >
            {eventName}
        </div>
    );
}

export default EventListItem;
