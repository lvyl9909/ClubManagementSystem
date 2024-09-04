import React, { createContext, useState, useContext, useMemo } from 'react';
import {loginApi, logoutApi} from "./api";


const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [authenticating, setAuthenticating] = useState(false);
    const [authenticationError, setAuthenticationError] = useState(null);


    const login = async (username, password) => {
        setAuthenticating(true);
        setAuthenticationError(null);

        try {
            const token = await loginApi(username, password);
            // console.log("API response:", res);
            //const token = await res.json();
            console.log("Raw token:", token);
            const extractUserFromToken = (token) => JSON.parse(atob(token.split('.')[1]));
            const userDetails = extractUserFromToken(token.accessToken); // Extract user details from token
            console.log("Decoded user details:", userDetails);
            setUser(userDetails);
        } catch (error) {
            setAuthenticationError(error.message);
            throw error;
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

    // function extractUserFromToken(token) {
    //     const decodedToken = JSON.parse(atob(token.split('.')[1]));
    //     console.log("decodedToken",decodedToken)
    //     return{
    //         username:decodedToken.sub,
    //         name:decodedToken.name
    //     }
    // }

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}




