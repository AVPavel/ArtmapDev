import React from 'react';
import styles from "./Footer.module.css";
import Instagram from '../../assets/images/HomePage/instagram.png'
import Facebook from '../../assets/images/HomePage/Facebook.png'
import Twitter from '../../assets/images/HomePage/twitter.png'

const Footer = () => {
    return (
        <div className={styles.footerContainer}>
            <div className={styles.content}>
                <div className={styles.leftContent}>
                    <h2>Subscribe to our newsletter</h2>
                    <p id="descriptionP">Be notified every time a new event is going to be around your area</p>
                    <div className={styles.subscribeForm}>
                        <input
                            type="email"
                            placeholder="Enter your email"
                            className={styles.emailInput}
                        />
                        <button className={styles.subscribeButton}>
                            Subscribe
                        </button>
                    </div>
                </div>
                <div className={styles.rightContent}>
                    <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
                        <img src={Facebook} alt="Facebook" />
                    </a>
                    <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
                        <img src={Twitter} alt="Twitter" />
                    </a>
                    <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
                        <img src={Instagram} alt="Instagram" />
                    </a>
                </div>
            </div>
            <hr className={styles.divider} />
            <div className={styles.bottomLinks}>
                <a href="/sitemap">Site Map</a>
                <a href="/terms">Terms Of Use</a>
                <a href="/privacy">Privacy Policy</a>
                <a href="/cookies">Cookie Policy</a>
            </div>
        </div>
    );
};

export default Footer;