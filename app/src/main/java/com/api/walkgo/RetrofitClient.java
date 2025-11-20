package com.api.walkgo;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static void Init(Context context) {
        SharedPreferences _prefs = context.getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        String _token = _prefs.getString("jwt_token", null);

        OkHttpClient.Builder _builder = new OkHttpClient.Builder();
        if (_token != null) {
            _builder.addInterceptor(new JwtInterceptor(_token));
        }
        OkHttpClient _client = _builder.build();

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