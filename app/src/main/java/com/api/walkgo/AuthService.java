package com.api.walkgo;

import com.api.walkgo.models.LoginRequest;
import com.api.walkgo.models.LoginResponse;
import com.api.walkgo.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("api/auth/login")
    Call<LoginResponse> Login(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<LoginResponse> Register(@Body RegisterRequest registerRequest);
}