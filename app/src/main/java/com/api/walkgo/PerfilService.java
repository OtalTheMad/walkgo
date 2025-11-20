package com.api.walkgo;

import com.api.walkgo.models.Perfil;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PerfilService {
    @GET("api/perfiles/{id}")
    Call<Perfil> GetPerfil(@Path("id") int idUsuario);
}