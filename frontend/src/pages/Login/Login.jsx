import React, {useState} from 'react';
import styles from './Login.module.css';
import {Link} from "react-router-dom"; // Scoped CSS

const Login = () => {
    // State to manage form inputs
    const [credentials, setCredentials] = useState({
        username: '',
        password: '',
    });

    // State to manage feedback messages
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Handle input changes
    const handleChange = (e) => {
        const {name, value} = e.target;
        setCredentials({
            ...credentials,
            [name]: value,
        });
    };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            const response = await fetch('http://localhost:8080/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(credentials), // Send username and password
            });
            if (response.ok) {
                const data = await response.json();
                // Store JWT in localStorage
                localStorage.setItem('jwt', data.jwt);
                setSuccess('Login successful!');
                // Optionally, redirect to a protected route
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
            <div className={styles.loginContainer}>
                <h2>Login</h2>
                {error && <p className={styles.error}>{error}</p>}
                {success && <p className={styles.success}>{success}</p>}
                <form onSubmit={handleSubmit}>
                    <label>
                        Username:
                        <input
                            type="text"
                            name="username"
                            value={credentials.username}
                            onChange={handleChange}
                            required
                        />
                    </label>
                    <label>
                        Password:
                        <input
                            type="password"
                            name="password"
                            value={credentials.password}
                            onChange={handleChange}
                            required
                        />
                    </label>
                    <button type="submit">Login</button>
                </form>
                <p>Go back to Home Page: <Link to="/">Home</Link></p>
            </div>
        </div>
    );
};

export default Login;
