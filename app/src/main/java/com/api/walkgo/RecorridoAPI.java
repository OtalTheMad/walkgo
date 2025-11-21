package com.api.walkgo;

import com.api.walkgo.models.ApiFinalizarRecorridoRequest;
import com.api.walkgo.models.Recorrido;
import com.api.walkgo.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RecorridoAPI {
    @POST("api/usuarios/{id}/recorridos/finalizar")
    Call<Usuario> FinalizarRecorrido(@Path("id") Integer idUsuario, @Body ApiFinalizarRecorridoRequest request);

    @GET("api/usuarios/{id}/recorridos/semana")
    Call<List<Recorrido>> GetRecorridosSemana(@Path("id") Integer idUsuario);
}