package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class Recorrido {

    @SerializedName("id")
    private Integer id;

    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("distanciaKm")
    private Double distanciaKm;

    @SerializedName("pasos")
    private Integer pasos;

    @SerializedName("fecha")
    private String fecha;

    public Integer GetId() {
        return id;
    }

    public Integer GetIdUsuario() {
        return idUsuario;
    }

    public void SetIdUsuario(Integer _idUsuario) {
        idUsuario = _idUsuario;
    }

    public Double GetDistanciaKm() {
        return distanciaKm;
    }

    public void SetDistanciaKm(Double _distanciaKm) {
        distanciaKm = _distanciaKm;
    }

    public Integer GetPasos() {
        return pasos;
    }

    public void SetPasos(Integer _pasos) {
        pasos = _pasos;
    }

    public String GetFecha() {
        return fecha;
    }

    public void SetFecha(String _fecha) {
        fecha = _fecha;
    }
}