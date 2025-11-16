package com.example.walkgo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walkgo.R;
import com.example.walkgo.PerfilActivity;
import com.example.walkgo.models.Runner;

import java.text.DecimalFormat;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private final Context context;
    private List<Runner> runnerList; // Ahora no es final para poder actualizarse
    private final String currentUsername;
    private final DecimalFormat kmFormat = new DecimalFormat("0.0 km");

    public RankingAdapter(Context context, List<Runner> runnerList, String currentUsername) {
        this.context = context;
        this.runnerList = runnerList;
        this.currentUsername = currentUsername;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Runner runner = runnerList.get(position);

        // 1. Configurar datos
        holder.tvPosition.setText(String.valueOf(runner.getPosition()));
        holder.tvUsername.setText(runner.getUsername());
        holder.tvKilometers.setText(kmFormat.format(runner.getKilometers()));
        holder.tvMovement.setText(runner.getMovement());

        // 2. Lógica de Resaltado (Estilo)
        // Estilo predeterminado: color negro
        int defaultColor = ContextCompat.getColor(context, android.R.color.black);
        holder.tvUsername.setTextColor(defaultColor);
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);

        if (runner.isCurrentUser()) {
            // Resaltar la fila completa para el usuario actual (color green_700)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green_700));
            holder.tvUsername.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }

        if (runner.isFriend() && !runner.isCurrentUser()) {
            // Resaltar solo el username del amigo con green_800 (o purple_500, según colors.xml)
            holder.tvUsername.setTextColor(ContextCompat.getColor(context, R.color.green_800));
        }

        // 3. Configurar Botón de Amigo/Click
        if (runner.isCurrentUser()) {
            holder.ivAddFriend.setVisibility(View.GONE);
        } else {
            // Asegúrate de que ic_add_friend exista en res/drawable
            holder.ivAddFriend.setImageResource(R.drawable.ic_add_friend);
            holder.ivAddFriend.setVisibility(View.VISIBLE);
        }

        // 4. Listener para el clic en la fila (navegar al perfil)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PerfilActivity.class);
            // Pasar el nombre de usuario
            intent.putExtra("username", runner.getUsername());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return runnerList.size();
    }

    /**
     * Método público para actualizar la lista de datos del adaptador (usado para filtros Global/Amigos).
     * @param newRunners La nueva lista de corredores filtrados.
     */
    public void updateData(List<Runner> newRunners) {
        this.runnerList = newRunners;
        // Notificar al RecyclerView que los datos han cambiado
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPosition;
        final TextView tvUsername;
        final TextView tvKilometers;
        final TextView tvMovement;
        final ImageView ivAddFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvKilometers = itemView.findViewById(R.id.tv_kilometers);
            tvMovement = itemView.findViewById(R.id.tv_movement);
            ivAddFriend = itemView.findViewById(R.id.iv_add_friend);
        }
    }


}
