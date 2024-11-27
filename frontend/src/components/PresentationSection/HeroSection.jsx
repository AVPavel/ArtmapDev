import React from 'react';
import styles from './HeroSection.module.css'
import ImageComponent from '../ImageComponent/ImageComponent'
import ballet from '../../assets/images/HomePage/balletPhoto.jpeg'
import concert from '../../assets/images/HomePage/concert.jpeg'
import painting from '../../assets/images/HomePage/Paintings.jpeg'


const HeroSection = () => {
    return (
        <div className={styles.heroContainer}>
            <div className={styles.heroItem}>
                <ImageComponent
                    src={ballet}
                    alt='Ballet Photo'
                />
            </div>
            <div className={styles.heroItem}>
                <div className={styles.heroCenter}>
                    <h1>Evenimentul cultural perfect pentru tine!</h1>
                    <input type="text" placeholder="Caută un concert, o expoziție sau o piesă"/>
                </div>
                <ImageComponent
                    src={concert}
                    alt='Concert Photo'
                />
            </div>
            <div className={styles.heroItem}>
                <ImageComponent
                    src={painting}
                    alt='Painting Photo'
                />
            </div>
        </div>
    )
}

export default HeroSection;