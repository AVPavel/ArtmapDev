import React from "react";
import styles from "./Home.module.css";
import NavbarAndPresentation from "../../components/Navbar/NavbarAndPresentation/NavbarAndPresentation";
import MapSection from "../../components/MapSection/MapSection";
import ServiceCards from "../../components/ServiceCards/ServiceCards"
import Footer from "../../components/Footer/Footer";
import WhyArtmapSection from "../../components/WhyArtmapSection/WhyArtmapSection";
import Navbar from "../../components/Navbar/Navbar/Navbar";

const Home = () => {
    return (
        <div className={styles.container}>
            <NavbarAndPresentation/>
            {/*<Navbar/>*/}
            <MapSection/>
            <ServiceCards/>
            <WhyArtmapSection/>
            <Footer/>
        </div>
    );
};

export default Home;
