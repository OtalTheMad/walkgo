package com.api.walkgo;

import android.util.Base64;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class JwtUtils {

    public static int GetUserIdFromToken(String _token) {
        JSONObject _payload = GetPayload(_token);
        if (_payload == null) {
            return -1;
        }
        return _payload.optInt("id_usuario", -1);
    }

    public static boolean IsTokenExpired(String _token) {
        long _exp = GetExpSeconds(_token);
        if (_exp <= 0) {
            return true;
        }

        if (_exp > 1000000000000L) {
            _exp = _exp / 1000L;
        }

        long _now = System.currentTimeMillis() / 1000L;
        return _now >= _exp;
    }

    private static long GetExpSeconds(String _token) {
        JSONObject _payload = GetPayload(_token);
        if (_payload == null) {
            return -1;
        }
        return _payload.optLong("exp", -1);
    }

    private static JSONObject GetPayload(String _token) {
        try {
            if (_token == null) {
                return null;
            }
            String[] _parts = _token.split("\\.");
            if (_parts.length < 2) {
                return null;
            }

            byte[] _decoded = Base64.decode(_parts[1], Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            String _json = new String(_decoded, StandardCharsets.UTF_8);
            return new JSONObject(_json);
        } catch (Exception _e) {
            return null;
        }
    }
}