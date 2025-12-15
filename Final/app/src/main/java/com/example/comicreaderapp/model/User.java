package com.example.comicreaderapp.model;

import com.google.gson.annotations.SerializedName;


public class User {

    @SerializedName("user_id")
    public int userId;

    @SerializedName("username")
    public String username;

    @SerializedName("email")
    public String email;

    @SerializedName("ImageURL")
    public String ImageURL;

    @SerializedName("status")
    public String status;

    @SerializedName("role_id")
    public String roleId;

    // ✅ Add token fields
    @SerializedName("session_token")
    public String sessionToken;

    @SerializedName("session_expiration")
    public String sessionExpiration;

    @SerializedName("last_login")
    public String lastLogin;

    @SerializedName("created_at")
    public String createdAt;

    // Constructor
    public User() {
    }

    public User(int userId, String username, String email, String ImageURL) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.ImageURL = ImageURL;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionExpiration() {
        return sessionExpiration;
    }

    public void setSessionExpiration(String sessionExpiration) {
        this.sessionExpiration = sessionExpiration;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
