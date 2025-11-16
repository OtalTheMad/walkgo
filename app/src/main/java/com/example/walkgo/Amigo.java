package com.example.walkgo;

public class Amigo {

    private String nombre;
    private String usuario;
    private String estado; // "activo", "no_amigo", "solicitud_enviada", "solicitud_recibida"
    private int kmRecorridos; // opcional, si quieremos mostrar km
    private int fotoPerfil; // recurso drawable

    public Amigo(String nombre, String usuario, String estado, int fotoPerfil, int kmRecorridos) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.estado = estado;
        this.fotoPerfil = fotoPerfil;
        this.kmRecorridos = kmRecorridos;
    }

    public String getNombre() { return nombre; }
    public String getUsuario() { return usuario; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getKmRecorridos() { return kmRecorridos; }
    public int getFotoPerfil() { return fotoPerfil; }
}