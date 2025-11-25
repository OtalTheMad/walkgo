package com.example.walkgo;

import android.content.Context;
import android.widget.TextView;

import com.example.walkgo.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;
import java.util.Locale;

public class RecorridoMarkerView extends MarkerView {

    private final TextView txtMarkerTitulo;
    private final TextView txtMarkerFecha;
    private final TextView txtMarkerKm;

    private final List<String> labels;

    public RecorridoMarkerView(Context context, List<String> _labels) {
        super(context, R.layout.chart_marker_recorrido);
        labels = _labels;
        txtMarkerTitulo = findViewById(R.id.txtMarkerTitulo);
        txtMarkerFecha = findViewById(R.id.txtMarkerFecha);
        txtMarkerKm = findViewById(R.id.txtMarkerKm);
    }

    @Override
    public void refreshContent(com.github.mikephil.charting.data.Entry e, Highlight highlight) {
        if (e instanceof BarEntry) {
            BarEntry _be = (BarEntry) e;
            int _index = (int) _be.getX();

            String _label = "Recorrido";
            if (labels != null && _index >= 0 && _index < labels.size()) {
                _label = labels.get(_index);
            }

            txtMarkerTitulo.setText("Recorrido #" + (_index + 1));
            txtMarkerFecha.setText("Fecha: " + _label);
            txtMarkerKm.setText(String.format(Locale.getDefault(), "%.2f km", _be.getY()));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 16f);
    }
}