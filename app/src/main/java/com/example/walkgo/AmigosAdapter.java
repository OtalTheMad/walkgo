package com.example.walkgo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder> {

    private Context context;
    private List<Amigo> amigos;

    public AmigosAdapter(Context context, List<Amigo> amigos) {
        this.context = context;
        this.amigos = amigos;
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_amigo, parent, false);
        return new AmigoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmigoViewHolder holder, int position) {
        Amigo amigo = amigos.get(position);

        // Datos
        holder.tvNombre.setText(amigo.getNombre());
        holder.tvUsuario.setText(amigo.getUsuario());
        holder.tvEstado.setText(amigo.getEstado());
        holder.imgPerfil.setImageResource(amigo.getFotoPerfil());

        // Botones según estado
        switch (amigo.getEstado()) {
            case "activo":
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Eliminar");
                holder.layoutSolicitud.setVisibility(View.GONE);
                holder.btnAccion.setOnClickListener(v -> {
                    amigo.setEstado("no_amigo");
                    Toast.makeText(context, "Amigo eliminado", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });
                break;

            case "no_amigo":
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Agregar");
                holder.layoutSolicitud.setVisibility(View.GONE);
                holder.btnAccion.setOnClickListener(v -> {
                    amigo.setEstado("solicitud_enviada");
                    Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });
                break;

            case "solicitud_enviada":
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Solicitud enviada");
                holder.btnAccion.setEnabled(false);
                holder.layoutSolicitud.setVisibility(View.GONE);
                break;

            case "solicitud_recibida":
                holder.btnAccion.setVisibility(View.GONE);
                holder.layoutSolicitud.setVisibility(View.VISIBLE);
                holder.btnAceptar.setOnClickListener(v -> {
                    amigo.setEstado("activo");
                    Toast.makeText(context, "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });
                holder.btnRechazar.setOnClickListener(v -> {
                    amigo.setEstado("no_amigo");
                    Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                });
                break;
        }

        // Click en todo el item → abrir PerfilActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PerfilActivity.class);
            intent.putExtra("nombre", amigo.getNombre());
            intent.putExtra("usuario", amigo.getUsuario());
            intent.putExtra("foto", amigo.getFotoPerfil());
            intent.putExtra("estado", amigo.getEstado());
            intent.putExtra("km", amigo.getKmRecorridos());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return amigos.size();
    }

    public static class AmigoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPerfil;
        TextView tvNombre, tvUsuario, tvEstado;
        Button btnAccion, btnAceptar, btnRechazar;
        LinearLayout layoutSolicitud;

        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPerfil = itemView.findViewById(R.id.imgPerfilAmigo);
            tvNombre = itemView.findViewById(R.id.tvNombreAmigo);
            tvUsuario = itemView.findViewById(R.id.tvUsuarioAmigo);
            tvEstado = itemView.findViewById(R.id.tvEstadoAmigo);
            btnAccion = itemView.findViewById(R.id.btnAccionAmigo);
            layoutSolicitud = itemView.findViewById(R.id.layoutSolicitud);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}