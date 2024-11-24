import React from 'react';
import styles from './Home.module.css'
import HeroSection from '../../components/PresentationSection/HeroSection'
import Navbar from "../../components/Navbar/Navbar";

const Home = () =>{
    return (
        <div className={styles.container}>
                <Navbar/>
                <HeroSection/>
        </div>

    )
}

export default Home;