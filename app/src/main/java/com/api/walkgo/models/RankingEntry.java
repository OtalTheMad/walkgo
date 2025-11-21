package com.api.walkgo.models;

public class RankingEntry {

    private Integer userId;
    private String usuario;
    private Integer posicion;
    private Integer rangoSemanal;
    private Double totalDistanciaKm;
    private String avatar;

    public Integer GetUserId() {
        return userId;
    }

    public String GetUsuario() {
        return usuario;
    }

    public Integer GetPosicion() {
        return posicion;
    }

    public Integer GetRangoSemanal() {
        return rangoSemanal;
    }

    public Double GetTotalDistanciaKm() {
        return totalDistanciaKm;
    }

    public String GetAvatar() {
        return avatar;
    }
}