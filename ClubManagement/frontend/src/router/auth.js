import React, { createContext, useState, useContext, useMemo } from 'react';
import {loginApi, logoutApi} from "./api";


const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [authenticating, setAuthenticating] = useState(false);
    const [authenticationError, setAuthenticationError] = useState(null);

    const extractUserFromToken = (token) => JSON.parse(atob(token.split('.')[1]));
    const login = async (username, password) => {
        setAuthenticating(true);
        setAuthenticationError(null);

        try {
            const token = await loginApi(username, password);
            setUser(extractUserFromToken(token.accessToken));
        } catch (error) {
            setAuthenticationError(error.message);
        } finally {
            setAuthenticating(false);
        }
    };

    const logout = async (username) => {
        await logoutApi(username);
        setUser(null);
    };

    const value = useMemo(() => ({
        user,
        login,
        logout,
        authenticating,
        authenticationError,
    }), [user, authenticating, authenticationError]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}




