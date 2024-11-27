import React from "react";
import styles from "./Navbar.module.css";
import { Link } from "react-router-dom";
import Logo from "../../assets/images/HomePage/LOGO_artmap.svg";

const Navbar = () => {
    return (
        <nav className={styles.navbar}>
            <div className={styles.navSection}>
                <ul className={styles.navLinks}>
                    <li>
                        <Link to="/">Messages</Link>
                    </li>
                    <li>
                        <Link to="/">Recommendations</Link>
                    </li>
                    <li>
                        <Link to="/">Favorites</Link>
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
                        <Link to="/">About</Link>
                    </li>
                    <li>
                        <Link to="/Contact">Contact</Link> {/* Updated path */}
                    </li>
                    <li>
                        <Link to="/">News</Link>
                    </li>
                    <li>
                        <Link to="/Login">
                            <button className={styles.signInButton}>Sign in</button>
                        </Link> {/* Capitalized for consistency */}
                    </li>
                </ul>
            </div>
        </nav>

    );
};

export default Navbar;
