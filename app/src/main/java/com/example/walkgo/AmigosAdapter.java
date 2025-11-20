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

    private Context context;
    private List<Amigo> amigos;
    private Map<Integer, String> nombresCache = new HashMap<>();

    public AmigosAdapter(Context _context, List<Amigo> _amigos) {
        this.context = _context;
        this.amigos = _amigos;
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

        String _nombreCache = nombresCache.get(_idUsuarioAmigo);
        if (_nombreCache != null) {
            holder.tvNombre.setText(_nombreCache);
        } else {
            holder.tvNombre.setText("Usuario " + _idUsuarioAmigo);
            CargarNombreUsuario(_idUsuarioAmigo, holder);
        }

        holder.layoutSolicitud.setVisibility(View.GONE);
        holder.btnAccion.setVisibility(View.VISIBLE);
        holder.btnAccion.setEnabled(true);

        String _estado = _amigo.getEstado();

        switch (_estado) {

            case "siguiendo":
                holder.btnAccion.setText("Dejar de seguir");
                holder.btnAccion.setOnClickListener(v -> {
                    UpdateEstado(_amigo, "no_siguiendo", position);
                });
                break;

            case "no_siguiendo":
            default:
                holder.btnAccion.setText("Seguir");
                holder.btnAccion.setOnClickListener(v -> {
                    CreateSolicitud(_amigo, position);
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return amigos.size();
    }

    private void CargarNombreUsuario(int _idUsuarioAmigo, AmigoViewHolder _holder) {
        Retrofit _retrofit = RetrofitClient.GetInstance();
        UsuarioService _service = _retrofit.create(UsuarioService.class);
        Call<Usuario> _call = _service.GetUsuario(_idUsuarioAmigo);
        _call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                Usuario _usuario = response.body();
                String _nombre = _usuario.GetUsuario();
                if (_nombre == null || _nombre.isEmpty()) {
                    _nombre = "Usuario " + _idUsuarioAmigo;
                }
                nombresCache.put(_idUsuarioAmigo, _nombre);
                int _adapterPos = _holder.getAdapterPosition();
                if (_adapterPos == RecyclerView.NO_POSITION) {
                    return;
                }
                Amigo _amigoActual = amigos.get(_adapterPos);
                if (_amigoActual.getIdUsuarioAmigo() == _idUsuarioAmigo) {
                    _holder.tvNombre.setText(_nombre);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
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