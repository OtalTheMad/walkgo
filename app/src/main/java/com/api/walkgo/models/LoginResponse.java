package com.api.walkgo.models;

public class LoginResponse {
    private String token;

    public String GetToken() {
        return token;
    }

    public void SetToken(String token) {
        this.token = token;
    }
}