import React from "react";
import styles from "./Navbar.module.css";
import {Link} from "react-router-dom";
import Logo from "../../assets/images/LOGO_artmap.svg";

const Navbar = () =>{
    return (
        <nav className={styles.navbar}>
            <div className={styles.logoContainer}>
                <Link to="/">
                    <img src={Logo} alt="logo" className={styles.logoImage}/>
                </Link>
            </div>
            <ul className={styles.navLinks}>
                <li>
                    <Link to="/">Home</Link>
                </li>
                <li>
                    <Link to="/Login">Login</Link>
                </li>
                <li>
                    <Link to="/Register">Register</Link>
                </li>
            </ul>
        </nav>
    )
}

export default Navbar;