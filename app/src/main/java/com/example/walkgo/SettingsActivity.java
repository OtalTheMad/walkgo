package com.example.walkgo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchTemaOscuro;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("config", MODE_PRIVATE);

        boolean _temaOscuro = prefs.getBoolean("tema_oscuro", false);
        if (_temaOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_settings);

        switchTemaOscuro = findViewById(R.id.switchTemaOscuro);

        switchTemaOscuro.setChecked(_temaOscuro);


        switchTemaOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor _editor = prefs.edit();
            _editor.putBoolean("tema_oscuro", isChecked);
            _editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
}