package com.api.walkgo;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected boolean RequiresAuth() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager.Bind(this);

        if (RequiresAuth() && !SessionManager.HasValidSession(this)) {
            SessionManager.ForceLogout(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SessionManager.Unbind(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent _ev) {
        SessionManager.Touch();
        return super.dispatchTouchEvent(_ev);
    }
}