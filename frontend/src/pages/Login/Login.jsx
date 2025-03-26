import React, { useState } from 'react';
import styles from './Login.module.css';
import { Link } from "react-router-dom";
import GoogleLogo from '../../assets/images/icons/Google_logo.png';


const Login = () => {
    const [credentials, setCredentials] = useState({
        emailOrPhone: '',
        password: '',
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

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

        try {
            const response = await fetch('http://192.168.1.132:8080/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(credentials),
            });
            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('jwt', data.jwt);
                setSuccess('Login successful!');
            } else {
                const errorData = await response.text();
                setError(errorData || 'Login failed');
            }
        } catch (err) {
            setError('Something went wrong. Please try again.');
        }
    };

    return (
        <div className={styles.loginPage}>
            <div className={styles.imagePlaceholder}></div>

            <div className={styles.loginContainer}>
                <h1 className={styles.LoginTitle}>Log in to your account</h1>
                <Link to="/register" className={styles.createAccount}>Are you new here? Create account</Link>

                <button className={styles.googleButton}>
                    <img src={GoogleLogo} alt="Google Logo" className={styles.googleLogo} />
                    Log in with Google
                </button>

                <div className={styles.separator}>
                    <span className={styles.line}></span>
                    <span className={styles.orText}>or</span>
                    <span className={styles.line}></span>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className={styles.inputGroup}>
                        <input
                            type="text"
                            name="emailOrPhone"
                            placeholder="Email or phone"
                            value={credentials.emailOrPhone}
                            onChange={handleChange}
                            required
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
                        />
                    </div>
                    <button type="submit" className={styles.signInButton}>
                        Sign in
                    </button>
                    <div className={styles.options}>
                        <input type="checkbox" id="rememberMe" />
                        <label htmlFor="rememberMe">Remember me</label>
                        <Link to="/forgot-password" className={styles.forgotPassword}>Forgot password?</Link>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;
