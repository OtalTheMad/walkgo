package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.api.walkgo.AmigosAPI;
import com.api.walkgo.AmigosService;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.models.ApiAmigo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeguidoresActivity extends AppCompatActivity {

    private RecyclerView rvAmigos;
    private AmigosAdapter amigosAdapter;

    private List<Amigo> listaAmigos = new ArrayList<>();

    private Amigo Convert(ApiAmigo api) {
        return new Amigo(
                api.idAmigo,
                api.idUsuario,
                api.idUsuarioAmigo,
                api.estado
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos);

        rvAmigos = findViewById(R.id.rvAmigos);

        rvAmigos.setLayoutManager(new LinearLayoutManager(this));
        amigosAdapter = new AmigosAdapter(this, listaAmigos);
        rvAmigos.setAdapter(amigosAdapter);

        // Token de prueba
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc2MzM5OTkxNywiZXhwIjoxNzYzNDg2MzE3fQ.jDUjnSk-GJSW-JoJTANmsSpyHe7-83a2xXHFuG8ANWM";

        if (token == null) {
            Toast.makeText(this, "No hay token activo. Inicia sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        AmigosService.SetToken(token);

        // ID de usuario de prueba
        Integer idUsuario = 9;

        if (idUsuario == null) {
            Toast.makeText(this, "ID de usuario no encontrado.", Toast.LENGTH_LONG).show();
            return;
        }

        CargarAmigos(idUsuario);
    }

    private String GetToken() {
        SharedPreferences prefs = getSharedPreferences("WALKGO_PREFS", MODE_PRIVATE);
        return prefs.getString("jwt", null);
    }

    private Integer GetLoggedUserId() {
        SharedPreferences prefs = getSharedPreferences("WALKGO_PREFS", MODE_PRIVATE);
        int id = prefs.getInt("user_id", -1);
        return id == -1 ? null : id;
    }

    private void CargarAmigos(Integer idUsuario) {

        RetrofitClient.Init(this);

        AmigosAPI api = RetrofitClient.GetInstance().create(AmigosAPI.class);

        api.GetAmigosByUsuario(idUsuario).enqueue(new Callback<List<ApiAmigo>>() {
            @Override
            public void onResponse(Call<List<ApiAmigo>> call, Response<List<ApiAmigo>> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(SeguidoresActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<ApiAmigo> apiList = response.body();
                listaAmigos.clear();

                for (ApiAmigo a : apiList) {
                    listaAmigos.add(Convert(a));
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
