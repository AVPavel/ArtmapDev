import React, { useState } from 'react';
import styles from './Register.module.css';
import { Link } from 'react-router-dom';
import GoogleLogo from '../../assets/images/icons/Google_logo.png';

const Register = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
    });

    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/users/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: formData.username,
                    email: formData.email,
                    password: formData.password,
                }),
            });

            if (response.ok) {
                setSuccess('Registration successful! Please log in.');
                setFormData({
                    username: '',
                    email: '',
                    password: '',
                    confirmPassword: '',
                });
                setError('');
            } else {
                const errorData = await response.json();
                setError(errorData.message || 'Registration failed');
                setSuccess('');
            }
        } catch (err) {
            setError('Something went wrong. Please try again later.');
            setSuccess('');
        }
    };

    return (
        <div className={styles.registerPage}>
            <div className={styles.imagePlaceholder}></div>

            <div className={styles.registerContainer}>
                <h1 className={styles.registerTitle}>Create your account</h1>
                <Link to="/login" className={styles.loginLink}>Already have an account? Log in</Link>

                <button className={styles.googleButton}>
                    <img src={GoogleLogo} alt="Google Logo" className={styles.googleLogo} />
                    Sign up with Google
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
                            name="username"
                            placeholder="Username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className={styles.inputGroup}>
                        <input
                            type="email"
                            name="email"
                            placeholder="Email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className={styles.inputGroup}>
                        <input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className={styles.inputGroup}>
                        <input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm Password"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <button type="submit" className={styles.registerButton}>
                        Register
                    </button>
                    <Link to="/user-preferences">UserPref? Log in</Link>
                </form>

                {error && <div className={styles.error}>{error}</div>}
                {success && <div className={styles.success}>{success}</div>}
            </div>
        </div>
    );
};

export default Register;
