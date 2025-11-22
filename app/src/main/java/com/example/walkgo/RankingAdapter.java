package com.example.walkgo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.api.walkgo.models.RankingEntry;

import java.util.List;
import java.util.Locale;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private final Context context;
    private final List<RankingEntry> ranking;

    public RankingAdapter(Context _context, List<RankingEntry> _ranking) {
        this.context = _context;
        this.ranking = _ranking;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(context).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(_view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        RankingEntry _entry = ranking.get(position);

        int _posicion = _entry.GetPosicion() != null ? _entry.GetPosicion() : position + 1;
        String _usuario = _entry.GetUsuario() != null ? _entry.GetUsuario() : "Usuario";
        Integer _rango = _entry.GetRangoSemanal() != null ? _entry.GetRangoSemanal() : 0;
        Double _totalKm = _entry.GetTotalDistanciaKm() != null ? _entry.GetTotalDistanciaKm() : 0.0;

        holder.txtPosicion.setText("#" + _posicion);
        holder.txtNombre.setText(_usuario);
        String _detalle = "Km semana: " + _rango + "   •   Km totales: " + String.format(Locale.getDefault(), "%.2f", _totalKm);
        holder.txtDetalle.setText(_detalle);

        if (_posicion == 1) {
            holder.txtPosicion.setTextColor(Color.parseColor("#FFD600"));
            holder.txtNombre.setTextSize(18f);
        } else if (_posicion == 2) {
            holder.txtPosicion.setTextColor(Color.parseColor("#B0BEC5"));
            holder.txtNombre.setTextSize(17f);
        } else if (_posicion == 3) {
            holder.txtPosicion.setTextColor(Color.parseColor("#FF8F00"));
            holder.txtNombre.setTextSize(17f);
        } else {
            holder.txtPosicion.setTextColor(Color.parseColor("#212121"));
            holder.txtNombre.setTextSize(16f);
        }
    }

    @Override
    public int getItemCount() {
        return ranking.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {

        TextView txtPosicion;
        ImageView imgAvatar;
        TextView txtNombre;
        TextView txtDetalle;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPosicion = itemView.findViewById(R.id.txtPosicionRanking);
            imgAvatar = itemView.findViewById(R.id.imgAvatarRanking);
            txtNombre = itemView.findViewById(R.id.txtNombreRanking);
            txtDetalle = itemView.findViewById(R.id.txtDetalleRanking);
        }
    }
}