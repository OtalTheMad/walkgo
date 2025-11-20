package com.api.walkgo.models;

public class LoginRequest {
    private String username;
    private String password;

    public String GetUsername() {
        return username;
    }

    public void SetUsername(String username) {
        this.username = username;
    }

    public String GetPassword() {
        return password;
    }

    public void SetPassword(String password) {
        this.password = password;
    }
}