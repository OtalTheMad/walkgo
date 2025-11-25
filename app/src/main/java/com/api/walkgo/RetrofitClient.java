package com.api.walkgo;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static void Init(Context context) {
        Context _app = context.getApplicationContext();

        OkHttpClient _client = new OkHttpClient.Builder()
                .addInterceptor(new JwtInterceptor(_app))
                .addInterceptor(chain -> {
                    okhttp3.Response _res = chain.proceed(chain.request());
                    if (_res.code() == 401 || _res.code() == 403) {
                        _app.getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE)
                                .edit()
                                .remove("jwt_token")
                                .remove("id_usuario")
                                .apply();
                    }
                    return _res;
                })
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