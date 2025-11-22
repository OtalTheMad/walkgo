package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class ApiFinalizarRecorridoRequest {

    @SerializedName("distanciaSesionKm")
    private Double distanciaSesionKm;

    @SerializedName("pasosSesion")
    private Integer pasosSesion;

    public ApiFinalizarRecorridoRequest(Double _distanciaSesionKm, Integer _pasosSesion) {
        this.distanciaSesionKm = _distanciaSesionKm;
        this.pasosSesion = _pasosSesion;
    }

    public Double GetDistanciaSesionKm() {
        return distanciaSesionKm;
    }

    public Integer GetPasosSesion() {
        return pasosSesion;
    }
}