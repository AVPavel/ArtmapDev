import React from 'react';
import Footer from "../../components/Footer/Footer";
import Navbar from "../../components/Navbar/Navbar/Navbar";
import AboutContent from "../../components/AboutContent/AboutContent";
import styles from "./About.module.css";


const About = () => {
    return (
        <div>
            <Navbar/>
            <AboutContent/>
            <Footer/>
        </div>
    )
}

export default About;