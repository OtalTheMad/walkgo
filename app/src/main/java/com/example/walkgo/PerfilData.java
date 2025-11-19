package com.example.walkgo;

public class PerfilData {

    private String name; // Nombre real (editable)
    private String username; // Nombre de usuario (editable, requiere validación de disponibilidad)
    private String country; // País (de la tabla 'perfiles')
    private String dateOfBirth; // Fecha de nacimiento (de la tabla 'perfiles')
    private String biography; // Biografía (de la tabla 'perfiles')
    private String profilePhotoUrl; // URL de la foto de perfil (de la tabla 'perfiles' - campo 'foto')

    // Simulación de Estadísticas (Los datos reales vendrían de la tabla 'estadisticas')
    private double weeklyKilometers;
    private double totalKilometers;

    // Datos Sociales
    private int friendsCount; // Cantidad de amigos

    // Identificador para saber si es el perfil propio (true) o el perfil de un amigo (false)
    private boolean isOwnProfile;

    // Constructor vacío (Necesario para deserialización de JSON/API)
    public PerfilData() {}

    // Constructor completo para simulación o inicialización
    public PerfilData(String name, String username, String country, String dateOfBirth, String biography,
                       String profilePhotoUrl, double weeklyKilometers, double totalKilometers,
                       int friendsCount, boolean isOwnProfile) {
        this.name = name;
        this.username = username;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.biography = biography;
        this.profilePhotoUrl = profilePhotoUrl;
        this.weeklyKilometers = weeklyKilometers;
        this.totalKilometers = totalKilometers;
        this.friendsCount = friendsCount;
        this.isOwnProfile = isOwnProfile;
    }

    // Getters
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getCountry() { return country; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getBiography() { return biography; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public double getWeeklyKilometers() { return weeklyKilometers; }
    public double getTotalKilometers() { return totalKilometers; }
    public int getFriendsCount() { return friendsCount; }
    public boolean isOwnProfile() { return isOwnProfile; }

    // Setters (Para el modo de edición)
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setCountry(String country) { this.country = country; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setBiography(String biography) { this.biography = biography; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

}
