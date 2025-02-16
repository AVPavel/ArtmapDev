import React from 'react'
import styles from "../Navbar/Navbar.module.css"
import {Link} from "react-router-dom";
import Logo from "../../../assets/images/HomePage/wrg42.png";

const Navbar = () =>{
    return (
            <nav className={styles.navbar}>
                <div className={styles.navSection}>
                    <ul className={styles.navLinks}>
                        <li>
                            <Link to="/messages">Messages</Link>
                        </li>
                        <li>
                            <Link to="/recommendations">Recommendations</Link>
                        </li>
                        <li>
                            <Link to="/favorites">Favorites</Link>
                        </li>
                    </ul>
                </div>
                <div className={styles.logoContainer}>
                    <Link to="/">
                        <img src={Logo} alt="logo" className={styles.logoImage}/>
                    </Link>
                </div>
                <div className={styles.navSection}>
                    <ul className={styles.navLinks}>
                        <li>
                            <Link to="/about">About</Link>
                        </li>
                        <li>
                            <Link to="/contact">Contact</Link>
                        </li>
                        <li>
                            <Link to="/news">News</Link>
                        </li>
                        <li>
                            <Link to="/Login">
                                <button className={styles.signInButton}>Sign in</button>
                            </Link>
                        </li>
                    </ul>
                </div>
            </nav>
    )
}

export default Navbar;