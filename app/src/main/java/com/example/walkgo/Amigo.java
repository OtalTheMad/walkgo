package com.example.walkgo;

public class Amigo {

    private Integer idAmigo;
    private Integer idUsuario;
    private Integer idUsuarioAmigo;
    private String estado;

    public Amigo(Integer idAmigo, Integer idUsuario, Integer idUsuarioAmigo, String estado) {
        this.idAmigo = idAmigo;
        this.idUsuario = idUsuario;
        this.idUsuarioAmigo = idUsuarioAmigo;
        this.estado = estado;
    }

    public Integer getIdAmigo() { return idAmigo; }
    public Integer getIdUsuario() { return idUsuario; }
    public Integer getIdUsuarioAmigo() { return idUsuarioAmigo; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }
}