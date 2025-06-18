import React, { useState, useEffect, useRef } from "react";
import styles from "./Navbar.module.css";
import { Link, useNavigate, useLocation } from "react-router-dom";
import Logo from "../../../assets/images/HomePage/wrg42.png";
import { jwtDecode } from 'jwt-decode';

const NavbarAndPresentation = () => {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [userRole, setUserRole] = useState(null); // Va stoca rolul utilizatorului (ex: 'ADMIN', 'USER')

    const navigate = useNavigate();
    const location = useLocation(); // Obține obiectul de locație curent

    const dropdownRef = useRef(null);

    // Efect pentru verificarea și setarea rolului utilizatorului
    // Va rula la montarea componentei și ori de câte ori calea URL-ului se schimbă
    useEffect(() => {
        const verifyAndSetUserRole = async () => {
            const token = localStorage.getItem("jwt");

            if (!token) {
                // Dacă nu există token, utilizatorul nu este autentificat și nu are un rol
                setIsAuthenticated(false);
                setUserRole(null);
                return;
            }

            try {
                // Pasul 1: Verifică validitatea token-ului cu backend-ul
                const verifyResponse = await fetch('http://localhost:8080/api/verifyJWT/isValid', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(token) // Trimite token-ul ca șir de caractere
                });

                if (verifyResponse.ok) {
                    setIsAuthenticated(true);

                    // MODIFICARE AICI: Simplifică verificarea rolului bazată pe username-ul din JWT
                    const decodedToken = jwtDecode(token);
                    const username = decodedToken.sub; // Asumăm că 'sub' conține username-ul

                    if (username === 'pavel' || username === 'admin') {
                        setUserRole('ADMIN');
                    } else {
                        setUserRole('USER');
                    }

                } else {
                    // Dacă token-ul nu este valid conform backend-ului, șterge-l și resetează stările
                    localStorage.removeItem("jwt");
                    setIsAuthenticated(false);
                    setUserRole(null);
                }
            } catch (error) {
                console.error("Eroare la verificarea token-ului sau la decodificare:", error);
                // În caz de eroare la orice pas, asumă că nu este autentificat
                setIsAuthenticated(false);
                setUserRole(null);
            }
        };

        verifyAndSetUserRole();
    }, [location.pathname]); // Dependența pe location.pathname va forța re-rularea la schimbările de rută

    // Efect pentru închiderea dropdown-ului la clic în afara acestuia
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

    // Funcție pentru delogare
    const handleLogout = () => {
        localStorage.removeItem("jwt"); // Șterge token-ul
        setIsAuthenticated(false); // Setează starea de neautentificat
        setIsDropdownOpen(false); // Închide dropdown-ul
        setUserRole(null); // Resetează rolul utilizatorului
        window.location.reload(); // Reîmprospătează pagina pentru a asigura resetarea completă a stării
    };

    // Efect pentru închiderea meniului mobil la clic în afara acestuia (dacă este necesar)
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (isMobileMenuOpen && !e.target.closest(`.${styles.navbar}`)) {
                setIsMobileMenuOpen(false);
            }
        };

        // Adaugă listener-ul doar dacă meniul mobil este deschis
        if (isMobileMenuOpen) {
            document.addEventListener("click", handleClickOutside);
        }

        // Curățare: elimină listener-ul la demontarea componentei sau la închiderea meniului
        return () => {
            document.removeEventListener("click", handleClickOutside);
        };
    }, [isMobileMenuOpen]); // Dependența pe isMobileMenuOpen

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
                                        {/* Poți adăuga o iconiță de utilizator aici, ex: <FaUserCircle /> */}
                                    </div>
                                    {isDropdownOpen && (
                                        <div className={styles.profileDropdown}>
                                            {/* Afișează butonul "Adaugă eveniment" doar dacă rolul este 'ADMIN' */}
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
