package com.example.walkgo;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.api.walkgo.AmigosAPI;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.models.ApiCreateAmigo;
import com.api.walkgo.models.ApiUpdateAmigo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        holder.tvEstado.setText(amigo.getEstado());
        holder.tvNombre.setText("Usuario " + amigo.getIdUsuarioAmigo());
        holder.tvUsuario.setText("ID: " + amigo.getIdUsuarioAmigo());

        switch (amigo.getEstado()) {

            case "activo":
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Eliminar amigo");
                holder.layoutSolicitud.setVisibility(View.GONE);

                holder.btnAccion.setOnClickListener(v -> {
                    UpdateEstado(amigo, "no_amigo", position);
                });
                break;

            case "no_amigo":
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Agregar amigo");
                holder.layoutSolicitud.setVisibility(View.GONE);

                holder.btnAccion.setOnClickListener(v -> {
                    CreateSolicitud(amigo, position);
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

                holder.btnAceptar.setOnClickListener(v ->
                        UpdateEstado(amigo, "activo", position)
                );

                holder.btnRechazar.setOnClickListener(v ->
                        UpdateEstado(amigo, "no_amigo", position)
                );

                break;
        }
    }

    @Override
    public int getItemCount() {
        return amigos.size();
    }

    private void CreateSolicitud(Amigo amigo, int position) {

        SharedPreferences prefs = context.getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        Integer myId = prefs.getInt("user_id", -1);

        AmigosAPI api = RetrofitClient.GetInstance().create(AmigosAPI.class);

        ApiCreateAmigo req = new ApiCreateAmigo(myId, amigo.getIdUsuarioAmigo());

        api.CreateAmigo(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call call, Response response) {
                amigo.setEstado("solicitud_enviada");
                notifyItemChanged(position);
                Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context, "Error enviando solicitud", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateEstado(Amigo amigo, String nuevoEstado, int position) {

        AmigosAPI api = RetrofitClient.GetInstance().create(AmigosAPI.class);

        ApiUpdateAmigo req = new ApiUpdateAmigo(
                amigo.getIdUsuario(),
                amigo.getIdUsuarioAmigo(),
                nuevoEstado
        );

        api.UpdateAmigo(amigo.getIdAmigo(), req).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call call, Response response) {
                amigo.setEstado(nuevoEstado);
                notifyItemChanged(position);
                Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context, "Error actualizando estado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class AmigoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUsuario, tvEstado;
        Button btnAccion, btnAceptar, btnRechazar;
        LinearLayout layoutSolicitud;

        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);

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