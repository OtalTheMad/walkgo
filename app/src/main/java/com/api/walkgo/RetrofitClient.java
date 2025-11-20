package com.api.walkgo;

import android.content.Context;
import android.content.SharedPreferences;

import com.api.walkgo.JwtInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit instance;

    public static void Init(Context context) {

        SharedPreferences prefs = context.getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);

        //OM: Token temporal, sustituir al integrar el login con el modulo de Amigos.

        //String token = prefs.getString("jwt_token", null);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc2MzM5OTkxNywiZXhwIjoxNzYzNDg2MzE3fQ.jDUjnSk-GJSW-JoJTANmsSpyHe7-83a2xXHFuG8ANWM";

        //Fin de Token

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new JwtInterceptor(token))
                .build();

        instance = new Retrofit.Builder()
                .baseUrl("http://34.134.77.253:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static Retrofit GetInstance() {
        return instance;
    }
}