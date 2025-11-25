package com.api.walkgo;

import android.util.Base64;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class JwtUtils {

    public static int GetUserIdFromToken(String _token) {
        try {
            JSONObject _payload = GetPayload(_token);
            return _payload.optInt("id_usuario", -1);
        } catch (Exception _e) {
            return -1;
        }
    }

    public static boolean IsTokenExpired(String _token) {
        long _exp = GetExpSeconds(_token);
        if (_exp <= 0) {
            return true;
        }
        long _now = System.currentTimeMillis() / 1000L;
        return _now >= _exp;
    }

    private static long GetExpSeconds(String _token) {
        try {
            JSONObject _payload = GetPayload(_token);
            return _payload.optLong("exp", -1);
        } catch (Exception _e) {
            return -1;
        }
    }

    private static JSONObject GetPayload(String _token) throws Exception {
        if (_token == null) throw new Exception("null");
        String[] _parts = _token.split("\\.");
        if (_parts.length < 2) throw new Exception("bad token");

        byte[] _decoded = Base64.decode(_parts[1], Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        String _json = new String(_decoded, StandardCharsets.UTF_8);
        return new JSONObject(_json);
    }
}