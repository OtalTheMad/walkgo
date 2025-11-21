package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName(value = "id", alternate = {"id_usuario", "idUsuario"})
    private int id;

    @SerializedName(value = "usuario", alternate = {"nombreUsuario", "nombre"})
    private String usuario;

    @SerializedName(value = "totalPasos", alternate = {"total_pasos"})
    private Integer totalPasos;

    @SerializedName(value = "totalDistanciaKm", alternate = {"total_distancia_km"})
    private Double totalDistanciaKm;

    public Integer GetTotalPasos() {
        return totalPasos;
    }

    public Double GetTotalDistanciaKm() {
        return totalDistanciaKm;
    }

    public int GetId() {
        return id;
    }

    public String GetUsuario() {
        return usuario;
    }

}