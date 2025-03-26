import React from 'react';
import styles from './UpperHalfContent.module.css';

const UpperHalfContent = () => {
    return (
        <div className={styles.content}>
            <p className={styles.paragraphSection}>
                ‘’I am a final-year Applied Computer Science student at the Faculty of Mathematics and   Science in Brașov. This application was developed as part of my Bachelor's thesis, built with Spring Boot, React, and MySQL, it focuses on delivering a secure, scalable, and efficient solution while showcasing my passion for software engineering, system design and... Concerts!’’

                Andrei Pavel, Founder of ArtMap
            </p>
            <div className={styles.imageSection}>
                <img alt="ceva poza cu mine"/>
            </div>
        </div>
    )
}


export default UpperHalfContent;