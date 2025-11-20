package com.api.walkgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.api.walkgo.models.Amigo;
import com.api.walkgo.models.Usuario;
import com.api.walkgo.models.Estadistica;
import com.api.walkgo.models.Perfil;
import com.example.walkgo.R;

import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PerfilActivity extends AppCompatActivity {

    private ImageView imgFotoPerfil;
    private TextView txtNombreUsuario;
    private TextView txtFechaNac;
    private TextView txtPais;
    private TextView txtBiografia;
    private TextView txtKmRecorrido;
    private TextView txtCaloriasQuemadas;
    private Button btnSeguir;

    private int loggedUserId;
    private int perfilUserId;
    private boolean esPropio;
    private boolean siguiendo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InicializarIds();
        if (esPropio) {
            setContentView(R.layout.activity_perfil);
        } else {
            setContentView(R.layout.activity_perfil_externo);
        }
        InicializarVistas();
        RetrofitClient.Init(getApplicationContext());
        CargarPerfil();
        CargarEstadisticas();
    }

    private void InicializarIds() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        loggedUserId = _prefs.getInt("id_usuario", -1);
        Intent _intent = getIntent();
        int _perfilIdIntent = _intent.getIntExtra("id_usuario_perfil", -1);
        if (_perfilIdIntent <= 0) {
            perfilUserId = loggedUserId;
        } else {
            perfilUserId = _perfilIdIntent;
        }
        esPropio = loggedUserId == perfilUserId;
    }

    private void InicializarVistas() {
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtFechaNac = findViewById(R.id.txtFechaNac);
        txtPais = findViewById(R.id.txtPais);
        txtBiografia = findViewById(R.id.txtBiografia);
        txtKmRecorrido = findViewById(R.id.txtKmRecorrido);
        txtCaloriasQuemadas = findViewById(R.id.txtCaloriasQuemadas);
        if (!esPropio) {
            btnSeguir = findViewById(R.id.btnSeguir);
            siguiendo = false;
            btnSeguir.setText("Seguir");
            btnSeguir.setOnClickListener(v -> ToggleSeguir());
        }
    }

    private void CargarNombreUsuario(int idUsuario) {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        UsuarioService _service = _retrofit.create(UsuarioService.class);
        Call<Usuario> _call = _service.GetUsuario(idUsuario);
        _call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    txtNombreUsuario.setText("Usuario " + idUsuario);
                    return;
                }
                Usuario _usuario = response.body();
                String _nombre = _usuario.GetUsuario();
                if (_nombre == null || _nombre.isEmpty()) {
                    _nombre = "Usuario " + idUsuario;
                }
                txtNombreUsuario.setText(_nombre);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                txtNombreUsuario.setText("Usuario " + idUsuario);
            }
        });
    }

    private void CargarPerfil() {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        PerfilService _service = _retrofit.create(PerfilService.class);
        Call<Perfil> _call = _service.GetPerfil(perfilUserId);
        _call.enqueue(new Callback<Perfil>() {
            @Override
            public void onResponse(Call<Perfil> call, Response<Perfil> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PerfilActivity.this, "Error cargando perfil", Toast.LENGTH_SHORT).show();
                    return;
                }
                Perfil _perfil = response.body();
                CargarNombreUsuario(_perfil.GetIdUsuario());
                txtBiografia.setText(_perfil.GetBiografia() != null ? _perfil.GetBiografia() : "");
                txtPais.setText(_perfil.GetPais() != null ? _perfil.GetPais() : "");
                txtFechaNac.setText(_perfil.GetFechaNac() != null ? _perfil.GetFechaNac() : "");
            }

            @Override
            public void onFailure(Call<Perfil> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void CargarEstadisticas() {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        EstadisticaService _service = _retrofit.create(EstadisticaService.class);
        Call<Estadistica> _call = _service.GetEstadistica(perfilUserId);
        _call.enqueue(new Callback<Estadistica>() {
            @Override
            public void onResponse(Call<Estadistica> call, Response<Estadistica> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    txtKmRecorrido.setText("0");
                    txtCaloriasQuemadas.setText("0");
                    return;
                }
                Estadistica _estadistica = response.body();
                txtKmRecorrido.setText(String.valueOf(_estadistica.GetKmRecorrido()));
                String _cal = _estadistica.GetCaloriasQuemadas();
                if (_cal == null || _cal.isEmpty()) {
                    _cal = "0";
                }
                txtCaloriasQuemadas.setText(_cal);
            }

            @Override
            public void onFailure(Call<Estadistica> call, Throwable t) {
                txtKmRecorrido.setText("0");
                txtCaloriasQuemadas.setText("0");
            }
        });
    }

    private void ToggleSeguir() {
        if (btnSeguir == null) {
            return;
        }
        btnSeguir.setEnabled(false);
        Retrofit _retrofit = RetrofitClient.GetInstance();
        AmigoService _service = _retrofit.create(AmigoService.class);
        Amigo _amigo = new Amigo();
        _amigo.SetIdUsuario(loggedUserId);
        _amigo.SetIdUsuarioAmigo(perfilUserId);
        if (!siguiendo) {
            _amigo.SetEstado("activo");
            Call<Amigo> _call = _service.CrearRelacion(_amigo);
            _call.enqueue(new Callback<Amigo>() {
                @Override
                public void onResponse(Call<Amigo> call, Response<Amigo> response) {
                    btnSeguir.setEnabled(true);
                    if (!response.isSuccessful()) {
                        Toast.makeText(PerfilActivity.this, "No se pudo seguir", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    siguiendo = true;
                    btnSeguir.setText("Siguiendo");
                }

                @Override
                public void onFailure(Call<Amigo> call, Throwable t) {
                    btnSeguir.setEnabled(true);
                    Toast.makeText(PerfilActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            _amigo.SetEstado("no_amigo");
            Call<Amigo> _call = _service.ActualizarRelacion(_amigo);
            _call.enqueue(new Callback<Amigo>() {
                @Override
                public void onResponse(Call<Amigo> call, Response<Amigo> response) {
                    btnSeguir.setEnabled(true);
                    if (!response.isSuccessful()) {
                        Toast.makeText(PerfilActivity.this, "No se pudo dejar de seguir", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    siguiendo = false;
                    btnSeguir.setText("Seguir");
                }

                @Override
                public void onFailure(Call<Amigo> call, Throwable t) {
                    btnSeguir.setEnabled(true);
                    Toast.makeText(PerfilActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
