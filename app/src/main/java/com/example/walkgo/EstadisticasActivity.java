package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.api.walkgo.RankingAPI;
import com.api.walkgo.RecorridoAPI;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.UsuarioService;
import com.api.walkgo.models.RankingEntry;
import com.api.walkgo.models.Recorrido;
import com.api.walkgo.models.Usuario;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EstadisticasActivity extends AppCompatActivity {

    private TextView txtTotalKmStats;
    private TextView txtTotalPasosStats;
    private TextView txtPosicionRankingStats;
    private TextView txtTotalRecorridosStats;
    private BarChart chartRecorridosSemana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        txtTotalKmStats = findViewById(R.id.txtTotalKmStats);
        txtTotalPasosStats = findViewById(R.id.txtTotalPasosStats);
        txtPosicionRankingStats = findViewById(R.id.txtPosicionRankingStats);
        txtTotalRecorridosStats = findViewById(R.id.txtTotalRecorridosStats);
        chartRecorridosSemana = findViewById(R.id.chartRecorridosSemana);

        Button _btnBack = findViewById(R.id.btnBack);
        _btnBack.setOnClickListener(v -> finish());

        RetrofitClient.Init(getApplicationContext());

        Integer _idUsuario = GetLoggedUserId();
        if (_idUsuario == null) {
            IrALogin();
            return;
        }

        ConfigurarGraficoBase();

        CargarResumenGlobal(_idUsuario);
        CargarPosicionRanking(_idUsuario);
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

    private void CargarPosicionRanking(int _idUsuario) {
        txtPosicionRankingStats.setText("Posición: -");

        Retrofit _retrofit = RetrofitClient.GetInstance();
        RankingAPI _api = _retrofit.create(RankingAPI.class);

        _api.GetRanking().enqueue(new Callback<List<RankingEntry>>() {
            @Override
            public void onResponse(Call<List<RankingEntry>> call, Response<List<RankingEntry>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    txtPosicionRankingStats.setText("Posición: -");
                    return;
                }

                List<RankingEntry> _list = response.body();
                RankingEntry _mi = null;

                for (RankingEntry _e : _list) {
                    if (_e != null && _e.GetIdUsuario() != null && _e.GetIdUsuario() == _idUsuario) {
                        _mi = _e;
                        break;
                    }
                }

                if (_mi == null || _mi.GetPosicion() == null || _mi.GetPosicion() <= 0) {
                    txtPosicionRankingStats.setText("Posición: -");
                    return;
                }

                int _pos = _mi.GetPosicion();
                if (_pos == 1) txtPosicionRankingStats.setText("Posición: 🥇 #1");
                else if (_pos == 2) txtPosicionRankingStats.setText("Posición: 🥈 #2");
                else if (_pos == 3) txtPosicionRankingStats.setText("Posición: 🥉 #3");
                else txtPosicionRankingStats.setText("Posición: #" + _pos);
            }

            @Override
            public void onFailure(Call<List<RankingEntry>> call, Throwable t) {
                txtPosicionRankingStats.setText("Posición: -");
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
                    txtTotalRecorridosStats.setText("Recorridos: 0");
                    ConfigurarGraficoVacio();
                    return;
                }

                List<Recorrido> _lista = response.body();
                if (_lista.isEmpty()) {
                    txtTotalRecorridosStats.setText("Recorridos: 0");
                    ConfigurarGraficoVacio();
                    return;
                }

                txtTotalRecorridosStats.setText("Recorridos: " + _lista.size());

                List<String> _labels = new ArrayList<>();
                List<BarEntry> _entries = new ArrayList<>();

                int _index = 0;
                for (Recorrido _recorrido : _lista) {
                    Double _distancia = _recorrido.GetDistanciaKm();
                    if (_distancia == null) {
                        _distancia = 0.0;
                    }
                    _entries.add(new BarEntry(_index, _distancia.floatValue()));

                    String _fecha = _recorrido.GetFecha();
                    _labels.add(FormatLabelFromFecha(_fecha, _index + 1));

                    _index++;
                }

                BarDataSet _dataSet = new BarDataSet(_entries, "Km por recorrido");
                _dataSet.setColor(ContextCompat.getColor(EstadisticasActivity.this, R.color.walkgo_primary));
                _dataSet.setHighLightAlpha(120);
                _dataSet.setValueTextSize(13f);
                _dataSet.setValueTextColor(ContextCompat.getColor(EstadisticasActivity.this, R.color.walkgo_on_surface));

                BarData _data = new BarData(_dataSet);
                _data.setBarWidth(0.55f);
                _data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getBarLabel(BarEntry barEntry) {
                        return String.format(Locale.getDefault(), "%.2f", barEntry.getY());
                    }
                });

                ConfigurarEjesConLabels(_labels);

                RecorridoMarkerView _marker = new RecorridoMarkerView(EstadisticasActivity.this, _labels);
                chartRecorridosSemana.setMarker(_marker);
                chartRecorridosSemana.setHighlightFullBarEnabled(true);
                chartRecorridosSemana.setHighlightPerTapEnabled(true);

                chartRecorridosSemana.setData(_data);
                chartRecorridosSemana.animateY(650);
                chartRecorridosSemana.invalidate();
            }

            @Override
            public void onFailure(Call<List<Recorrido>> call, Throwable t) {
                txtTotalRecorridosStats.setText("Recorridos: 0");
                ConfigurarGraficoVacio();
            }
        });
    }

    private void ConfigurarGraficoBase() {
        chartRecorridosSemana.getDescription().setEnabled(false);
        chartRecorridosSemana.setDrawGridBackground(false);
        chartRecorridosSemana.setDrawBarShadow(false);
        chartRecorridosSemana.setPinchZoom(true);
        chartRecorridosSemana.setScaleEnabled(false);
        chartRecorridosSemana.setExtraOffsets(12f, 10f, 12f, 10f);

        chartRecorridosSemana.setNoDataText("No hay recorridos para mostrar");

        android.graphics.Paint _infoPaint = chartRecorridosSemana.getPaint(BarChart.PAINT_INFO);
        _infoPaint.setColor(ContextCompat.getColor(this, R.color.walkgo_on_surface));
        _infoPaint.setTextSize(42f);

        Legend _legend = chartRecorridosSemana.getLegend();
        _legend.setEnabled(true);
        _legend.setTextSize(13f);
        _legend.setTextColor(ContextCompat.getColor(this, R.color.walkgo_on_surface));
        _legend.setForm(Legend.LegendForm.CIRCLE);

        YAxis _right = chartRecorridosSemana.getAxisRight();
        _right.setEnabled(false);

        YAxis _left = chartRecorridosSemana.getAxisLeft();
        _left.setTextSize(13f);
        _left.setTextColor(ContextCompat.getColor(this, R.color.walkgo_on_surface));
        _left.setAxisLineColor(ContextCompat.getColor(this, R.color.walkgo_on_surface));
        _left.setGridColor(ContextCompat.getColor(this, R.color.walkgo_on_surface));
        _left.setGridLineWidth(0.6f);
        _left.setAxisMinimum(0f);

        XAxis _x = chartRecorridosSemana.getXAxis();
        _x.setPosition(XAxis.XAxisPosition.BOTTOM);
        _x.setTextSize(12.5f);
        _x.setTextColor(ContextCompat.getColor(this, R.color.walkgo_on_surface));
        _x.setAxisLineColor(ContextCompat.getColor(this, R.color.walkgo_on_surface));
        _x.setDrawGridLines(false);
        _x.setGranularity(1f);
        _x.setLabelRotationAngle(-20f);
    }

    private void ConfigurarEjesConLabels(List<String> _labels) {
        XAxis _x = chartRecorridosSemana.getXAxis();
        _x.setLabelCount(Math.min(_labels.size(), 7), false);
        _x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int _i = Math.round(value);
                if (_i < 0 || _i >= _labels.size()) {
                    return "";
                }
                return _labels.get(_i);
            }
        });
    }

    private String FormatLabelFromFecha(String _fecha, int _fallbackIndex) {
        if (_fecha == null || _fecha.trim().isEmpty()) {
            return "R" + _fallbackIndex;
        }
        String _f = _fecha.trim();
        int _t = _f.indexOf("T");
        String _date = _t > 0 ? _f.substring(0, _t) : _f;

        if (_date.length() >= 10 && _date.charAt(4) == '-' && _date.charAt(7) == '-') {
            String _mm = _date.substring(5, 7);
            String _dd = _date.substring(8, 10);
            return _dd + "/" + _mm;
        }

        return "R" + _fallbackIndex;
    }

    private void ConfigurarGraficoVacio() {
        List<BarEntry> _entries = new ArrayList<>();
        _entries.add(new BarEntry(0f, 0f));

        BarDataSet _dataSet = new BarDataSet(_entries, "Sin datos");
        _dataSet.setColor(ContextCompat.getColor(this, R.color.walkgo_primary));
        _dataSet.setValueTextSize(13f);
        _dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.walkgo_on_surface));

        BarData _data = new BarData(_dataSet);
        _data.setBarWidth(0.55f);

        chartRecorridosSemana.setData(_data);
        chartRecorridosSemana.invalidate();
    }
}