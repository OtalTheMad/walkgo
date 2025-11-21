package com.api.walkgo.models;

public class ApiFinalizarRecorridoRequest {

    public Double distanciaSesionKm;
    public Integer pasosSesion;

    public ApiFinalizarRecorridoRequest(Double distanciaSesionKm, Integer pasosSesion) {
        this.distanciaSesionKm = distanciaSesionKm;
        this.pasosSesion = pasosSesion;
    }
}