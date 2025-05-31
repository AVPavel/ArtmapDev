import React, { useState } from 'react';
import styles from './Login.module.css';
import { Link } from "react-router-dom";

const Login = () => {
    const [credentials, setCredentials] = useState({
        username: '',
        password: '',
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        console.log(name, value);
        setCredentials({
            ...credentials,
            [name]: value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        console.log(credentials)
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
                    <button type="submit" className={styles.signInButton} onClick={handleSubmit}>
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