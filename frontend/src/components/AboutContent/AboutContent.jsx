import React from 'react';
import UpperHalfContent from "./UpperHalfContent";
import LowerHalfContent from "./LowerHalfContent";
import IntroAbout from "./IntroAbout";
import styles from './AboutContent.module.css';


const AboutContent = () => {
    return (
        <div className={styles.container}>
            <div className={styles.introAbout}>
                <IntroAbout/>
            </div>
            <div className={styles.upperHalf}>
                <UpperHalfContent />
            </div>
            <div className={styles.lowerHalf}>
                <LowerHalfContent />
            </div>
        </div>
    )
}

export default AboutContent;
