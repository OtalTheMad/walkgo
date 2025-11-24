package com.api.walkgo.models;

public class RegisterRequest {

    private String usuario;
    private String clave;

    public String GetUsuario() {
        return usuario;
    }

    public void SetUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String GetClave() {
        return clave;
    }

    public void SetClave(String clave) {
        this.clave = clave;
    }
}