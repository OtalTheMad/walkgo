package com.api.walkgo;

import com.api.walkgo.models.Estadistica;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EstadisticaService {
    @GET("api/estadisticas/{id}")
    Call<Estadistica> GetEstadistica(@Path("id") int idUsuario);
}