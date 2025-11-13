package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

public class PerfilActivity extends AppCompatActivity {

    private ImageView imgPerfil;
    private TextView tvNombre, tvUsuario, tvKm;
    private Button btnAccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Vincular elementos del layout
        imgPerfil = findViewById(R.id.imgPerfil);
        tvNombre = findViewById(R.id.tvNombre);
        tvUsuario = findViewById(R.id.tvUsuario);
        tvKm = findViewById(R.id.tvKm);
        btnAccion = findViewById(R.id.btnAccion);

        // Obtener datos enviados desde AmigosAdapter
        String nombre = getIntent().getStringExtra("nombre");
        String usuario = getIntent().getStringExtra("usuario");
        int foto = getIntent().getIntExtra("foto", R.drawable.ic_launcher_foreground);
        String estado = getIntent().getStringExtra("estado");
        int km = getIntent().getIntExtra("km", 0);

        // Mostrar datos
        tvNombre.setText(nombre);
        tvUsuario.setText(usuario);
        imgPerfil.setImageResource(foto);
        tvKm.setText("Kilómetros recorridos: " + km + " km");

        // Configurar botón según estado
        switch (estado) {
            case "activo":
                btnAccion.setText("Eliminar amigo");
                btnAccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red_700));
                btnAccion.setOnClickListener(v -> {
                    Toast.makeText(this, "Amigo eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                });
                break;
            case "no_amigo":
                btnAccion.setText("Agregar amigo");
                btnAccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green_700));
                btnAccion.setOnClickListener(v -> {
                    Toast.makeText(this, "Solicitud de amistad enviada", Toast.LENGTH_SHORT).show();
                    finish();
                });
                break;
            default:
                btnAccion.setText("Solicitud pendiente");
                btnAccion.setEnabled(false);
                break;
        }
    }
}