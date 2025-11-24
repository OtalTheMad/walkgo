package com.api.walkgo;

import com.api.walkgo.models.Perfil;
import com.api.walkgo.models.PerfilUpdateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PerfilService {

    @GET("api/perfiles/{id}")
    Call<Perfil> GetPerfil(@Path("id") int idUsuario);

    @PUT("api/perfiles/usuario/{idUsuario}")
    Call<Perfil> UpdatePerfil(@Path("idUsuario") int idUsuario, @Body PerfilUpdateRequest request);
}