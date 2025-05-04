import React from 'react';
import styles from './IntroAbout.module.css';

const IntroAbout = () => {
    return (
        <div className={styles.introContainer}>
            <h1 className={styles.title}>
                About us
            </h1>
            <p className={styles.description}>
                At ArtMap, we believe that culture brings people together and enriches communities. Our platform is designed to help you discover cultural events near you, from art exhibitions and theater performances to music festivals and literary gatherings. By using location-based technology, we provide personalized recommendations so you never miss an event that matters to you.
            </p>
        </div>
    )
}

export default IntroAbout;
