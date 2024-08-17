package org.teamy.backend.security.model;

public class Token {
    private String accessToken;//signed JWT
    private String refreshTokenId;//id related to JWT(to find refresh token)

    // accessors omitted, for brevity

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshTokenId() {
        return refreshTokenId;
    }

    public void setRefreshTokenId(String refreshTokenId) {
        this.refreshTokenId = refreshTokenId;
    }
}