import React, {useState, useEffect, useRef} from "react";
import styles from "./Navbar.module.css";
import {Link, useNavigate} from "react-router-dom";
import Logo from "../../../assets/images/HomePage/wrg42.png";
import { jwtDecode } from 'jwt-decode';
const NavbarAndPresentation = () => {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [userRole, setUserRole] = useState(null);
    const navigate = useNavigate();

    const dropdownRef = useRef(null);
    useEffect(() => {
        const verifyAndDecodeToken = async () => {
            const token = localStorage.getItem("jwt");
            if (!token) {
                setUserRole(null);
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/api/verifyJWT/isValid', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(token)
                });

                if (response.ok) {
                    setIsAuthenticated(true);
                    const decodedToken = jwtDecode(token);
                    setUserRole(decodedToken.role);
                } else {
                   localStorage.removeItem("jwt");
                    setIsAuthenticated(false);
                    setUserRole(null);
                }
            } catch (error) {
                console.error("Eroare la verificarea sau decodificarea token-ului:", error);
                setIsAuthenticated(false);
                setUserRole(null);
            }
        };
        verifyAndDecodeToken();
    }, []);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsDropdownOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const handleLogout = () => {
        localStorage.removeItem("jwt");
        setIsAuthenticated(false);
        setIsDropdownOpen(false);
        setUserRole(null);
        window.location.reload();
    };

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

                <div className={styles.logoContainer}>
                    <Link to="/">
                        <img src={Logo} alt="logo" className={styles.logoImage}/>
                    </Link>
                </div>

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
                        <li ref={dropdownRef} className={styles.profileMenuContainer}>
                            {isAuthenticated ? (
                                <>
                                    <div
                                        className={styles.profileIcon}
                                        onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                    >
                                    </div>
                                    {isDropdownOpen && (
                                        <div className={styles.profileDropdown}>
                                            {userRole === 'ADMIN' && (
                                                <Link to="/add-event" className={styles.dropdownItem} onClick={() => setIsDropdownOpen(false)}>
                                                    Adaugă eveniment
                                                </Link>
                                            )}
                                            <div className={styles.dropdownItem} onClick={handleLogout}>
                                                Logout
                                            </div>
                                        </div>
                                    )}
                                </>
                            ) : (
                                <Link to="/Login" onClick={() => setIsMobileMenuOpen(false)}>
                                    <button className={styles.signInButton}>Sign in</button>
                                </Link>
                            )}
                        </li>
                    </ul>
                </div>
            </nav>
        </div>
    );
};

export default NavbarAndPresentation;