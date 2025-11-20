package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName(value = "id", alternate = {"id_usuario", "idUsuario"})
    private int id;

    @SerializedName(value = "usuario", alternate = {"nombreUsuario", "nombre"})
    private String usuario;

    public int GetId() {
        return id;
    }

    public String GetUsuario() {
        return usuario;
    }
}