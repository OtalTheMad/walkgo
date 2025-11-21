package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.api.walkgo.RankingAPI;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.models.RankingEntry;
import com.example.walkgo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RankingActivity extends AppCompatActivity {

    private TextView txtPosicionUsuario;
    private TextView txtKmSemanaUsuario;
    private TextView txtKmTotalUsuario;
    private RecyclerView rvRanking;
    private RankingAdapter rankingAdapter;
    private final List<RankingEntry> listaRankingTop = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        InicializarVistas();
        InicializarLista();
        CargarRanking();
    }

    private void InicializarVistas() {
        txtPosicionUsuario = findViewById(R.id.txtPosicionUsuario);
        txtKmSemanaUsuario = findViewById(R.id.txtKmSemanaUsuario);
        txtKmTotalUsuario = findViewById(R.id.txtKmTotalUsuario);
        rvRanking = findViewById(R.id.rvRanking);
    }

    private void InicializarLista() {
        rvRanking.setLayoutManager(new LinearLayoutManager(this));
        rankingAdapter = new RankingAdapter(this, listaRankingTop);
        rvRanking.setAdapter(rankingAdapter);
    }

    private Integer GetLoggedUserId() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", MODE_PRIVATE);
        int _id = _prefs.getInt("id_usuario", -1);
        return _id <= 0 ? null : _id;
    }

    private void CargarRanking() {
        Integer _idUsuario = GetLoggedUserId();
        if (_idUsuario == null) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }
        RetrofitClient.Init(getApplicationContext());
        Retrofit _retrofit = RetrofitClient.GetInstance();
        RankingAPI _api = _retrofit.create(RankingAPI.class);
        Call<List<RankingEntry>> _call = _api.GetRankingSemana();
        _call.enqueue(new Callback<List<RankingEntry>>() {
            @Override
            public void onResponse(Call<List<RankingEntry>> call, Response<List<RankingEntry>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RankingActivity.this, "Error al cargar ranking", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<RankingEntry> _listaCompleta = response.body();
                listaRankingTop.clear();
                RankingEntry _usuarioActual = null;
                for (RankingEntry _entry : _listaCompleta) {
                    if (_entry.GetUserId() != null && _entry.GetUserId().equals(_idUsuario)) {
                        _usuarioActual = _entry;
                    }
                    if (_entry.GetPosicion() != null && _entry.GetPosicion() <= 10) {
                        listaRankingTop.add(_entry);
                    }
                }
                ActualizarResumenUsuario(_usuarioActual);
                rankingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<RankingEntry>> call, Throwable t) {
                Toast.makeText(RankingActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarResumenUsuario(RankingEntry _usuarioActual) {
        if (_usuarioActual == null) {
            txtPosicionUsuario.setText("Posición: -");
            txtKmSemanaUsuario.setText("Km esta semana: 0");
            txtKmTotalUsuario.setText("Km totales: 0");
            return;
        }
        Integer _posicion = _usuarioActual.GetPosicion();
        Integer _rangoSemanal = _usuarioActual.GetRangoSemanal();
        Double _totalKm = _usuarioActual.GetTotalDistanciaKm();
        if (_posicion == null) {
            _posicion = 0;
        }
        if (_rangoSemanal == null) {
            _rangoSemanal = 0;
        }
        if (_totalKm == null) {
            _totalKm = 0.0;
        }
        txtPosicionUsuario.setText("Posición: " + _posicion);
        txtKmSemanaUsuario.setText("Km esta semana: " + _rangoSemanal);
        txtKmTotalUsuario.setText("Km totales: " + String.format(Locale.getDefault(), "%.2f", _totalKm));
    }
}