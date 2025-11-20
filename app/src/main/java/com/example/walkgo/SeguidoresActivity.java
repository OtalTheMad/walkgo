package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.api.walkgo.AmigosAPI;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.models.Amigo;
import com.api.walkgo.models.ApiAmigo;
import com.example.walkgo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SeguidoresActivity extends AppCompatActivity {

    private RecyclerView rvAmigos;
    private AmigosAdapter amigosAdapter;

    private List<Amigo> listaAmigos = new ArrayList<>();

    private Integer idUsuarioActual;
    private boolean puedeCargar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos);

        rvAmigos = findViewById(R.id.rvAmigos);
        rvAmigos.setLayoutManager(new LinearLayoutManager(this));

        amigosAdapter = new AmigosAdapter(this, listaAmigos);
        rvAmigos.setAdapter(amigosAdapter);

        String _token = GetToken();

        if (_token == null) {
            Toast.makeText(this, "No hay token activo. Inicia sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        Integer _idUsuario = GetLoggedUserId();

        if (_idUsuario == null) {
            Toast.makeText(this, "ID de usuario no encontrado.", Toast.LENGTH_LONG).show();
            return;
        }

        idUsuarioActual = _idUsuario;
        puedeCargar = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (puedeCargar && idUsuarioActual != null) {
            CargarAmigos(idUsuarioActual);
        }
    }

    private String GetToken() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", MODE_PRIVATE);
        return _prefs.getString("jwt_token", null);
    }

    private Integer GetLoggedUserId() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", MODE_PRIVATE);
        int _id = _prefs.getInt("id_usuario", -1);
        return _id == -1 ? null : _id;
    }

    private void CargarAmigos(Integer _idUsuario) {

        RetrofitClient.Init(this);

        Retrofit _retrofit = RetrofitClient.GetInstance();
        AmigosAPI _api = _retrofit.create(AmigosAPI.class);

        _api.GetAmigosByUsuario(_idUsuario).enqueue(new Callback<List<ApiAmigo>>() {
            @Override
            public void onResponse(Call<List<ApiAmigo>> call, Response<List<ApiAmigo>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(SeguidoresActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<ApiAmigo> _apiLista = response.body();

                listaAmigos.clear();

                for (ApiAmigo _apiAmigo : _apiLista) {
                    Amigo _amigo = new Amigo();
                    if (_apiAmigo.idUsuario != null) {
                        _amigo.SetIdUsuario(_apiAmigo.idUsuario);
                    }
                    if (_apiAmigo.idUsuarioAmigo != null) {
                        _amigo.SetIdUsuarioAmigo(_apiAmigo.idUsuarioAmigo);
                    }
                    if (_apiAmigo.estado != null) {
                        _amigo.SetEstado(_apiAmigo.estado);
                    } else {
                        _amigo.SetEstado("no_seguido");
                    }
                    listaAmigos.add(_amigo);
                }

                amigosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ApiAmigo>> call, Throwable t) {
                Toast.makeText(SeguidoresActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}