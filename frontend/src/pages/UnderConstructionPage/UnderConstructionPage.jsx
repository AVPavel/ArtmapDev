import React from "react";
import Navbar from "../../components/Navbar/Navbar/Navbar";
import Footer from "../../components/Footer/Footer";
import styles from "./UnderConstructionPage.module.css";
import UnderConstructionIcon from '../../assets/images/icons/sasd_NotReady.png';

const UnderConstructionPage = () => {
    return (
        <div className={styles.pageContainer}>
            <Navbar />
            <div className={styles.content}>
                <div className={styles.messageBox}>
                    <h1>Page is under construction...</h1>
                    <img src={UnderConstructionIcon} alt="Under Construction" className={styles.image} />
                    <p>Please excuse us for any inconvenience. If you need to get in touch with us, please use our contact platform.</p>
                </div>
            </div>
            <Footer />
        </div>
    )
}

export default UnderConstructionPage;