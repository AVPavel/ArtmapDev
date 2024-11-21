import React from 'react';
import styles from './Home.module.css'
import {Link} from "react-router-dom";

const Home = () =>{
    return (
        <div className={styles.container}>
            <h1>Hello world, main page</h1>
            <p>This is the home page</p>
            <nav>
                <Link to="/login">Login</Link> | <Link to="/register">Register</Link>
            </nav>
        </div>

    )
}

export default Home;