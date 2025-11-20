package com.api.walkgo;

import com.api.walkgo.models.LoginRequest;
import com.api.walkgo.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("api/auth/login")
    Call<LoginResponse> Login(@Body LoginRequest loginRequest);
}