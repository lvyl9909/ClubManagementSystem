package org.teamy.backend.security.model;

public class RefreshToken {
    private String id;
    private String tokenId;
    private String username;


    // accessors omitted, for brevity


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}