package com.example.walkgo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.api.walkgo.LoginActivity;

public class LogoutActivity extends AppCompatActivity {

    private static final long TIEMPO_INACTIVIDAD = 10 * 60 * 1000; // 10 minutos
    private Handler handler = new Handler();
    private Runnable cerrarSesionRunnable;

    private Button btnCerrarSesion;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // EdgeToEdge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logout);

        mainLayout = findViewById(R.id.mainLogout);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Cierre manual de sesión
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        // Cierre automático por inactividad
        cerrarSesionRunnable = this::cerrarSesion;
        reiniciarContadorInactividad();
    }

    // Reinicia temporizador
    private void reiniciarContadorInactividad() {
        handler.removeCallbacks(cerrarSesionRunnable);
        handler.postDelayed(cerrarSesionRunnable, TIEMPO_INACTIVIDAD);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        reiniciarContadorInactividad();
    }

    private void cerrarSesion() {
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}