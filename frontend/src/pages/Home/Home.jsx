import React from "react";
import styles from "./Home.module.css";
import HeroSection from "../../components/PresentationSection/HeroSection";
import Navbar from "../../components/Navbar/Navbar";
import MapSection from "../../components/MapSection/MapSection";
import ServiceCards from "../../components/ServiceCards/ServiceCards"

const Home = () => {
  return (
    <div className={styles.container}>
      <Navbar />
      <HeroSection />
      <MapSection />
        <ServiceCards/>
    </div>
  );
};

export default Home;
