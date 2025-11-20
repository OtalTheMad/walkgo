package com.example.walkgo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchNotificaciones, switchTemaOscuro;
    private Button btnVolver;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar SharedPreferences
        prefs = getSharedPreferences("config", MODE_PRIVATE);

        // Aplicar tema guardado
        boolean temaOscuro = prefs.getBoolean("tema_oscuro", false);
        if (temaOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_settings);

        // Referencias
        switchNotificaciones = findViewById(R.id.switchNotificaciones);
        switchTemaOscuro = findViewById(R.id.switchTemaOscuro);
        btnVolver = findViewById(R.id.btnVolver);

        // Cargar preferencias guardadas
        switchNotificaciones.setChecked(prefs.getBoolean("notificaciones", true));
        switchTemaOscuro.setChecked(temaOscuro);

        // Guardar cambios automáticamente al cambiar el switch de notificaciones
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notificaciones", isChecked);
            editor.apply();

            // Simular activación/desactivación de notificaciones
            if (isChecked) {
                Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show();
                // Aquí podrías iniciar alarmas, recordatorios o FCM
            } else {
                Toast.makeText(this, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
                // Aquí cancelar alarmas o notificaciones programadas
            }
        });

        // Aplicar tema oscuro automáticamente al cambiar switch
        switchTemaOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("tema_oscuro", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Volver al Home
        btnVolver.setOnClickListener(v -> finish());
    }
}