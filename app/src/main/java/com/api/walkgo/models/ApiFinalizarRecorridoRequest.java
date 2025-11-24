package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class ApiFinalizarRecorridoRequest {

    @SerializedName("distanciaSesionKm")
    private Double distanciaSesionKm;

    @SerializedName("pasosSesion")
    private Integer pasosSesion;

    public ApiFinalizarRecorridoRequest(Double distanciaSesionKm, Integer pasosSesion) {
        this.distanciaSesionKm = distanciaSesionKm;
        this.pasosSesion = pasosSesion;
    }

    public Double GetDistanciaSesionKm() {
        return distanciaSesionKm;
    }

    public void SetDistanciaSesionKm(Double distanciaSesionKm) {
        this.distanciaSesionKm = distanciaSesionKm;
    }

    public Integer GetPasosSesion() {
        return pasosSesion;
    }

    public void SetPasosSesion(Integer pasosSesion) {
        this.pasosSesion = pasosSesion;
    }
}