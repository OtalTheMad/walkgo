package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private static final long TIEMPO_INACTIVIDAD = 10 * 60 * 1000; // 10 minutos
    private Handler handler = new Handler();
    private Runnable cerrarSesionRunnable;

    private TextView txtNombreUsuario, txtDistanciaSemana;
    private LinearLayout layoutEstadisticas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Vincular elementos
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtDistanciaSemana = findViewById(R.id.txtDistanciaSemana);
        layoutEstadisticas = findViewById(R.id.layoutEstadisticas);

        Button btnPerfil = findViewById(R.id.btnPerfil);
        Button btnRanking = findViewById(R.id.btnRanking);
        Button btnAmigos = findViewById(R.id.btnAmigos);
        Button btnContador = findViewById(R.id.btnContadorPasos);
        Button btnEstadisticas = findViewById(R.id.btnEstadisticas);

        // Ejemplo: mostrar nombre de usuario
        txtNombreUsuario.setText("¡Hola, Juan!");
        txtDistanciaSemana.setText("Distancia recorrida esta semana: 12 km");

        // Ejemplo: estadísticas semanales (puedes poner gráfica o lista)
        String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        int[] distancias = {2, 3, 1, 4, 2, 5, 3}; // km recorridos
        for (int i = 0; i < dias.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(dias[i] + ": " + distancias[i] + " km");
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setTextSize(16f);
            layoutEstadisticas.addView(tv);
        }

        // Configurar navegación a otras actividades
       // btnPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
        // btnRanking.setOnClickListener(v -> startActivity(new Intent(this, RankingActivity.class)));
        //btnAmigos.setOnClickListener(v -> startActivity(new Intent(this, AmigosActivity.class)));
        //btnContador.setOnClickListener(v -> startActivity(new Intent(this, ContadorPasosActivity.class)));
        //btnEstadisticas.setOnClickListener(v -> startActivity(new Intent(this, EstadisticasActivity.class)));

        // Configurar cierre de sesión por inactividad
        cerrarSesionRunnable = this::cerrarSesion;
        reiniciarContadorInactividad();
    }

    // Reinicia el temporizador de inactividad
    private void reiniciarContadorInactividad() {
        handler.removeCallbacks(cerrarSesionRunnable);
        handler.postDelayed(cerrarSesionRunnable, TIEMPO_INACTIVIDAD);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        reiniciarContadorInactividad(); // reinicia temporizador con cada interacción
    }

    private void cerrarSesion() {
        Toast.makeText(this, "Sesión cerrada por inactividad", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, activity_login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
