package com.example.walkgo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EstadisticaActivity extends AppCompatActivity {

    private TextView tvTotalKm, tvActiveTime, tvDailyAverage, tvBestMark, tvComparativeChange, tvNoData;
    private BarChart chartProgress;
    private TabLayout tabLayout;

    // Formateadores de texto para kilómetros y porcentajes
    private final DecimalFormat kmFormat = new DecimalFormat("0.0 km");
    private final DecimalFormat percentFormat = new DecimalFormat("0.0%");

    // Simulación de datos (En una aplicación real, estos vendrían de una REST API)
    private EstadisticaData weeklyData;
    private EstadisticaData monthlyData;
    private EstadisticaData totalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establece el layout de la actividad
        setContentView(R.layout.activity_estadistica);

        // Inicializar vistas
        initializeViews();

        // Cargar los datos simulados
        loadMockData();

        // Configurar el listener para el cambio de pestaña (período)
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Llama a la función de actualización con el índice de la pestaña seleccionada
                updateUIForPeriod(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { /* No se requiere acción al deseleccionar */ }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { /* No se requiere acción al reseleccionar */ }
        });

        // Cargar las estadísticas iniciales (Semana Actual, índice 0)
        updateUIForPeriod(0);
    }

    /** Inicializa todas las vistas del layout y el gráfico. */
    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout_period);

        tvTotalKm = findViewById(R.id.tv_total_km);
        tvActiveTime = findViewById(R.id.tv_active_time);
        tvDailyAverage = findViewById(R.id.tv_daily_average);
        tvBestMark = findViewById(R.id.tv_best_mark);
        tvComparativeChange = findViewById(R.id.tv_comparative_change);
        tvNoData = findViewById(R.id.tv_no_data);

        chartProgress = findViewById(R.id.chart_progress);
        configureChartSettings();
    }

    /**
     * Configura la apariencia básica del gráfico de barras (MPAndroidChart).
     * Esto incluye deshabilitar la leyenda, la descripción y configurar los ejes.
     */
    private void configureChartSettings() {
        chartProgress.getDescription().setEnabled(false); // Quita la descripción
        chartProgress.getLegend().setEnabled(false); // Quita la leyenda
        chartProgress.setDrawGridBackground(false); // Quita el fondo de la cuadrícula
        chartProgress.setTouchEnabled(true);
        chartProgress.setDragEnabled(true);
        chartProgress.setScaleEnabled(false); // Deshabilitar zoom

        // Configurar Eje X (Etiquetas)
        XAxis xAxis = chartProgress.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Las etiquetas van abajo
        xAxis.setDrawGridLines(false); // No dibujar líneas de cuadrícula verticales
        xAxis.setGranularity(1f); // Mínimo intervalo entre valores
        xAxis.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Configurar Eje Y (Izquierdo)
        chartProgress.getAxisLeft().setDrawGridLines(true);
        chartProgress.getAxisLeft().setTextColor(getResources().getColor(android.R.color.darker_gray));
        chartProgress.getAxisLeft().setAxisMinimum(0f); // El gráfico siempre empieza en cero

        // Configurar Eje Y (Derecho): deshabilitado para un diseño más limpio
        chartProgress.getAxisRight().setEnabled(false);
    }

    /**
     * Simulación de carga de datos para los 3 períodos.
     * En una app real, aquí harías una llamada a tu REST API para obtener los datos.
     */
    private void loadMockData() {
        // Datos de la Semana Actual
        List<EstadisticaData.DataPoint> weeklyPoints = new ArrayList<>();
        weeklyPoints.add(new EstadisticaData.DataPoint("Lun", 1.5));
        weeklyPoints.add(new EstadisticaData.DataPoint("Mar", 0));
        weeklyPoints.add(new EstadisticaData.DataPoint("Mié", 3.2));
        weeklyPoints.add(new EstadisticaData.DataPoint("Jue", 2.0));
        weeklyPoints.add(new EstadisticaData.DataPoint("Vie", 4.5));
        weeklyPoints.add(new EstadisticaData.DataPoint("Sáb", 0));
        weeklyPoints.add(new EstadisticaData.DataPoint("Dom", 2.8));
        // Etiqueta, Km Totales, Tiempo Segundos, Promedio Diario, Mejor Marca, % Cambio, Puntos Gráfico
        weeklyData = new EstadisticaData("Semana Actual", 14.0, 7800, 2.0, 4.5, 12.5, weeklyPoints);

        // Datos del Mes Actual
        List<EstadisticaData.DataPoint> monthlyPoints = new ArrayList<>();
        monthlyPoints.add(new EstadisticaData.DataPoint("Sem 1", 10.1));
        monthlyPoints.add(new EstadisticaData.DataPoint("Sem 2", 14.0));
        monthlyPoints.add(new EstadisticaData.DataPoint("Sem 3", 11.8));
        monthlyData = new EstadisticaData("Mes Actual", 35.9, 18200, 1.2, 5.5, -5.0, monthlyPoints);

        // Datos Totales (Ejemplo para probar el estado "Sin datos de progresión")
        List<EstadisticaData.DataPoint> totalPoints = new ArrayList<>();
        totalPoints.add(new EstadisticaData.DataPoint("2024", 150.0));
        totalPoints.add(new EstadisticaData.DataPoint("2025", 80.0));
        totalData = new EstadisticaData("Totales Históricos", 230.0, 50000, 0, 8.0, 0, totalPoints);
    }

    /**
     * Actualiza la interfaz de usuario con los datos correspondientes al período seleccionado.
     * @param periodIndex 0: Semana, 1: Mes, 2: Total.
     */
    private void updateUIForPeriod(int periodIndex) {
        EstadisticaData data;

        switch (periodIndex) {
            case 0: // Semana
                data = weeklyData;
                break;
            case 1: // Mes
                data = monthlyData;
                break;
            case 2: // Total
                data = totalData;
                break;
            default:
                data = weeklyData;
        }

        // 1. Actualizar KPIs y manejar el estado de "No Datos"
        // Mostramos datos si los Km totales son > 0 O si estamos en la vista "Total" (incluso si los km son 0)
        if (data.getTotalKilometers() > 0 || periodIndex == 2) {
            tvNoData.setVisibility(View.GONE);
            chartProgress.setVisibility(View.VISIBLE);

            // Kilómetros recorridos
            tvTotalKm.setText(kmFormat.format(data.getTotalKilometers()));

            // Tiempo activo (formato H:MM o M:SS)
            tvActiveTime.setText(formatTime(data.getTotalActiveTimeSeconds()));

            // Promedio diario
            tvDailyAverage.setText(kmFormat.format(data.getDailyAverageKm()));

            // Mejores marcas
            tvBestMark.setText(kmFormat.format(data.getBestMarkKm()));

            // Comparativa semanal (Solo para períodos Semana/Mes)
            if (periodIndex == 2) {
                tvComparativeChange.setText("N/A");
                tvComparativeChange.setTextColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                double change = data.getWeeklyChangePercentage();
                // Formatea el porcentaje con signo (+)
                tvComparativeChange.setText((change > 0 ? "+" : "") + percentFormat.format(change / 100.0));

                // Colorización: verde para positivo, rojo para negativo
                int color = change >= 0 ?
                        getResources().getColor(android.R.color.holo_green_dark) :
                        getResources().getColor(android.R.color.holo_red_dark);
                tvComparativeChange.setTextColor(color);
            }

            // 2. Actualizar Gráfico
            if (data.getChartData() != null && !data.getChartData().isEmpty()) {
                loadChartData(data.getChartData());
            } else {
                // Mostrar el mensaje de No Datos si hay KPIs pero el gráfico está vacío
                tvNoData.setText("No hay datos de progresión detallados para " + data.getPeriodLabel() + ".");
                tvNoData.setVisibility(View.VISIBLE);
                chartProgress.setVisibility(View.INVISIBLE);
            }

        } else {
            // Caso de No Existencia de Datos (Km totales es 0 y no es la vista Total)
            tvNoData.setText("Aún no hay estadísticas disponibles en " + data.getPeriodLabel() + ".");
            tvNoData.setVisibility(View.VISIBLE);
            chartProgress.setVisibility(View.INVISIBLE);

            // Reiniciar KPIs a valores por defecto
            tvTotalKm.setText("0.0 km");
            tvActiveTime.setText("0h 0m");
            tvDailyAverage.setText("0.0 km/día");
            tvBestMark.setText("0.0 km");
            tvComparativeChange.setText("N/A");
            tvComparativeChange.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    /**
     * Carga los puntos de datos al gráfico de barras y lo refresca.
     * @param dataPoints Lista de DataPoint con etiquetas y valores.
     */
    private void loadChartData(List<EstadisticaData.DataPoint> dataPoints) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();

        for (int i = 0; i < dataPoints.size(); i++) {
            entries.add(new BarEntry(i, (float) dataPoints.get(i).getValue()));
            labels.add(dataPoints.get(i).getLabel());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Kilómetros");
        // Asegúrate de que R.color.design_default_color_primary exista o usa un color fijo
        int primaryColor = ContextCompat.getColor(this, R.color.green_700); // LÍNEA CORREGIDA
        dataSet.setColor(primaryColor);
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        dataSet.setDrawValues(true);

        // Formateador para mostrar el valor sobre la barra (ej: 4.5)
        dataSet.setValueFormatter(new ValueFormatter() {
            private final DecimalFormat format = new DecimalFormat("0.0");
            @Override
            public String getFormattedValue(float value) {
                return format.format(value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f); // Ancho de las barras

        // Actualiza las etiquetas del Eje X (Lun, Mar, Sem 1, etc.)
        chartProgress.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartProgress.getXAxis().setLabelCount(labels.size(), false);

        chartProgress.setData(barData);
        chartProgress.invalidate(); // Refresca el gráfico para mostrar los nuevos datos
        chartProgress.animateY(1000); // Animación vertical al cargar
    }

    /**
     * Convierte una cantidad de segundos a un formato de tiempo legible ("Hh MMm" o "MMm SSs").
     */
    private String formatTime(long totalSeconds) {
        if (totalSeconds < 60) {
            return totalSeconds + "s";
        }
        long hours = TimeUnit.SECONDS.toHours(totalSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) % 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%dh %02dm", hours, minutes); // Si hay horas (ej: 1h 30m)
        } else if (minutes > 0) {
            return String.format("%dm %02ds", minutes, seconds); // Si hay minutos (ej: 10m 15s)
        } else {
            return String.format("%ds", totalSeconds); // Por si acaso
        }
    }
}