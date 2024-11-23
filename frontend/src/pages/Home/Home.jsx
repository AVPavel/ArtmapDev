import React from 'react';
import styles from './Home.module.css'
import {Link} from "react-router-dom";
import Navbar from "../../components/Navbar/Navbar";

const Home = () =>{
    return (
        <div className={styles.container}>
            <div>
                <Navbar/>
            </div>
            <div>

            </div>
        </div>

    )
}

export default Home;