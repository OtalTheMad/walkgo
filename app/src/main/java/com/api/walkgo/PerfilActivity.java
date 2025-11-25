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

import com.api.walkgo.models.ApiAmigo;
import com.api.walkgo.models.ApiCreateAmigo;
import com.api.walkgo.models.ApiUpdateAmigo;
import com.api.walkgo.models.Estadistica;
import com.api.walkgo.models.Perfil;
import com.api.walkgo.models.Usuario;
import com.example.walkgo.R;

import java.util.Base64;
import java.util.List;
import java.util.Locale;

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
    private TextView txtTotalKmGlobal;
    private TextView txtTotalPasosGlobal;
    private Button btnSeguir;

    private int loggedUserId;
    private int perfilUserId;
    private boolean esPropio;
    private boolean siguiendo;

    private ApiAmigo amigoPerfil;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        CargarPerfil();
        CargarEstadisticas();
        CargarResumenGlobal(perfilUserId);
        if (!esPropio) {
            CargarRelacionSeguir();
        }
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
        txtTotalKmGlobal = findViewById(R.id.txtTotalKmGlobal);
        txtTotalPasosGlobal = findViewById(R.id.txtTotalPasosGlobal);
        if (esPropio) {
            Button _btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
            _btnEditarPerfil.setOnClickListener(v -> IrAEditarPerfil());
            btnSeguir = null;
        } else {
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
                String _fotoBase64 = _perfil.GetFoto();
                if (_fotoBase64 != null && !_fotoBase64.isEmpty()) {
                    try {
                        byte[] _bytes = Base64.getDecoder().decode(_fotoBase64);
                        imgFotoPerfil.setImageBitmap(BitmapFactory.decodeByteArray(_bytes, 0, _bytes.length));
                    } catch (Exception e) {
                    }
                }
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

    private void CargarRelacionSeguir() {
        if (btnSeguir == null) {
            return;
        }
        if (loggedUserId <= 0 || perfilUserId <= 0) {
            siguiendo = false;
            ActualizarTextoBotonSeguir();
            return;
        }
        Retrofit _retrofit = RetrofitClient.GetInstance();
        AmigosAPI _api = _retrofit.create(AmigosAPI.class);
        Call<List<ApiAmigo>> _call = _api.GetAmigosByUsuario(loggedUserId);
        _call.enqueue(new Callback<List<ApiAmigo>>() {
            @Override
            public void onResponse(Call<List<ApiAmigo>> call, Response<List<ApiAmigo>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    amigoPerfil = null;
                    siguiendo = false;
                    ActualizarTextoBotonSeguir();
                    return;
                }
                List<ApiAmigo> _lista = response.body();
                ApiAmigo _encontrado = null;
                for (ApiAmigo _a : _lista) {
                    if (_a.idUsuarioAmigo != null && _a.idUsuarioAmigo == perfilUserId) {
                        _encontrado = _a;
                        break;
                    }
                }
                amigoPerfil = _encontrado;
                siguiendo = amigoPerfil != null && "siguiendo".equals(amigoPerfil.estado);
                ActualizarTextoBotonSeguir();
            }

            @Override
            public void onFailure(Call<List<ApiAmigo>> call, Throwable t) {
                amigoPerfil = null;
                siguiendo = false;
                ActualizarTextoBotonSeguir();
            }
        });
    }

    private void CargarResumenGlobal(int idUsuario) {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        UsuarioService _service = _retrofit.create(UsuarioService.class);
        Call<Usuario> _call = _service.GetUsuario(idUsuario);
        _call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    if (txtTotalKmGlobal != null) {
                        txtTotalKmGlobal.setText("Total km: 0");
                    }
                    if (txtTotalPasosGlobal != null) {
                        txtTotalPasosGlobal.setText("Total pasos: 0");
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
                if (txtTotalKmGlobal != null) {
                    txtTotalKmGlobal.setText("Total km: " + String.format(Locale.getDefault(), "%.2f", _totalKm));
                }
                if (txtTotalPasosGlobal != null) {
                    txtTotalPasosGlobal.setText("Total pasos: " + _totalPasos);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if (txtTotalKmGlobal != null) {
                    txtTotalKmGlobal.setText("Total km: 0");
                }
                if (txtTotalPasosGlobal != null) {
                    txtTotalPasosGlobal.setText("Total pasos: 0");
                }
            }
        });
    }

    private void ActualizarTextoBotonSeguir() {
        if (btnSeguir == null) {
            return;
        }
        if (siguiendo) {
            btnSeguir.setText("Siguiendo");
        } else {
            btnSeguir.setText("Seguir");
        }
    }

    private void ToggleSeguir() {
        if (btnSeguir == null) {
            return;
        }
        if (loggedUserId <= 0 || perfilUserId <= 0) {
            Toast.makeText(PerfilActivity.this, "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }
        btnSeguir.setEnabled(false);
        Retrofit _retrofit = RetrofitClient.GetInstance();
        AmigosAPI _api = _retrofit.create(AmigosAPI.class);
        if (!siguiendo) {
            ApiCreateAmigo _req = new ApiCreateAmigo(
                    loggedUserId,
                    perfilUserId
            );
            Call<ApiCreateAmigo> _call = _api.CreateAmigo(_req);
            _call.enqueue(new Callback<ApiCreateAmigo>() {
                @Override
                public void onResponse(Call<ApiCreateAmigo> call, Response<ApiCreateAmigo> response) {
                    btnSeguir.setEnabled(true);
                    if (!response.isSuccessful()) {
                        Toast.makeText(PerfilActivity.this, "No se pudo seguir", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CargarRelacionSeguir();
                }

                @Override
                public void onFailure(Call<ApiCreateAmigo> call, Throwable t) {
                    btnSeguir.setEnabled(true);
                    Toast.makeText(PerfilActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (amigoPerfil == null || amigoPerfil.idAmigo == null) {
                btnSeguir.setEnabled(true);
                Toast.makeText(PerfilActivity.this, "Relación no encontrada", Toast.LENGTH_SHORT).show();
                return;
            }
            String _nuevoEstado = "no_seguido";
            ApiUpdateAmigo _req = new ApiUpdateAmigo(
                    loggedUserId,
                    perfilUserId,
                    _nuevoEstado
            );
            Call<ApiUpdateAmigo> _call = _api.UpdateAmigo(amigoPerfil.idAmigo, _req);
            _call.enqueue(new Callback<ApiUpdateAmigo>() {
                @Override
                public void onResponse(Call<ApiUpdateAmigo> call, Response<ApiUpdateAmigo> response) {
                    btnSeguir.setEnabled(true);
                    if (!response.isSuccessful()) {
                        Toast.makeText(PerfilActivity.this, "No se pudo dejar de seguir", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    siguiendo = false;
                    if (amigoPerfil != null) {
                        amigoPerfil.estado = _nuevoEstado;
                    }
                    ActualizarTextoBotonSeguir();
                }

                @Override
                public void onFailure(Call<ApiUpdateAmigo> call, Throwable t) {
                    btnSeguir.setEnabled(true);
                    Toast.makeText(PerfilActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void IrAEditarPerfil() {
        Intent _intent = new Intent(this, com.example.walkgo.EditarPerfilActivity.class);
        startActivity(_intent);
    }
}