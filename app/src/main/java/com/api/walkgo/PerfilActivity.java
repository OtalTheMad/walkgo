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
        if (loggedUserId <= 0) {
            IrALogin();
            return;
        }
        if (esPropio) {
            setContentView(R.layout.activity_perfil);
        } else {
            setContentView(R.layout.activity_perfil_externo);
        }
        InicializarVistas();
        RetrofitClient.Init(getApplicationContext());

        CargarPerfilDesdeCache();
        CargarPerfilDesdeApi();

        CargarEstadisticas();
        CargarResumenGlobal(perfilUserId);
        if (!esPropio) {
            CargarRelacionSeguir();
        }
    }

    private void InicializarIds() {
        SharedPreferences _prefs = GetPrefs();
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

    private SharedPreferences GetPrefs() {
        return getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
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
        if (!esPropio) {
            btnSeguir = findViewById(R.id.btnSeguir);
            siguiendo = false;
            btnSeguir.setOnClickListener(v -> ToggleSeguir());
        } else {
            btnSeguir = null;
        }
    }

    private void CargarPerfilDesdeApi() {
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
                MostrarPerfil(_perfil);
                GuardarPerfilEnCache(_perfil);
            }

            @Override
            public void onFailure(Call<Perfil> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void MostrarPerfil(Perfil perfil) {
        if (perfil == null) {
            return;
        }
        CargarNombreUsuario(perfil.GetIdUsuario());
        String _bio = perfil.GetBiografia() != null ? perfil.GetBiografia() : "";
        String _pais = perfil.GetPais() != null ? perfil.GetPais() : "";
        String _fecha = perfil.GetFechaNac() != null ? perfil.GetFechaNac() : "";
        txtBiografia.setText(_bio);
        txtPais.setText(_pais);
        txtFechaNac.setText(_fecha);
        String _fotoBase64 = perfil.GetFoto();
        if (_fotoBase64 != null && !_fotoBase64.isEmpty()) {
            try {
                byte[] _bytes = Base64.getDecoder().decode(_fotoBase64);
                imgFotoPerfil.setImageBitmap(BitmapFactory.decodeByteArray(_bytes, 0, _bytes.length));
            } catch (Exception e) {
            }
        }
    }

    private void CargarPerfilDesdeCache() {
        SharedPreferences _prefs = GetPrefs();
        String _prefix = "perfil_" + perfilUserId + "_";
        String _bio = _prefs.getString(_prefix + "biografia", null);
        String _pais = _prefs.getString(_prefix + "pais", null);
        String _fecha = _prefs.getString(_prefix + "fechaNac", null);
        String _fotoBase64 = _prefs.getString(_prefix + "foto", null);
        String _nombre = _prefs.getString(_prefix + "nombre", null);

        if (_nombre != null && txtNombreUsuario != null) {
            txtNombreUsuario.setText(_nombre);
        }
        if (_bio != null && txtBiografia != null) {
            txtBiografia.setText(_bio);
        }
        if (_pais != null && txtPais != null) {
            txtPais.setText(_pais);
        }
        if (_fecha != null && txtFechaNac != null) {
            txtFechaNac.setText(_fecha);
        }
        if (_fotoBase64 != null && !_fotoBase64.isEmpty() && imgFotoPerfil != null) {
            try {
                byte[] _bytes = Base64.getDecoder().decode(_fotoBase64);
                imgFotoPerfil.setImageBitmap(BitmapFactory.decodeByteArray(_bytes, 0, _bytes.length));
            } catch (Exception e) {
            }
        }
    }

    private void GuardarPerfilEnCache(Perfil perfil) {
        if (perfil == null) {
            return;
        }
        SharedPreferences _prefs = GetPrefs();
        SharedPreferences.Editor _editor = _prefs.edit();
        String _prefix = "perfil_" + perfilUserId + "_";
        String _bio = perfil.GetBiografia() != null ? perfil.GetBiografia() : "";
        String _pais = perfil.GetPais() != null ? perfil.GetPais() : "";
        String _fecha = perfil.GetFechaNac() != null ? perfil.GetFechaNac() : "";
        String _foto = perfil.GetFoto() != null ? perfil.GetFoto() : "";
        _editor.putString(_prefix + "biografia", _bio);
        _editor.putString(_prefix + "pais", _pais);
        _editor.putString(_prefix + "fechaNac", _fecha);
        _editor.putString(_prefix + "foto", _foto);
        _editor.apply();
        CargarNombreUsuarioYGuardarCache(perfil.GetIdUsuario());
    }

    private void CargarNombreUsuarioYGuardarCache(int idUsuario) {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        UsuarioService _service = _retrofit.create(UsuarioService.class);
        Call<Usuario> _call = _service.GetUsuario(idUsuario);
        _call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    String _fallback = "Usuario " + idUsuario;
                    txtNombreUsuario.setText(_fallback);
                    GuardarNombreCache(_fallback);
                    return;
                }
                Usuario _usuario = response.body();
                String _nombre = _usuario.GetUsuario();
                if (_nombre == null || _nombre.isEmpty()) {
                    _nombre = "Usuario " + idUsuario;
                }
                txtNombreUsuario.setText(_nombre);
                GuardarNombreCache(_nombre);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                String _fallback = "Usuario " + idUsuario;
                txtNombreUsuario.setText(_fallback);
                GuardarNombreCache(_fallback);
            }
        });
    }

    private void GuardarNombreCache(String nombre) {
        SharedPreferences _prefs = GetPrefs();
        SharedPreferences.Editor _editor = _prefs.edit();
        String _prefix = "perfil_" + perfilUserId + "_";
        _editor.putString(_prefix + "nombre", nombre != null ? nombre : "");
        _editor.apply();
    }

    private void CargarNombreUsuario(int idUsuario) {
        CargarNombreUsuarioYGuardarCache(idUsuario);
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
                String _km = String.valueOf(_estadistica.GetKmRecorrido());
                String _cal = _estadistica.GetCaloriasQuemadas();
                if (_cal == null || _cal.isEmpty()) {
                    _cal = "0";
                }
                txtKmRecorrido.setText(_km);
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

    private void IrALogin() {
        SharedPreferences _prefs = GetPrefs();
        SharedPreferences.Editor _editor = _prefs.edit();
        _editor.clear();
        _editor.apply();
        Intent _intent = new Intent(this, LoginActivity.class);
        _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(_intent);
        finish();
    }
}