package com.example.walkgo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.api.walkgo.RankingAPI;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.models.RankingEntry;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView rvRanking;
    private TextView txtPosicionUsuario;
    private TextView txtKmSemanaUsuario;
    private TextView txtKmTotalUsuario;

    private RankingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rvRanking = findViewById(R.id.rvRanking);
        txtPosicionUsuario = findViewById(R.id.txtPosicionUsuario);
        txtKmSemanaUsuario = findViewById(R.id.txtKmSemanaUsuario);
        txtKmTotalUsuario = findViewById(R.id.txtKmTotalUsuario);

        adapter = new RankingAdapter();
        rvRanking.setLayoutManager(new LinearLayoutManager(this));
        rvRanking.setAdapter(adapter);

        RetrofitClient.Init(getApplicationContext());
        CargarRanking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RetrofitClient.Init(getApplicationContext());
        CargarRanking();
    }

    private Integer GetLoggedUserId() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        int _id = _prefs.getInt("id_usuario", -1);
        return _id <= 0 ? null : _id;
    }

    private void CargarRanking() {
        Integer _loggedId = GetLoggedUserId();
        if (_loggedId == null) {
            txtPosicionUsuario.setText("Posición: -");
            txtKmSemanaUsuario.setText("Km esta semana: 0");
            txtKmTotalUsuario.setText("Km totales: 0");
            adapter.SetItems(null);
            return;
        }

        Retrofit _retrofit = RetrofitClient.GetInstance();
        RankingAPI _api = _retrofit.create(RankingAPI.class);

        _api.GetRanking().enqueue(new Callback<List<RankingEntry>>() {
            @Override
            public void onResponse(Call<List<RankingEntry>> call, Response<List<RankingEntry>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RankingActivity.this, "No se pudo cargar el ranking", Toast.LENGTH_SHORT).show();
                    adapter.SetItems(null);
                    txtPosicionUsuario.setText("Posición: -");
                    txtKmSemanaUsuario.setText("Km esta semana: 0");
                    txtKmTotalUsuario.setText("Km totales: 0");
                    return;
                }

                List<RankingEntry> _list = response.body();
                adapter.SetItems(_list);

                RankingEntry _mi = null;
                for (RankingEntry _e : _list) {
                    if (_e != null && _e.GetIdUsuario() != null && _e.GetIdUsuario() == _loggedId) {
                        _mi = _e;
                        break;
                    }
                }

                if (_mi == null) {
                    txtPosicionUsuario.setText("Posición: -");
                    txtKmSemanaUsuario.setText("Km esta semana: 0");
                    txtKmTotalUsuario.setText("Km totales: 0");
                    return;
                }

                int _pos = _mi.GetPosicion() == null ? 0 : _mi.GetPosicion();
                double _kmTotal = _mi.GetTotalDistanciaKm() == null ? 0.0 : _mi.GetTotalDistanciaKm();

                txtPosicionUsuario.setText("Posición: " + (_pos <= 0 ? "-" : String.valueOf(_pos)));
                txtKmTotalUsuario.setText(String.format(Locale.getDefault(), "Km totales: %.2f", _kmTotal));

                txtKmSemanaUsuario.setText("Km esta semana: 0");
            }

            @Override
            public void onFailure(Call<List<RankingEntry>> call, Throwable t) {
                Toast.makeText(RankingActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}