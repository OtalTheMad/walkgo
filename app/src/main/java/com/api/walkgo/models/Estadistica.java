package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class Estadistica {

    @SerializedName(value = "id_estadistica", alternate = {"idEstadistica"})
    private int idEstadistica;

    @SerializedName(value = "id_usuario", alternate = {"idUsuario"})
    private int idUsuario;

    @SerializedName(value = "km_recorrido", alternate = {"kmRecorrido"})
    private int kmRecorrido;

    @SerializedName(value = "calorias_quemadas", alternate = {"caloriasQuemadas"})
    private String caloriasQuemadas;

    @SerializedName("clasificacion")
    private String clasificacion;

    @SerializedName("estado")
    private String estado;

    public int GetIdEstadistica() {
        return idEstadistica;
    }

    public int GetIdUsuario() {
        return idUsuario;
    }

    public int GetKmRecorrido() {
        return kmRecorrido;
    }

    public String GetCaloriasQuemadas() {
        return caloriasQuemadas;
    }

    public String GetClasificacion() {
        return clasificacion;
    }

    public String GetEstado() {
        return estado;
    }
}