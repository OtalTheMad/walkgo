package com.api.walkgo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

import com.example.walkgo.HomeActivity;

import java.lang.ref.WeakReference;

public final class SessionManager {

    private static final long TIMEOUT_MS = 10 * 60 * 1000L;

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static WeakReference<Activity> currentActivity = new WeakReference<>(null);
    private static Runnable logoutRunnable;

    private SessionManager() {
    }

    public static void Bind(Activity _activity) {
        currentActivity = new WeakReference<>(_activity);
        EnsureLogoutRunnable(_activity.getApplicationContext());
        Touch();
    }

    public static void Unbind(Activity _activity) {
        Activity _current = currentActivity.get();
        if (_current == _activity) {
            handler.removeCallbacks(logoutRunnable);
            currentActivity = new WeakReference<>(null);
        }
    }

    public static void Touch() {
        if (logoutRunnable == null) {
            return;
        }
        handler.removeCallbacks(logoutRunnable);
        handler.postDelayed(logoutRunnable, TIMEOUT_MS);
    }

    public static boolean HasValidSession(Context _context) {
        SharedPreferences _prefs = _context.getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        String _token = _prefs.getString("jwt_token", null);
        int _id = _prefs.getInt("id_usuario", -1);

        if (_token == null || _token.trim().isEmpty() || _id <= 0) {
            ClearSession(_context);
            return false;
        }

        if (JwtUtils.IsTokenExpired(_token)) {
            ClearSession(_context);
            return false;
        }

        return true;
    }

    public static void ForceLogout(Activity _activity) {
        ClearSession(_activity);

        Intent _intent = new Intent(_activity, LoginActivity.class);
        _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _activity.startActivity(_intent);
        _activity.finish();
    }

    private static void ClearSession(Context _context) {
        _context.getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE).edit().clear().apply();
    }

    private static void EnsureLogoutRunnable(Context _context) {
        if (logoutRunnable != null) {
            return;
        }

        Context _app = _context.getApplicationContext();
        logoutRunnable = () -> {
            Activity _a = currentActivity.get();
            ClearSession(_app);

            Intent _intent = new Intent(_app, LoginActivity.class);
            _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            _app.startActivity(_intent);

            if (_a != null && !_a.isFinishing()) {
                _a.finish();
            }
        };
    }
}