import React, { useState,useEffect } from "react";
import styles from "./Navbar.module.css";
import { Link } from "react-router-dom";
import Logo from "../../../assets/images/HomePage/wrg42.png";

const NavbarAndPresentation = () => {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
// Add this inside the component
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (isMobileMenuOpen && !e.target.closest(`.${styles.navbar}`)) {
                setIsMobileMenuOpen(false);
            }
        };

        document.addEventListener("click", handleClickOutside);
        return () => document.removeEventListener("click", handleClickOutside);
    }, [isMobileMenuOpen]);
    return (
        <div className={styles.navbarPresentation}>
            <nav className={styles.navbar}>
                {/* Mobile Menu Button */}
                <button
                    className={styles.mobileMenuButton}
                    onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                    aria-label="Toggle menu"
                >
                    {isMobileMenuOpen ? "✕" : "☰"}
                </button>

                {/* Left Nav Section */}
                <div className={styles.navSection}>
                    <ul className={`${styles.navLinks} ${isMobileMenuOpen ? styles.mobileOpen : ""}`}>
                        <li>
                            <Link to="/messages" onClick={() => setIsMobileMenuOpen(false)}>Messages</Link>
                        </li>
                        <li>
                            <Link to="/recommendations" onClick={() => setIsMobileMenuOpen(false)}>Recommendations</Link>
                        </li>
                        <li>
                            <Link to="/favorites" onClick={() => setIsMobileMenuOpen(false)}>Favorites</Link>
                        </li>
                    </ul>
                </div>

                {/* Logo */}
                <div className={styles.logoContainer}>
                    <Link to="/">
                        <img src={Logo} alt="logo" className={styles.logoImage}/>
                    </Link>
                </div>

                {/* Right Nav Section */}
                <div className={styles.navSection}>
                    <ul className={`${styles.navLinks} ${isMobileMenuOpen ? styles.mobileOpen : ""}`}>
                        <li>
                            <Link to="/about" onClick={() => setIsMobileMenuOpen(false)}>About</Link>
                        </li>
                        <li>
                            <Link to="/contact" onClick={() => setIsMobileMenuOpen(false)}>Contact</Link>
                        </li>
                        <li>
                            <Link to="/news" onClick={() => setIsMobileMenuOpen(false)}>News</Link>
                        </li>
                        <li>
                            <Link to="/Login" onClick={() => setIsMobileMenuOpen(false)}>
                                <button className={styles.signInButton}>Sign in</button>
                            </Link>
                        </li>
                    </ul>
                </div>
            </nav>
        </div>
    );
};

export default NavbarAndPresentation;