package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class Amigo {

    @SerializedName(value = "id", alternate = {"idAmigo", "id_amigo"})
    private int id;

    @SerializedName(value = "idUsuario", alternate = {"id_usuario"})
    private int idUsuario;

    @SerializedName(value = "idUsuarioAmigo", alternate = {"id_usuario_amigo"})
    private int idUsuarioAmigo;

    @SerializedName("estado")
    private String estado;

    public int GetId() {
        return id;
    }

    public int GetIdUsuario() {
        return idUsuario;
    }

    public void SetIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int GetIdUsuarioAmigo() {
        return idUsuarioAmigo;
    }

    public void SetIdUsuarioAmigo(int idUsuarioAmigo) {
        this.idUsuarioAmigo = idUsuarioAmigo;
    }

    public String GetEstado() {
        return estado;
    }

    public void SetEstado(String estado) {
        this.estado = estado;
    }
}