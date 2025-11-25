package com.example.walkgo;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.api.walkgo.RecorridoAPI;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.SessionManager;
import com.api.walkgo.models.ApiFinalizarRecorridoRequest;
import com.api.walkgo.models.Usuario;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecorridoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    private static final long MOVIMIENTO_GRACE_MS = 8000L;
    private static final long MOVIMIENTO_TIMEOUT_MS = 20000L;
    private static final long MOVIMIENTO_CHECK_MS = 1500L;
    private static final float MIN_DELTA_METERS = 2.0f;

    private GoogleMap map;
    private TextView txtDistanciaSesion;
    private Button btnIniciar;
    private Button btnDetener;
    private Button btnGuardar;

    private ColorStateList tintIniciar;
    private ColorStateList tintDetener;
    private ColorStateList tintGuardar;
    private ColorStateList tintDisabled;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable movimientoRunnable;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean recorridoActivo;
    private boolean recorridoPendienteGuardar;

    private Location ultimaLocation;
    private double distanciaMetros;

    private long inicioRecorridoMs;
    private long ultimoMovimientoMs;

    private Integer idUsuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorrido);

        txtDistanciaSesion = findViewById(R.id.txtDistanciaSesion);
        btnIniciar = findViewById(R.id.btnIniciarRecorrido);
        btnDetener = findViewById(R.id.btnDetenerRecorrido);
        btnGuardar = findViewById(R.id.btnGuardarRecorrido);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        idUsuarioActual = GetLoggedUserId();
        if (idUsuarioActual == null) {
            Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        RetrofitClient.Init(getApplicationContext());

        SupportMapFragment _mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRecorrido);
        if (_mapFragment != null) {
            _mapFragment.getMapAsync(this);
        }

        tintIniciar = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_700));
        tintDetener = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        tintGuardar = ColorStateList.valueOf(ResolveColorAttr(android.R.attr.colorPrimary));
        tintDisabled = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.darker_gray));

        SetUiEstadoInicial();

        btnIniciar.setOnClickListener(v -> IniciarRecorrido());
        btnDetener.setOnClickListener(v -> DetenerRecorrido());
        btnGuardar.setOnClickListener(v -> GuardarRecorrido());
    }

    private int ResolveColorAttr(int _attr) {
        TypedValue _tv = new TypedValue();
        boolean _ok = getTheme().resolveAttribute(_attr, _tv, true);
        if (_ok) {
            if (_tv.resourceId != 0) {
                return ContextCompat.getColor(this, _tv.resourceId);
            }
            return _tv.data;
        }
        return ContextCompat.getColor(this, R.color.walkgo_primary);
    }

    private void SetUiEstadoInicial() {
        recorridoActivo = false;
        recorridoPendienteGuardar = false;
        distanciaMetros = 0.0;
        ultimaLocation = null;
        ActualizarTextoDistancia();
        AplicarEstadoBotones();
    }

    private void AplicarEstadoBotones() {
        boolean _puedeIniciar = !recorridoActivo && !recorridoPendienteGuardar;
        boolean _puedeDetener = recorridoActivo;
        boolean _puedeGuardar = !recorridoActivo && recorridoPendienteGuardar && distanciaMetros > 0.0;

        SetBotonEstado(btnIniciar, _puedeIniciar, tintIniciar);
        SetBotonEstado(btnDetener, _puedeDetener, tintDetener);
        SetBotonEstado(btnGuardar, _puedeGuardar, tintGuardar);
    }

    private void SetBotonEstado(Button _btn, boolean _enabled, ColorStateList _tintEnabled) {
        _btn.setEnabled(_enabled);
        _btn.setAlpha(_enabled ? 1.0f : 0.55f);
        _btn.setBackgroundTintList(_enabled ? _tintEnabled : tintDisabled);
    }

    private Integer GetLoggedUserId() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        int _id = _prefs.getInt("id_usuario", -1);
        return _id <= 0 ? null : _id;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        VerificarPermisosUbicacion();
    }

    private void VerificarPermisosUbicacion() {
        boolean _fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean _coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (_fine && _coarse) {
            HabilitarUbicacionMapa();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }
    }

    private void HabilitarUbicacionMapa() {
        if (map == null) {
            return;
        }
        boolean _fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean _coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!_fine && !_coarse) {
            return;
        }
        map.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng _pos = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(_pos, 16f));
            }
        });
    }

    private void IniciarRecorrido() {
        if (recorridoActivo || recorridoPendienteGuardar) {
            return;
        }

        distanciaMetros = 0.0;
        ultimaLocation = null;
        ActualizarTextoDistancia();

        long _now = System.currentTimeMillis();
        inicioRecorridoMs = _now;
        ultimoMovimientoMs = _now;

        IniciarActualizacionesUbicacion();

        recorridoActivo = true;
        recorridoPendienteGuardar = false;

        IniciarWatchdogMovimiento();
        AplicarEstadoBotones();

        Toast.makeText(this, "Recorrido iniciado", Toast.LENGTH_SHORT).show();
    }

    private void DetenerRecorrido() {
        if (!recorridoActivo) {
            return;
        }

        DetenerActualizacionesUbicacion();
        DetenerWatchdogMovimiento();

        recorridoActivo = false;

        if (distanciaMetros <= 0.0) {
            CancelarRecorridoSinMovimiento();
            return;
        }

        recorridoPendienteGuardar = true;
        AplicarEstadoBotones();

        Toast.makeText(this, "Recorrido detenido", Toast.LENGTH_SHORT).show();
    }

    private void CancelarRecorridoSinMovimiento() {
        DetenerActualizacionesUbicacion();
        DetenerWatchdogMovimiento();

        recorridoActivo = false;
        recorridoPendienteGuardar = false;

        distanciaMetros = 0.0;
        ultimaLocation = null;

        ActualizarTextoDistancia();
        AplicarEstadoBotones();

        Toast.makeText(this, "No hay distancia recorrida", Toast.LENGTH_SHORT).show(); SetBotonEstado(btnIniciar, true, tintIniciar);
    }

    private void GuardarRecorrido() {
        if (recorridoActivo) {
            Toast.makeText(this, "Detén el recorrido antes de guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!recorridoPendienteGuardar) {
            return;
        }

        double _distKm = distanciaMetros / 1000.0;
        if (_distKm <= 0.0) {
            CancelarRecorridoSinMovimiento();
            return;
        }

        int _pasosEstimados = (int) Math.round(_distKm * 1300.0);

        SetBotonEstado(btnGuardar, false, tintGuardar);

        Retrofit _retrofit = RetrofitClient.GetInstance();
        RecorridoAPI _api = _retrofit.create(RecorridoAPI.class);

        ApiFinalizarRecorridoRequest _req = new ApiFinalizarRecorridoRequest(_distKm, _pasosEstimados);

        _api.FinalizarRecorrido(idUsuarioActual, _req).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RecorridoActivity.this, "Error guardando recorrido", Toast.LENGTH_SHORT).show();
                    AplicarEstadoBotones();
                    return;
                }

                Toast.makeText(RecorridoActivity.this, "Recorrido guardado", Toast.LENGTH_SHORT).show();

                distanciaMetros = 0.0;
                ultimaLocation = null;
                recorridoPendienteGuardar = false;

                ActualizarTextoDistancia();
                AplicarEstadoBotones();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(RecorridoActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                AplicarEstadoBotones();
            }
        });
    }

    private void IniciarWatchdogMovimiento() {
        DetenerWatchdogMovimiento();

        movimientoRunnable = new Runnable() {
            @Override
            public void run() {
                if (!recorridoActivo) {
                    return;
                }

                long _now = System.currentTimeMillis();
                boolean _yaPasoGrace = (_now - inicioRecorridoMs) >= MOVIMIENTO_GRACE_MS;
                boolean _sinMovimiento = (_now - ultimoMovimientoMs) >= MOVIMIENTO_TIMEOUT_MS;

                if (_yaPasoGrace && _sinMovimiento && distanciaMetros <= 0.0) {
                    CancelarRecorridoSinMovimiento();
                    return;
                }

                handler.postDelayed(this, MOVIMIENTO_CHECK_MS);
            }
        };

        handler.postDelayed(movimientoRunnable, MOVIMIENTO_CHECK_MS);
    }

    private void DetenerWatchdogMovimiento() {
        if (movimientoRunnable != null) {
            handler.removeCallbacks(movimientoRunnable);
            movimientoRunnable = null;
        }
    }

    private void IniciarActualizacionesUbicacion() {
        LocationRequest _request = LocationRequest.create();
        _request.setInterval(3000);
        _request.setFastestInterval(2000);
        _request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location _location = locationResult.getLastLocation();
                if (_location == null) {
                    return;
                }
                ProcesarNuevaUbicacion(_location);
            }
        };

        boolean _fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean _coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!_fine && !_coarse) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(_request, locationCallback, getMainLooper());
    }

    private void DetenerActualizacionesUbicacion() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void ProcesarNuevaUbicacion(Location _nueva) {
        SessionManager.Touch();

        if (ultimaLocation != null) {
            float[] _result = new float[1];
            Location.distanceBetween(
                    ultimaLocation.getLatitude(),
                    ultimaLocation.getLongitude(),
                    _nueva.getLatitude(),
                    _nueva.getLongitude(),
                    _result
            );

            float _delta = _result[0];
            if (_delta >= MIN_DELTA_METERS) {
                distanciaMetros += _delta;
                ultimoMovimientoMs = System.currentTimeMillis();
                ActualizarTextoDistancia();
                AplicarEstadoBotones();
            }
        }

        ultimaLocation = _nueva;

        if (map != null) {
            LatLng _pos = new LatLng(_nueva.getLatitude(), _nueva.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(_pos));
        }
    }

    private void ActualizarTextoDistancia() {
        double _km = distanciaMetros / 1000.0;
        String _texto = String.format(Locale.getDefault(), "Distancia: %.2f km", _km);
        txtDistanciaSesion.setText(_texto);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            boolean _otorgado = false;
            for (int _res : grantResults) {
                if (_res == PackageManager.PERMISSION_GRANTED) {
                    _otorgado = true;
                    break;
                }
            }
            if (_otorgado) {
                HabilitarUbicacionMapa();
            } else {
                Toast.makeText(this, "Permiso de ubicación requerido", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DetenerActualizacionesUbicacion();
        DetenerWatchdogMovimiento();
    }
}