package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.api.walkgo.RecorridoAPI;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.UsuarioService;
import com.api.walkgo.models.Recorrido;
import com.api.walkgo.models.Usuario;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EstadisticasActivity extends AppCompatActivity {

    private TextView txtTotalKmStats;
    private TextView txtTotalPasosStats;
    private BarChart chartRecorridosSemana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        txtTotalKmStats = findViewById(R.id.txtTotalKmStats);
        txtTotalPasosStats = findViewById(R.id.txtTotalPasosStats);
        chartRecorridosSemana = findViewById(R.id.chartRecorridosSemana);

        Button _btnBack = findViewById(R.id.btnBack);
        _btnBack.setOnClickListener(v -> finish());

        RetrofitClient.Init(getApplicationContext());

        Integer _idUsuario = GetLoggedUserId();
        if (_idUsuario == null) {
            IrALogin();
            return;
        }

        CargarResumenGlobal(_idUsuario);
        CargarRecorridosSemana(_idUsuario);
    }

    private void IrALogin() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor _editor = _prefs.edit();
        _editor.clear();
        _editor.apply();

        android.content.Intent _intent = new android.content.Intent(this, com.api.walkgo.LoginActivity.class);
        _intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(_intent);
        finish();
    }

    private Integer GetLoggedUserId() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", MODE_PRIVATE);
        int _id = _prefs.getInt("id_usuario", -1);
        return _id == -1 ? null : _id;
    }

    private void CargarResumenGlobal(int _idUsuario) {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        UsuarioService _service = _retrofit.create(UsuarioService.class);
        Call<Usuario> _call = _service.GetUsuario(_idUsuario);
        _call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    txtTotalKmStats.setText("0.00");
                    txtTotalPasosStats.setText("0");
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
                txtTotalKmStats.setText(String.format(Locale.getDefault(), "%.2f", _totalKm));
                txtTotalPasosStats.setText(String.valueOf(_totalPasos));
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                txtTotalKmStats.setText("0.00");
                txtTotalPasosStats.setText("0");
            }
        });
    }

    private void CargarRecorridosSemana(int _idUsuario) {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        RecorridoAPI _service = _retrofit.create(RecorridoAPI.class);
        Call<List<Recorrido>> _call = _service.GetRecorridosSemana(_idUsuario);
        _call.enqueue(new Callback<List<Recorrido>>() {
            @Override
            public void onResponse(Call<List<Recorrido>> call, Response<List<Recorrido>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    ConfigurarGraficoVacio();
                    return;
                }
                List<Recorrido> _lista = response.body();
                if (_lista.isEmpty()) {
                    ConfigurarGraficoVacio();
                    return;
                }
                List<BarEntry> _entries = new ArrayList<>();
                int _index = 0;
                for (Recorrido _recorrido : _lista) {
                    Double _distancia = _recorrido.GetDistanciaKm();
                    if (_distancia == null) {
                        _distancia = 0.0;
                    }
                    _entries.add(new BarEntry(_index, _distancia.floatValue()));
                    _index++;
                }
                BarDataSet _dataSet = new BarDataSet(_entries, "Km por recorrido");
                BarData _data = new BarData(_dataSet);
                chartRecorridosSemana.setData(_data);
                chartRecorridosSemana.getDescription().setEnabled(false);
                chartRecorridosSemana.invalidate();
            }

            @Override
            public void onFailure(Call<List<Recorrido>> call, Throwable t) {
                ConfigurarGraficoVacio();
            }
        });
    }

    private void ConfigurarGraficoVacio() {
        List<BarEntry> _entries = new ArrayList<>();
        _entries.add(new BarEntry(0f, 0f));
        BarDataSet _dataSet = new BarDataSet(_entries, "Sin datos");
        BarData _data = new BarData(_dataSet);
        chartRecorridosSemana.setData(_data);
        chartRecorridosSemana.getDescription().setEnabled(false);
        chartRecorridosSemana.invalidate();
    }
}