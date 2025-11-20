package com.api.walkgo;

import android.util.Base64;

import org.json.JSONObject;

public class JwtUtils {

    public static int GetUserIdFromToken(String token) {
        if (token == null) {
            return -1;
        }
        String[] _parts = token.split("\\.");
        if (_parts.length < 2) {
            return -1;
        }
        byte[] _decoded = Base64.decode(_parts[1], Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        String _payload = new String(_decoded);
        try {
            JSONObject _json = new JSONObject(_payload);
            return _json.getInt("id_usuario");
        } catch (Exception e) {
            return -1;
        }
    }
}