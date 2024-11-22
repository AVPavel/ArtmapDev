import React from 'react';
import styles from './Home.module.css'
import {Link} from "react-router-dom";
import Navbar from "../../components/Navbar/Navbar";

const Home = () =>{
    return (
        <div className={styles.container}>
            <Navbar/>
            <h1>Hello BIBI din nou</h1>
            <p>This is the home page</p>
            <nav>
                <Link to="/login">Login</Link> | <Link to="/register">Register</Link>
            </nav>
        </div>

    )
}

export default Home;