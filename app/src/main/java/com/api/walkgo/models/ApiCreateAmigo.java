package com.api.walkgo.models;

public class ApiCreateAmigo {
    public Integer idUsuario;
    public Integer idUsuarioAmigo;

    public ApiCreateAmigo(Integer idUsuario, Integer idUsuarioAmigo) {
        this.idUsuario = idUsuario;
        this.idUsuarioAmigo = idUsuarioAmigo;
    }
}