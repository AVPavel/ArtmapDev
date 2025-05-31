import React, { useEffect, useState } from 'react';
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ element }) => {
    const [isValidToken, setIsValidToken] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const verifyToken = async () => {
            const token = localStorage.getItem("jwt");

            if (!token) {
                setIsLoading(false);
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/api/verifyJWT/isValid', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(token) // Send as raw string
                });

                if (response.ok) {
                    setIsValidToken(true);
                }
            } catch (error) {
                console.error("Token verification failed:", error);
            } finally {
                setIsLoading(false);
            }
        };

        verifyToken();
    }, []);

    if (isLoading) {
        return <div>Loading...</div>;
    }

    return isValidToken ? element : <Navigate to="/unauthorized" replace />;
}

export default ProtectedRoute;