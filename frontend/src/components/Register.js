import React, { useState } from 'react';
import styles from '../styles/Register.module.css';
/* "username": "organizerUser",
  "password": "securePassword123",
  "email": "organizer@example.com",
  "role": "ORGANIZER",
  "preferredBudget": 500.00*/
const Register = () => {
    const[formData, setFormData] = useState({
        username: '',
        password:'',
        email:'',
        role:'',
        preferredBudget: null
    });

    //states for error and success messages
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')

    return(
        <div>
            <h2>Register</h2>
            <form>
                <label>Username:
                    <input type='text' name='Username' required>
                    </input>
                </label>
                <label>Email:
                    <input type='text' name='Email' required>
                    </input>
                </label>
                <label>Password:
                    <input type='text' name='Password' required>
                    </input>
                </label>
                <label>PreferredBudget:
                    <input type='text' name='PreferredBudget' required>
                    </input>
                </label>
                <button type='submit'>submit</button>
            </form>
        </div>
    )
}

export default Register;