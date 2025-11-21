package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.api.walkgo.LoginActivity;
import com.api.walkgo.PerfilActivity;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.UsuarioService;
import com.api.walkgo.models.Usuario;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {

    private static final long TIEMPO_INACTIVIDAD = 10 * 60 * 1000;

    private Handler handler = new Handler();
    private Runnable cerrarSesionRunnable;

    private TextView txtNombreUsuario;
    private TextView txtTotalKmGlobalHome;
    private TextView txtTotalPasosGlobalHome;
    private LinearLayout layoutEstadisticas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtTotalKmGlobalHome = findViewById(R.id.txtTotalKmGlobalHome);
        txtTotalPasosGlobalHome = findViewById(R.id.txtTotalPasosGlobalHome);
        layoutEstadisticas = findViewById(R.id.layoutEstadisticas);

        Button _btnPerfil = findViewById(R.id.btnPerfil);
        Button _btnRanking = findViewById(R.id.btnRanking);
        Button _btnAmigos = findViewById(R.id.btnAmigos);
        Button _btnContador = findViewById(R.id.btnContadorPasos);
        Button _btnEstadisticas = findViewById(R.id.btnEstadisticas);
        Button _btnConfig = findViewById(R.id.btnConfig);
        Button _btnLogout = findViewById(R.id.btnLogout);

        _btnPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
        _btnAmigos.setOnClickListener(v -> startActivity(new Intent(this, SeguidoresActivity.class)));
        _btnContador.setOnClickListener(v -> startActivity(new Intent(this, RecorridoActivity.class)));
        _btnConfig.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        _btnLogout.setOnClickListener(v -> {
            Intent _intent = new Intent(HomeActivity.this, LogoutActivity.class);
            startActivity(_intent);
        });

        cerrarSesionRunnable = this::CerrarSesion;
        ReiniciarContadorInactividad();

        RetrofitClient.Init(getApplicationContext());

        Integer _idUsuario = GetLoggedUserId();
        if (_idUsuario != null) {
            CargarNombreUsuario(_idUsuario);
            CargarResumenGlobal(_idUsuario);
        } else {
            txtNombreUsuario.setText("¡Hola!");
            if (txtTotalKmGlobalHome != null) {
                txtTotalKmGlobalHome.setText("0.00");
            }
            if (txtTotalPasosGlobalHome != null) {
                txtTotalPasosGlobalHome.setText("0");
            }
        }
    }

    private Integer GetLoggedUserId() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", MODE_PRIVATE);
        int _id = _prefs.getInt("id_usuario", -1);
        return _id == -1 ? null : _id;
    }

    private void CargarNombreUsuario(int _idUsuario) {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        UsuarioService _service = _retrofit.create(UsuarioService.class);
        Call<Usuario> _call = _service.GetUsuario(_idUsuario);
        _call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    txtNombreUsuario.setText("¡Hola!");
                    return;
                }
                Usuario _usuario = response.body();
                String _nombre = _usuario.GetUsuario();
                if (_nombre == null || _nombre.isEmpty()) {
                    _nombre = "Usuario " + _idUsuario;
                }
                txtNombreUsuario.setText("¡Hola, " + _nombre + "!");
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                txtNombreUsuario.setText("¡Hola!");
            }
        });
    }

    private void CargarResumenGlobal(int _idUsuario) {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        UsuarioService _service = _retrofit.create(UsuarioService.class);
        Call<Usuario> _call = _service.GetUsuario(_idUsuario);
        _call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    if (txtTotalKmGlobalHome != null) {
                        txtTotalKmGlobalHome.setText("0.00");
                    }
                    if (txtTotalPasosGlobalHome != null) {
                        txtTotalPasosGlobalHome.setText("0");
                    }
                    return;
                }
                Usuario _usuario = response.body();
                Double _totalKm = _usuario.GetTotalDistanciaKm();
                if (_totalKm == null) {
                    _totalKm = 0.0;
                }
                Integer _totalPasos = _usuario.GetTotalPasos();
                if (_totalPasos == null) {
                    _totalPasos = 0;
                }
                if (txtTotalKmGlobalHome != null) {
                    txtTotalKmGlobalHome.setText(String.format(Locale.getDefault(), "%.2f", _totalKm));
                }
                if (txtTotalPasosGlobalHome != null) {
                    txtTotalPasosGlobalHome.setText(String.valueOf(_totalPasos));
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if (txtTotalKmGlobalHome != null) {
                    txtTotalKmGlobalHome.setText("0.00");
                }
                if (txtTotalPasosGlobalHome != null) {
                    txtTotalPasosGlobalHome.setText("0");
                }
            }
        });
    }

    private void ReiniciarContadorInactividad() {
        handler.removeCallbacks(cerrarSesionRunnable);
        handler.postDelayed(cerrarSesionRunnable, TIEMPO_INACTIVIDAD);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ReiniciarContadorInactividad();
    }

    private void CerrarSesion() {
        Toast.makeText(this, "Sesión cerrada por inactividad", Toast.LENGTH_SHORT).show();
        Intent _intent = new Intent(this, LoginActivity.class);
        _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(_intent);
    }
}