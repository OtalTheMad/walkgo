package com.example.walkgo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.walkgo.fragments.HomeFragment;
import com.example.walkgo.fragments.ProgressFragment;
import com.example.walkgo.fragments.ProfileFragment;
// Asegúrate de crear la clase RankingFragment
import com.example.walkgo.fragments.RankingFragment;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inicializar la barra de navegación
        bottomNavigationView = findViewById(R.id.bottom_navigation); // ID del XML
        bottomNavigationView.setOnItemSelectedListener(navListener);

        // 2. Cargar el fragmento de inicio (Carrera) por defecto
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()) // ID del contenedor en el XML
                    .commit();
        }
    }

    // ====================================================
    // 🧭 LÓGICA DE NAVEGACIÓN (BottomNavigationView)
    // ====================================================
    private final BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    // Usando los IDs definidos en res/menu/bottom_nav_menu.xml
                    if (itemId == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                    } else if (itemId == R.id.nav_progress) {
                        selectedFragment = new ProgressFragment();
                    } else if (itemId == R.id.nav_ranking) {
                        selectedFragment = new RankingFragment(); // Necesitas crear esta clase
                    } else if (itemId == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment(); // Necesitas crear esta clase
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }
                    return true;
                }
            };

    // ====================================================
    // 🛰️ LÓGICA DE PERMISOS GPS
    // ====================================================

    public boolean checkLocationPermission() {
        // Verifica si el permiso ya fue concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Solicita el permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de ubicación concedido.", Toast.LENGTH_SHORT).show();
                // Opcional: Si la carrera estaba pendiente de inicio, puedes iniciarla aquí
            } else {
                Toast.makeText(this, "El seguimiento GPS requiere el permiso de ubicación.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ====================================================
    // 🏃 LÓGICA DEL SERVICIO DE CARRERA
    // ====================================================

    /**
     * Inicia el RunningService en primer plano, previa verificación de permisos.
     */
    public void startRunningService() {
        if (!checkLocationPermission()) {
            Toast.makeText(this, "Debe conceder el permiso de GPS para iniciar la carrera.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent serviceIntent = new Intent(this, RunningService.class);
        serviceIntent.setAction("ACTION_START_RUN");

        // Manejo de servicios en primer plano (Foreground Service) para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            startService(serviceIntent);
        }

        Toast.makeText(this, "Carrera iniciada. Grabando GPS.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Detiene el RunningService.
     */
    public void stopRunningService() {
        Intent serviceIntent = new Intent(this, RunningService.class);
        serviceIntent.setAction("ACTION_STOP_RUN");
        stopService(serviceIntent);

        // La lógica de guardar en el VPS ocurre dentro de RunningService.onDestroy()
    }
}