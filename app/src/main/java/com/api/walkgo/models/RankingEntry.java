package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class RankingEntry {

    @SerializedName(value = "posicion", alternate = {"position"})
    private Integer posicion;

    @SerializedName(value = "idUsuario", alternate = {"id_usuario"})
    private Integer idUsuario;

    @SerializedName(value = "usuario", alternate = {"username"})
    private String usuario;

    @SerializedName(value = "totalDistanciaKm", alternate = {"total_distancia_km"})
    private Double totalDistanciaKm;

    public Integer GetPosicion() {
        return posicion;
    }

    public Integer GetIdUsuario() {
        return idUsuario;
    }

    public String GetUsuario() {
        return usuario;
    }

    public Double GetTotalDistanciaKm() {
        return totalDistanciaKm;
    }
}