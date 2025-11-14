package com.example.walkgo;

import java.util.List;
public class EstadisticaData {

    private String periodLabel; // Etiqueta del período (Ejemplo: "Semana Actual", "Mes Actual", "Totales")
    private double totalKilometers;
    private long totalActiveTimeSeconds; // Tiempo total invertido en actividades, en segundos
    private double dailyAverageKm; // Promedio de actividad por día en kilómetros
    private double bestMarkKm; // Valor máximo alcanzado en una sesión (Mejor marca)
    private double weeklyChangePercentage; // Variación de desempeño respecto al período anterior (Ejemplo: -5.0 o +12.5)
    private List<DataPoint> chartData; // Puntos de datos para el gráfico de progreso

    // Constructor vacío (Es necesario para que librerías como Gson o Retrofit puedan deserializar el JSON de tu API)
    public EstadisticaData() {}

    /**
     * Constructor completo para inicializar todos los datos de estadísticas.
     */
    public EstadisticaData(String periodLabel, double totalKilometers, long totalActiveTimeSeconds, double dailyAverageKm, double bestMarkKm, double weeklyChangePercentage, List<DataPoint> chartData) {
        this.periodLabel = periodLabel;
        this.totalKilometers = totalKilometers;
        this.totalActiveTimeSeconds = totalActiveTimeSeconds;
        this.dailyAverageKm = dailyAverageKm;
        this.bestMarkKm = bestMarkKm;
        this.weeklyChangePercentage = weeklyChangePercentage;
        this.chartData = chartData;
    }

    /**
     * Clase interna estática para definir los puntos individuales del gráfico.
     * Representa un par (etiqueta del eje X, valor del eje Y).
     */
    public static class DataPoint {
        private String label; // Etiqueta del eje X (ej: "Lun", "Mar", "Sem 1", "2024")
        private double value; // Valor para el eje Y (kilómetros recorridos para esa etiqueta)

        public DataPoint(String label, double value) {
            this.label = label;
            this.value = value;
        }

        // Getters para los puntos de datos
        public String getLabel() { return label; }
        public double getValue() { return value; }
    }

    // Getters para acceder a los datos de la clase principal
    public String getPeriodLabel() { return periodLabel; }
    public double getTotalKilometers() { return totalKilometers; }
    public long getTotalActiveTimeSeconds() { return totalActiveTimeSeconds; }
    public double getDailyAverageKm() { return dailyAverageKm; }
    public double getBestMarkKm() { return bestMarkKm; }
    public double getWeeklyChangePercentage() { return weeklyChangePercentage; }
    public List<DataPoint> getChartData() { return chartData; }
}
