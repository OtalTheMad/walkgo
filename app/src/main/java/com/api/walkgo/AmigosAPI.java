package com.api.walkgo;

import com.api.walkgo.models.ApiAmigo;
import com.api.walkgo.models.ApiCreateAmigo;
import com.api.walkgo.models.ApiUpdateAmigo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface AmigosAPI {

    @GET("api/amigos/usuario/{idUsuario}")
    Call<List<ApiAmigo>> GetAmigosByUsuario(@Path("idUsuario") Integer idUsuario);

    @POST("api/amigos")
    Call<ApiAmigo> CreateAmigo(@Body ApiCreateAmigo request);

    @PUT("api/amigos/{id}")
    Call<ApiAmigo> UpdateAmigo(@Path("id") Integer id, @Body ApiUpdateAmigo request);
}