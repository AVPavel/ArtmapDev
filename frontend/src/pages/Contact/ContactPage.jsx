import React from "react";
import Navbar from "../../components/Navbar/Navbar/Navbar";
import Footer from "../../components/Footer/Footer";
import styles from "./ContactPage.module.css";
import PhoneIcon from  "../../assets/images/icons/phoneIcon.png";
import mapIcon from  "../../assets/images/icons/mapIcon.png";
import mailIcon from  "../../assets/images/icons/mailIcon.png";

const ContactPage = () => {
    return (
        <div className={styles.pageContainer}>
            <Navbar />
            <div className={styles.content}>
                <div className={styles.contactForm}>
                    <h1>Contact us</h1>
                    <p>Any questions or remarks? Don’t hesitate to ask!</p>
                    <form>
                        <input type="email" placeholder="Enter a valid email address" className={styles.inputField} />
                        <input type="text" placeholder="Enter your name" className={styles.inputField} />
                        <button type="submit" className={styles.registerButton}>Submit</button>
                    </form>
                </div>
                <div className={styles.contactInfo}>
                    <div className={styles.contactItem}>
                        <img src={PhoneIcon} alt="Phone" className={styles.contactIcon} />
                        <p>+40 0754 345 789</p>
                    </div>
                    <div className={styles.contactItem}>
                        <img src={mapIcon} alt="Location" className={styles.contactIcon} />
                        <p>Aleea Valea Viilor, M36, Sector 6, București</p>
                    </div>
                    <div className={styles.contactItem}>
                        <img src={mailIcon} alt="Email" className={styles.contactIcon} />
                        <p>artmap23@gmail.com</p>
                    </div>
                </div>
            </div>
            <Footer />
        </div>
    )
}

export default ContactPage;
