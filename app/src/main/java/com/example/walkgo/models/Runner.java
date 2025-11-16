package com.example.walkgo.models;

public class Runner {

    private int position;
    private String name;
    private String username; // Útil para la validación y navegación al perfil
    private float kilometers;
    private String weeklyChange; // Ejemplo: "↑ 5", "↓ 2", "= 0"
    private String profileImageUrl;
    private boolean isCurrentUser;
    private boolean isFriend;

    // Constructor completo
    public Runner(int position, String name, String username, float kilometers, String weeklyChange, String profileImageUrl, boolean isCurrentUser, boolean isFriend) {
        this.position = position;
        this.name = name;
        this.username = username;
        this.kilometers = kilometers;
        this.weeklyChange = weeklyChange;
        this.profileImageUrl = profileImageUrl;
        this.isCurrentUser = isCurrentUser;
        this.isFriend = isFriend;
    }

    // Getters
    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public float getKilometers() {
        return kilometers;
    }

    public String getWeeklyChange() {
        return weeklyChange;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public boolean isFriend() {
        return isFriend;
    }

    // Setters (Si fuera necesario actualizar el estado de amistad o la posición)
    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
