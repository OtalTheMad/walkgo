package com.api.walkgo;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static void Init(Context context) {
        OkHttpClient _client = new OkHttpClient.Builder()
                .addInterceptor(new JwtInterceptor(context))
                .build();

        instance = new Retrofit.Builder()
                .baseUrl("http://34.134.77.253:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(_client)
                .build();
    }

    public static Retrofit GetInstance() {
        return instance;
    }
}