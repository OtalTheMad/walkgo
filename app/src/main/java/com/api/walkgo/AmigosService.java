package com.api.walkgo;

public class AmigosService {

    private static AmigosAPI amigosApi;
    private static String storedToken = null;

    public static void SetToken(String _token) {
        storedToken = _token;
        amigosApi = null;
    }

    public static AmigosAPI GetApi() {
        if (amigosApi == null) {

            if (storedToken == null || storedToken.isEmpty()) {
                throw new IllegalStateException("Token JWT no está configurado. Llama a SetToken(token) primero.");
            }

            amigosApi = RetrofitClient.GetInstance().create(AmigosAPI.class);
        }
        return amigosApi;
    }
}