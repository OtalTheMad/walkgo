package com.api.walkgo.models;

import com.google.gson.annotations.SerializedName;

public class Perfil {

    @SerializedName(value = "id_perfil", alternate = {"idPerfil"})
    private int idPerfil;

    @SerializedName(value = "id_usuario", alternate = {"idUsuario"})
    private int idUsuario;

    @SerializedName("pais")
    private String pais;

    @SerializedName("biografia")
    private String biografia;

    @SerializedName(value = "fecha_nac", alternate = {"fechaNac"})
    private String fechaNac;

    @SerializedName("estado")
    private String estado;

    @SerializedName("foto")
    private String foto;

    public int GetIdPerfil() {
        return idPerfil;
    }

    public int GetIdUsuario() {
        return idUsuario;
    }

    public String GetPais() {
        return pais;
    }

    public String GetBiografia() {
        return biografia;
    }

    public String GetFechaNac() {
        return fechaNac;
    }

    public String GetEstado() {
        return estado;
    }

    public String GetFoto() {
        return foto;
    }
}