import React, { useState } from 'react';
import styles from './Login.module.css';
import Logo from "../../assets/images/HomePage/LOGO_artmap.svg";
import { Link, useNavigate } from "react-router-dom"; // Import useNavigate
import { FaSpinner } from 'react-icons/fa'; // Import spinner icon

const Login = () => {
    const [credentials, setCredentials] = useState({
        username: '',
        password: '',
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [isLoading, setIsLoading] = useState(false); // New loading state
    const [currentStep, setCurrentStep] = useState(''); // New step message state
    const navigate = useNavigate(); // Initialize useNavigate

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCredentials({
            ...credentials,
            [name]: value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setIsLoading(true); // Start loading
        setCurrentStep('Logging you in...'); // Set initial step message

        try {
            const response = await fetch('http://localhost:8080/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(credentials),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('jwt', data.jwt); // Store the JWT token

                setSuccess('Login successful! Redirecting to home page...');
                setCurrentStep('Login successful!'); // Update step for success
                setTimeout(() => navigate('/'), 1500); // Redirect to home page after 1.5 seconds
            } else {
                const errorData = await response.json(); // Assume JSON error response
                setError(errorData.message || 'Login failed. Please check your credentials.');
            }
        } catch (err) {
            setError(err.message || 'Something went wrong. Please try again later.');
        } finally {
            setIsLoading(false); // End loading
        }
    };

    return (
        <div className={styles.loginPage}>
            <div className={styles.imagePlaceholder}></div>

            <div className={styles.loginContainer}>
                <div className={styles.logoContainer}>
                    <img src={Logo} alt="Artmap Logo" className={styles.logo} />
                </div>

                <h1 className={styles.LoginTitle}>Log in to your account</h1>
                <Link to="/register" className={styles.createAccount}>Are you new here? Create account</Link>

                <div className={styles.separator}>
                    <span className={styles.line}></span>
                    <span className={styles.orText}>or</span>
                    <span className={styles.line}></span>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className={styles.inputGroup}>
                        <input
                            type="text"
                            name="username"
                            placeholder="Username"
                            value={credentials.username}
                            onChange={handleChange}
                            required
                            disabled={isLoading} // Disable input when loading
                        />
                    </div>

                    <div className={styles.inputGroup}>
                        <input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={credentials.password}
                            onChange={handleChange}
                            required
                            disabled={isLoading} // Disable input when loading
                        />
                    </div>

                    <button
                        type="submit"
                        className={styles.signInButton}
                        disabled={isLoading} // Disable button when loading
                    >
                        {isLoading ? (
                            <div className={styles.loadingContainer}>
                                <FaSpinner className={styles.spinner} />
                                {currentStep}
                            </div>
                        ) : 'Sign in'}
                    </button>

                    <div className={styles.options}>
                        <input type="checkbox" id="rememberMe" />
                        <label htmlFor="rememberMe">Remember me</label>
                        <Link to="/forgot-password" className={styles.forgotPassword}>Forgot password?</Link>
                    </div>
                </form>

                {isLoading && (
                    <div className={styles.progressContainer}>
                        <div className={styles.progressText}>{currentStep}</div>
                    </div>
                )}

                {/* Error and Success messages */}
                {error && <div className={styles.error}>{error}</div>}
                {success && <div className={styles.success}>{success}</div>}
            </div>
        </div>
    );
};

export default Login;
