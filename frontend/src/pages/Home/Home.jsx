import React from "react";
import styles from "./Home.module.css";
import NavbarAndPresentation from "../../components/Navbar/NavbarAndPresentation/NavbarAndPresentation";
import MapSection from "../../components/MapSection/MapSection";
import ServiceCards from "../../components/ServiceCards/ServiceCards"
import Footer from "../../components/Footer/Footer";

const Home = () => {
    return (
        <div className={styles.container}>
            <NavbarAndPresentation/>
            <MapSection/>
            <ServiceCards/>
            <Footer/>
        </div>
    );
};

export default Home;
