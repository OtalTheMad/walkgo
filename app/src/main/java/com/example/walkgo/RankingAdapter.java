package com.example.walkgo;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.api.walkgo.models.RankingEntry;
import com.example.walkgo.R;

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

        int _pos = _e.GetPosicion() != null ? _e.GetPosicion() : (position + 1);
        String _usuario = _e.GetUsuario() != null && !_e.GetUsuario().trim().isEmpty() ? _e.GetUsuario() : "Usuario";
        double _km = _e.GetTotalDistanciaKm() != null ? _e.GetTotalDistanciaKm() : 0.0;

        ApplyDefaultStyle(holder);

        holder.txtNombreRanking.setText(_usuario);
        holder.txtDetalleRanking.setText(String.format(Locale.getDefault(), "Km totales: %.2f", _km));

        if (_pos == 1) {
            holder.txtPosicionRanking.setText("🥇 #1");
            ApplyMedalStyle(holder, R.color.ranking_gold_bg, R.color.ranking_gold_text, 8, 1.01f, 1.05f);
            return;
        }

        if (_pos == 2) {
            holder.txtPosicionRanking.setText("🥈 #2");
            ApplyMedalStyle(holder, R.color.ranking_silver_bg, R.color.ranking_silver_text, 6, 1.00f, 1.02f);
            return;
        }

        if (_pos == 3) {
            holder.txtPosicionRanking.setText("🥉 #3");
            ApplyMedalStyle(holder, R.color.ranking_bronze_bg, R.color.ranking_bronze_text, 5, 1.00f, 1.02f);
            return;
        }

        holder.txtPosicionRanking.setText("#" + _pos);
    }

    private void ApplyDefaultStyle(VH holder) {
        Context _ctx = holder.itemView.getContext();

        holder.card.setCardElevation(DpToPx(_ctx, 2));
        holder.card.setCardBackgroundColor(ContextCompat.getColor(_ctx, R.color.walkgo_surface));

        holder.txtPosicionRanking.setTypeface(Typeface.DEFAULT_BOLD);
        holder.txtPosicionRanking.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        holder.txtNombreRanking.setTypeface(Typeface.DEFAULT_BOLD);

        holder.txtPosicionRanking.setTextColor(ContextCompat.getColor(_ctx, R.color.walkgo_primary));

        holder.itemView.setScaleX(1.0f);
        holder.itemView.setScaleY(1.0f);
        holder.imgAvatarRanking.setScaleX(1.0f);
        holder.imgAvatarRanking.setScaleY(1.0f);
    }

    private void ApplyMedalStyle(VH holder, int _bgColorRes, int _textColorRes, float _elevationDp, float _cardScale, float _avatarScale) {
        Context _ctx = holder.itemView.getContext();

        holder.card.setCardElevation(DpToPx(_ctx, _elevationDp));
        holder.card.setCardBackgroundColor(ContextCompat.getColor(_ctx, _bgColorRes));
        holder.txtPosicionRanking.setTextColor(ContextCompat.getColor(_ctx, _textColorRes));

        holder.itemView.setScaleX(_cardScale);
        holder.itemView.setScaleY(_cardScale);
        holder.imgAvatarRanking.setScaleX(_avatarScale);
        holder.imgAvatarRanking.setScaleY(_avatarScale);
    }

    private float DpToPx(Context _ctx, float _dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _dp, _ctx.getResources().getDisplayMetrics());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        androidx.cardview.widget.CardView card;
        TextView txtPosicionRanking;
        ImageView imgAvatarRanking;
        TextView txtNombreRanking;
        TextView txtDetalleRanking;

        public VH(@NonNull View itemView) {
            super(itemView);
            card = (androidx.cardview.widget.CardView) itemView;
            txtPosicionRanking = itemView.findViewById(R.id.txtPosicionRanking);
            imgAvatarRanking = itemView.findViewById(R.id.imgAvatarRanking);
            txtNombreRanking = itemView.findViewById(R.id.txtNombreRanking);
            txtDetalleRanking = itemView.findViewById(R.id.txtDetalleRanking);
        }
    }
}