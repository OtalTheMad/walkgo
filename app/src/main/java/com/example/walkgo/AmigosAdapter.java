package com.example.walkgo;

import android.content.Context;
import android.content.Intent;
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
import com.api.walkgo.PerfilActivity;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.UsuarioService;
import com.api.walkgo.models.ApiCreateAmigo;
import com.api.walkgo.models.ApiUpdateAmigo;
import com.api.walkgo.models.Usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder> {

    private final Context context;
    private final List<Amigo> amigos;
    private final Map<Integer, String> nombresCache = new HashMap<>();

    public AmigosAdapter(Context _context, List<Amigo> _amigos) {
        this.context = _context;
        this.amigos = _amigos;
        CargarNombresUsuarios();
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(context).inflate(R.layout.item_amigo, parent, false);
        return new AmigoViewHolder(_view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmigoViewHolder holder, int position) {

        Amigo _amigo = amigos.get(position);

        int _idUsuarioAmigo = _amigo.getIdUsuarioAmigo();

        String _nombre = nombresCache.get(_idUsuarioAmigo);
        if (_nombre == null || _nombre.isEmpty()) {
            holder.tvNombre.setText("Usuario " + _idUsuarioAmigo);
        } else {
            holder.tvNombre.setText(_nombre);
        }

        holder.layoutSolicitud.setVisibility(View.GONE);
        holder.btnAccion.setVisibility(View.VISIBLE);
        holder.btnAccion.setEnabled(true);

        String _estado = _amigo.getEstado();

        switch (_estado) {

            case "siguiendo":
                holder.btnAccion.setText("Dejar de seguir");
                holder.btnAccion.setOnClickListener(v -> {
                    UpdateEstado(_amigo, "no_seguido", position);
                });
                break;

            case "no_seguido":
            default:
                holder.btnAccion.setText("Seguir");
                holder.btnAccion.setOnClickListener(v -> {
                    CreateSolicitud(_amigo, position);
                });
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            AbrirPerfilUsuario(_idUsuarioAmigo);
        });
    }

    @Override
    public int getItemCount() {
        return amigos.size();
    }

    private void CargarNombresUsuarios() {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        UsuarioService _service = _retrofit.create(UsuarioService.class);
        Call<List<Usuario>> _call = _service.GetAllUsuarios();
        _call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                List<Usuario> _usuarios = response.body();
                for (Usuario _usuario : _usuarios) {
                    nombresCache.put(_usuario.GetId(), _usuario.GetUsuario());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
            }
        });
    }

    private Integer GetLoggedUserId() {
        SharedPreferences _prefs = context.getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        int _id = _prefs.getInt("id_usuario", -1);
        return _id <= 0 ? null : _id;
    }

    private void CreateSolicitud(Amigo _amigo, int _position) {

        Integer _idUsuarioLogueado = GetLoggedUserId();

        if (_idUsuarioLogueado == null) {
            Toast.makeText(context, "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit _retrofit = RetrofitClient.GetInstance();
        AmigosAPI _api = _retrofit.create(AmigosAPI.class);

        ApiCreateAmigo _req = new ApiCreateAmigo(
                _idUsuarioLogueado,
                _amigo.getIdUsuarioAmigo()
        );

        _api.CreateAmigo(_req).enqueue(new Callback<ApiCreateAmigo>() {
            @Override
            public void onResponse(Call<ApiCreateAmigo> call, Response<ApiCreateAmigo> response) {
                _amigo.setEstado("siguiendo");
                notifyItemChanged(_position);
                Toast.makeText(context, "Ahora sigues a este usuario", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiCreateAmigo> call, Throwable t) {
                Toast.makeText(context, "Error al seguir usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateEstado(Amigo _amigo, String _nuevoEstado, int _position) {

        Integer _idUsuarioLogueado = GetLoggedUserId();

        if (_idUsuarioLogueado == null) {
            Toast.makeText(context, "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        int _idUsuarioAmigo = _amigo.getIdUsuarioAmigo();

        Retrofit _retrofit = RetrofitClient.GetInstance();
        AmigosAPI _api = _retrofit.create(AmigosAPI.class);

        ApiUpdateAmigo _req = new ApiUpdateAmigo(
                _idUsuarioLogueado,
                _idUsuarioAmigo,
                _nuevoEstado
        );

        _api.UpdateAmigo(_amigo.getIdAmigo(), _req).enqueue(new Callback<ApiUpdateAmigo>() {
            @Override
            public void onResponse(Call<ApiUpdateAmigo> call, Response<ApiUpdateAmigo> response) {
                _amigo.setEstado(_nuevoEstado);
                notifyItemChanged(_position);
                String _msg = "siguiendo".equals(_nuevoEstado)
                        ? "Ahora sigues a este usuario"
                        : "Has dejado de seguir a este usuario";
                Toast.makeText(context, _msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiUpdateAmigo> call, Throwable t) {
                Toast.makeText(context, "Error actualizando estado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AbrirPerfilUsuario(int _idUsuarioAmigo) {
        Intent _intent = new Intent(context, PerfilActivity.class);
        _intent.putExtra("id_usuario_perfil", _idUsuarioAmigo);
        context.startActivity(_intent);
    }

    public static class AmigoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        Button btnAccion, btnAceptar, btnRechazar;
        LinearLayout layoutSolicitud;

        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombreAmigo);
            btnAccion = itemView.findViewById(R.id.btnAccionAmigo);
            layoutSolicitud = itemView.findViewById(R.id.layoutSolicitud);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}