package com.api.walkgo.models;

public class ApiUpdateAmigo {
    public Integer idUsuario;
    public Integer idUsuarioAmigo;
    public String estado;

    public ApiUpdateAmigo(Integer idUsuario, Integer idUsuarioAmigo, String estado) {
        this.idUsuario = idUsuario;
        this.idUsuarioAmigo = idUsuarioAmigo;
        this.estado = estado;
    }
}