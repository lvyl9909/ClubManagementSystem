import React, { createContext, useState, useContext, useMemo } from 'react';

// Create a context for authentication
const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
    const path = process.env.REACT_APP_API_BASE_URL
    const [user, setUser] = useState(null);
    const [authenticating, setAuthenticating] = useState(false);
    const [authenticationError, setAuthenticationError] = useState(null);


    const login = async (username, password) => {
        setAuthenticating(true);
        setAuthenticationError(null);

        try {
            const res = await fetch('${path}/auth/token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            if (!res.ok) {
                throw new Error('Login failed');
            }

            const token = await res.json();
            setTokenInStorage(token);
            setUser(extractUserFromToken(token.accessToken));
        } catch (error) {
            setAuthenticationError(error.message);
        } finally {
            setAuthenticating(false);
        }
    };


    const logout = async () => {
        await fetch('${path}/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        localStorage.removeItem('accessToken');
        localStorage.removeItem('tokenType');
        setUser(null);
    };

    const doCall = async (path, method = 'GET', data = null, signal = null) => {
        const headers = {
            'Content-Type': 'application/json',
            Accept: 'application/json',
        };

        const accessToken = localStorage.getItem('accessToken');
        if (accessToken) {
            headers.Authorization = `${localStorage.getItem('tokenType')} ${accessToken}`;
        }

        let body;
        if (data) {
            body = JSON.stringify(data);
        }

        const res = await fetch(path, {
            method,
            headers,
            body,
            signal,
        });

        if (res.status === 401 && accessToken) {
            await refreshToken(accessToken, signal);
            return doCall(path, method, data, signal); // Retry the original request
        }

        if (res.status > 299) {
            throw new Error(`Error: ${res.status} ${res.statusText}`);
        }

        return res.json();
    };


    const refreshToken = async (accessToken, signal) => {
        const res = await fetch('${path}/auth/token', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                Accept: 'application/json',
            },
            body: JSON.stringify({ accessToken }),
            signal,
        });

        if (!res.ok) {
            throw new Error('Token refresh failed');
        }

        const token = await res.json();
        setTokenInStorage(token);
        setUser(extractUserFromToken(token.accessToken));
    };

    const setTokenInStorage = (token) => {
        localStorage.setItem('accessToken', token.accessToken);
        localStorage.setItem('tokenType', token.type);
    };

    const extractUserFromToken = (token) => {
        return JSON.parse(atob(token.split('.')[1]));
    };

    const value = useMemo(() => ({
        user,
        login,
        logout,
        authenticating,
        authenticationError,
        doCall,
    }), [user, authenticating, authenticationError]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}
