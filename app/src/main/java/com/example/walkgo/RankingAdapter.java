package com.example.walkgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.api.walkgo.models.RankingEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.VH> {

    private final List<RankingEntry> items = new ArrayList<>();

    public void SetItems(List<RankingEntry> _items) {
        items.clear();
        if (_items != null) {
            items.addAll(_items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new VH(_view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        RankingEntry _e = items.get(position);

        int _pos = _e.GetPosicion() == null ? (position + 1) : _e.GetPosicion();
        double _km = _e.GetTotalDistanciaKm() == null ? 0.0 : _e.GetTotalDistanciaKm();

        holder.txtRankingPos.setText("Top " + _pos);
        holder.txtRankingKm.setText(String.format(Locale.getDefault(), "%.2f km", _km));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView txtRankingPos;
        TextView txtRankingKm;

        public VH(@NonNull View itemView) {
            super(itemView);
            txtRankingPos = itemView.findViewById(R.id.txtPosicionRanking);
            txtRankingKm = itemView.findViewById(R.id.txtDetalleRanking);
        }
    }
}