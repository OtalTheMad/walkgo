package com.api.walkgo.models;

public class PerfilUpdateRequest {

    private String pais;
    private String biografia;
    private String fechaNac;
    private String fotoBase64;
    private String estado;

    public String GetPais() {
        return pais;
    }

    public void SetPais(String pais) {
        this.pais = pais;
    }

    public String GetBiografia() {
        return biografia;
    }

    public void SetBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String GetFechaNac() {
        return fechaNac;
    }

    public void SetFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String GetFotoBase64() {
        return fotoBase64;
    }

    public void SetFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }

    public String GetEstado() {
        return estado;
    }

    public void SetEstado(String estado) {
        this.estado = estado;
    }
}