package com.api.walkgo;

import com.api.walkgo.models.Amigo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AmigoService {
    @POST("api/amigos")
    Call<Amigo> CrearRelacion(@Body Amigo amigo);

    @PUT("api/amigos")
    Call<Amigo> ActualizarRelacion(@Body Amigo amigo);
}