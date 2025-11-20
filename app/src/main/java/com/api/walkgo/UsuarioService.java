package com.api.walkgo;

import com.api.walkgo.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UsuarioService {

    @GET("api/usuarios")
    Call<List<Usuario>> GetAllUsuarios();

    @GET("api/usuarios/{id}")
    Call<Usuario> GetUsuario(@Path("id") int idUsuario);
}