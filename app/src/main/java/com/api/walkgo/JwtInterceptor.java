package com.api.walkgo;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class JwtInterceptor implements Interceptor {

    private final Context context;

    public JwtInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences _prefs = context.getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        String _token = _prefs.getString("jwt_token", null);

        Request.Builder _builder = chain.request().newBuilder();

        if (_token != null && !_token.isEmpty()) {
            _builder.addHeader("Authorization", "Bearer " + _token);
        }

        Request _request = _builder.build();
        return chain.proceed(_request);
    }
}