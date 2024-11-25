import React from "react";
import styles from "./Home.module.css";
import HeroSection from "../../components/PresentationSection/HeroSection";
import Navbar from "../../components/Navbar/Navbar";
import MapSection from "../../components/MapSection/MapSection";

const Home = () => {
  return (
    <div className={styles.container}>
      <Navbar />
      <HeroSection />
      <MapSection />
    </div>
  );
};

export default Home;
