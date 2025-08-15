import React, { useState, useEffect, useRef } from "react";
import styles from "./Navbar.module.css";
import { Link, useNavigate, useLocation } from "react-router-dom";
import Logo from "../../../assets/images/HomePage/wrg42.png";
import { jwtDecode } from 'jwt-decode';
import { FaUserCircle } from 'react-icons/fa';

const NavbarAndPresentation = () => {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [userRole, setUserRole] = useState(null); // Stores user role (e.g., 'ADMIN', 'USER')
    const [profileImageUrl, setProfileImageUrl] = useState(null); // NEW: State for profile image URL

    const navigate = useNavigate();
    const location = useLocation(); // Get the current location object

    const dropdownRef = useRef(null);

    useEffect(() => {
        const verifyAndSetUserRole = async () => {
            const token = localStorage.getItem("jwt");

            if (!token) {
                // If no token, user is not authenticated and has no role
                setIsAuthenticated(false);
                setUserRole(null);
                setProfileImageUrl(null); // Reset profile image on logout/no token
                return;
            }

            try {
                // Step 1: Verify token validity with the backend
                const verifyResponse = await fetch('http://localhost:8080/api/verifyJWT/isValid', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(token) // Send token as a string
                });

                if (verifyResponse.ok) {
                    setIsAuthenticated(true);

                    // MODIFICATION HERE: Simplify role verification based on username from JWT
                    const decodedToken = jwtDecode(token);
                    const username = decodedToken.sub; // Assume 'sub' contains the username

                    if (username === 'pavel' || username === 'admin') {
                        setUserRole('ADMIN');
                    } else {
                        setUserRole('USER');
                    }

                    // Example: Set a profile image based on username.
                    // In a real application, you would fetch this URL from the backend
                    // after user authentication, e.g., from the user profile object.
                    if (username === 'pavel') {
                        setProfileImageUrl('https://placehold.co/40x40/FF6347/ffffff?text=P'); // Placeholder image for 'pavel'
                    } else if (username === 'admin') {
                        setProfileImageUrl('https://placehold.co/40x40/00CED1/ffffff?text=A'); // Placeholder image for 'admin'
                    } else {
                        setProfileImageUrl(null); // Use fallback if no specific image
                    }

                } else {
                    // If token is invalid according to backend, remove it and reset states
                    localStorage.removeItem("jwt");
                    setIsAuthenticated(false);
                    setUserRole(null);
                    setProfileImageUrl(null); // Reset profile image
                }
            } catch (error) {
                console.error("Error verifying or decoding token:", error);
                // In case of error at any step, assume not authenticated
                setIsAuthenticated(false);
                setUserRole(null);
                setProfileImageUrl(null);
            }
        };

        verifyAndSetUserRole();
    }, [location.pathname]); // Dependency on location.pathname will force re-run on route changes

    // Effect for closing the dropdown when clicking outside
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

    // Logout function
    const handleLogout = () => {
        localStorage.removeItem("jwt"); // Remove token
        setIsAuthenticated(false); // Set unauthenticated state
        setIsDropdownOpen(false); // Close dropdown
        setUserRole(null); // Reset user role
        setProfileImageUrl(null); // Reset profile image on logout
        window.location.reload(); // Reload page to ensure full state reset
    };

    // Effect for closing mobile menu on outside click (if necessary)
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (isMobileMenuOpen && !e.target.closest(`.${styles.navbar}`)) {
                setIsMobileMenuOpen(false);
            }
        };

        // Add listener only if mobile menu is open
        if (isMobileMenuOpen) {
            document.addEventListener("click", handleClickOutside);
        }

        // Cleanup: remove listener on component unmount or menu close
        return () => {
            document.removeEventListener("click", handleClickOutside);
        };
    }, [isMobileMenuOpen]); // Dependency on isMobileMenuOpen

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
                                        {profileImageUrl ? (
                                            <img
                                                src={profileImageUrl}
                                                alt="Imagine Profil"
                                                className={styles.profileImage} // New class for profile image
                                                onError={(e) => { e.target.onerror = null; e.target.src="https://placehold.co/40x40/cccccc/000000?text=?" }} // Fallback on error
                                            />
                                        ) : (
                                            <FaUserCircle className={styles.fallbackProfileIcon} /> // Generic fallback icon
                                        )}
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
