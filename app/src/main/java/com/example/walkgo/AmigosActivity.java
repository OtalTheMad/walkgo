package com.example.walkgo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.api.walkgo.AmigosAPI;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.models.ApiAmigo;
import com.api.walkgo.models.Amigo;
import com.example.walkgo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AmigosActivity extends AppCompatActivity {

    private RecyclerView rvAmigos;
    private AmigosAdapter amigosAdapter;
    private List<Amigo> amigosList = new ArrayList<>();
    private int loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos);
        RetrofitClient.Init(getApplicationContext());
        InicializarIds();
        InicializarVistas();
        InicializarRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CargarAmigos();
    }

    private void InicializarIds() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        loggedUserId = _prefs.getInt("id_usuario", -1);
    }

    private void InicializarVistas() {
        rvAmigos = findViewById(R.id.rvAmigos);
    }

    private void InicializarRecycler() {
        amigosAdapter = new AmigosAdapter(this, amigosList);
        rvAmigos.setLayoutManager(new LinearLayoutManager(this));
        rvAmigos.setAdapter(amigosAdapter);
    }

    private void CargarAmigos() {
        if (loggedUserId <= 0) {
            amigosList.clear();
            amigosAdapter.notifyDataSetChanged();
            return;
        }

        Retrofit _retrofit = RetrofitClient.GetInstance();
        AmigosAPI _api = _retrofit.create(AmigosAPI.class);

        Call<List<ApiAmigo>> _call = _api.GetAmigosByUsuario(loggedUserId);
        _call.enqueue(new Callback<List<ApiAmigo>>() {
            @Override
            public void onResponse(Call<List<ApiAmigo>> call, Response<List<ApiAmigo>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    amigosList.clear();
                    amigosAdapter.notifyDataSetChanged();
                    return;
                }

                List<ApiAmigo> _apiList = response.body();
                amigosList.clear();

                for (ApiAmigo _apiAmigo : _apiList) {
                    Amigo _amigo = new Amigo();

                    if (_apiAmigo.idAmigo != null) {
                        _amigo.SetIdUsuarioAmigo(_apiAmigo.idAmigo);
                    }

                    if (_apiAmigo.idUsuario != null) {
                        _amigo.SetIdUsuario(_apiAmigo.idUsuario);
                    }

                    if (_apiAmigo.idUsuarioAmigo != null) {
                        _amigo.SetIdUsuario(_apiAmigo.idUsuarioAmigo);
                    }

                    if (_apiAmigo.estado != null) {
                        _amigo.SetEstado(_apiAmigo.estado);
                    } else {
                        _amigo.SetEstado("no_seguido");
                    }

                    amigosList.add(_amigo);
                }

                amigosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ApiAmigo>> call, Throwable t) {
                amigosList.clear();
                amigosAdapter.notifyDataSetChanged();
            }
        });
    }
}