import React from 'react';
import styles from './UpperHalfContent.module.css';
import MePhoto from "../../assets/images/icons/mePhotoAbout.png"

const UpperHalfContent = () => {
    return (
        <div className={styles.content}>
            <div className={styles.paragraphSection}>
                <p>
                    ‘’I am a final-year Applied Computer Science student at the Faculty of Mathematics and Science in Brașov. This application was developed as part of my Bachelor's thesis, built with Spring Boot, React, and MySQL, it focuses on delivering a secure, scalable, and efficient solution while showcasing my passion for software engineering, system design and... Concerts!’’
                    <br />
                    Andrei Pavel, Founder of ArtMap
                </p>
            </div>
            <div className={styles.imageSection}>
                <img src={MePhoto} alt="Andrei Pavel" />
            </div>
        </div>
    )
}

export default UpperHalfContent;
