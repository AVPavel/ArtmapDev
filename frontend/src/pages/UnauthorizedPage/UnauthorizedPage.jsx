import React from "react";
import Navbar from "../../components/Navbar/Navbar/Navbar";
import Footer from "../../components/Footer/Footer";
import styles from "./UnauthorizedPage.module.css";
import UnauthorizedPageIcon from '../../assets/images/icons/angry_unauthorized.png';

const UnauthorizedPage = () => {
    return (
        <div className={styles.pageContainer}>
            <Navbar />
            <div className={styles.content}>
                <div className={styles.messageBox}>
                    <h1>Unauthorized</h1>
                    <img src={UnauthorizedPageIcon} alt="angry Folder" className={styles.image} />
                    <p>You are not allowed to enter to this page, please excuse us for any incovinence.</p>
                </div>
            </div>
            <Footer />
        </div>
    )
}

export default UnauthorizedPage;
