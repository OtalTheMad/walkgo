package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AmigosActivity extends AppCompatActivity {

    private RecyclerView rvAmigos;
    private AmigosAdapter adapter;
    private List<Amigo> listaAmigos;
    private Button btnAgregarAmigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos);

        rvAmigos = findViewById(R.id.rvAmigos);
        btnAgregarAmigo = findViewById(R.id.btnAgregarAmigo);

        // Lista de ejemplo
        listaAmigos = new ArrayList<>();
        listaAmigos.add(new Amigo("Juan Pérez", "@juanp", "activo", R.drawable.amigo1, 5));
        listaAmigos.add(new Amigo("María López", "@marial", "no_amigo", R.drawable.amiga2, 0));
        listaAmigos.add(new Amigo("Carlos Díaz", "@carlosd", "solicitud_recibida", R.drawable.amigo3, 2));

        adapter = new AmigosAdapter(this, listaAmigos);
        rvAmigos.setLayoutManager(new LinearLayoutManager(this));
        rvAmigos.setAdapter(adapter);

        btnAgregarAmigo.setOnClickListener(v ->
                Toast.makeText(this, "Aquí puedes enviar una nueva solicitud", Toast.LENGTH_SHORT).show()
        );
    }
}