import React from 'react';
import styles from './LowerHalfContent.module.css';
import ComputerPhoto from "../../assets/images/icons/computerPhotoAbout.png"
const LowerHalfContent = () => {
    return (
        <div className={styles.content}>
            <div className={styles.imageSection}>
                <img src={ComputerPhoto} alt="ArtMap Benefits" />
            </div>
            <div className={styles.benefitsSection}>
                <h2>Benefits of Choosing ArtMap</h2>
                <ul>
                    <li>Personalized Event Discovery – Get recommendations tailored to your location and interests.</li>
                    <li>Comprehensive Cultural Listings – Explore a wide range of events, from art exhibitions to live performances and festivals.</li>
                    <li>Real-Time Updates – Stay informed about upcoming and last-minute cultural happenings.</li>
                    <li>Seamless Navigation – Integrated maps help you locate events and plan your route effortlessly.</li>
                    <li>Community Engagement – Connect with like-minded culture enthusiasts and support local artists and organizations.</li>
                </ul>
            </div>
        </div>
    )
}

export default LowerHalfContent;
