import React, { useState } from 'react';
import styles from './ForgotPassword.module.css';
import { Link } from 'react-router-dom';

const ForgotPassword = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === 'email') setEmail(value);
        if (name === 'password') setPassword(value);
        if (name === 'confirmPassword') setConfirmPassword(value);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/users/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email,
                    password,
                }),
            });

            if (response.ok) {
                setSuccess('Password reset successful! Please log in.');
                setEmail('');
                setPassword('');
                setConfirmPassword('');
                setError('');
            } else {
                const errorData = await response.json();
                setError(errorData.message || 'Password reset failed');
                setSuccess('');
            }
        } catch (err) {
            setError('Something went wrong. Please try again later.');
            setSuccess('');
        }
    };

    return (
        <div className={styles.forgotPasswordPage}>
            <div className={styles.imagePlaceholder}></div>

            <div className={styles.forgotPasswordContainer}>
                <h1 className={styles.forgotPasswordTitle}>Reset password</h1>

                {error && <div className={styles.error}>{error}</div>}
                {success && <div className={styles.success}>{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className={styles.inputGroup}>
                        <input
                            type="email"
                            name="email"
                            placeholder="Email"
                            value={email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className={styles.inputGroup}>
                        <input
                            type="password"
                            name="password"
                            placeholder="New password"
                            value={password}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className={styles.inputGroup}>
                        <input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm new password"
                            value={confirmPassword}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <button type="submit" className={styles.resetButton}>
                        Reset
                    </button>
                </form>

                <div className={styles.options}>
                    <input type="checkbox" id="rememberMe" />
                    <label htmlFor="rememberMe">Remember me</label>
                </div>
            </div>
        </div>
    );
};

export default ForgotPassword;
