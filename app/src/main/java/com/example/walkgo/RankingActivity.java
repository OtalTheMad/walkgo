package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

// Asegúrate de que estas rutas sean correctas
import com.example.walkgo.adapters.RankingAdapter;
import com.example.walkgo.models.Runner;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RankingActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialButtonToggleGroup toggleGroupRanking;
    private RecyclerView recyclerViewRanking;
    private RankingAdapter rankingAdapter;
    private List<Runner> allRunners;
    private final String TAG = "RankingActivity";
    private final String CURRENT_USER_USERNAME = "CorredorUltra"; // Usuario actual para resaltar
    private String currentFilter = "Global"; // Estado inicial

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asegúrate de que R.layout.activity_ranking exista
        setContentView(R.layout.activity_ranking);

        // 1. Inicialización de Vistas
        toolbar = findViewById(R.id.toolbarRanking);
        toggleGroupRanking = findViewById(R.id.toggleGroupRanking);
        recyclerViewRanking = findViewById(R.id.recyclerViewRanking);

        // 2. Cargar todos los datos (Mock Data)
        loadMockRankingData();

        // 3. Configuración del RecyclerView
        recyclerViewRanking.setLayoutManager(new LinearLayoutManager(this));
        // Inicialización del adaptador con todos los datos por defecto
        rankingAdapter = new RankingAdapter(this, allRunners, CURRENT_USER_USERNAME);
        recyclerViewRanking.setAdapter(rankingAdapter);

        // 4. Configuración de la Barra de Herramientas
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ranking");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 5. Configuración del Toggle Group (Filtro)
        toggleGroupRanking.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnRankingGlobal) {
                    currentFilter = "Global";
                    Log.d(TAG, "Filtro cambiado a: Global");
                    loadRankingData(currentFilter);
                } else if (checkedId == R.id.btnRankingAmigos) {
                    currentFilter = "Amigos";
                    Log.d(TAG, "Filtro cambiado a: Amigos");
                    loadRankingData(currentFilter);
                }
            }
        });

        // 6. Cargar datos iniciales
        loadRankingData(currentFilter);
    }

    /**
     * Carga datos simulados de corredores. En una app real, esto sería una llamada API.
     */
    private void loadMockRankingData() {
        allRunners = new ArrayList<>();
        // Los datos están ordenados por kilómetros (descendente)

        // Posición, Nombre Completo, Username, Kilómetros, Movimiento, URL de Imagen, Es Usuario Actual, Es Amigo
        allRunners.add(new Runner(1, "Pedro 'El Rayo'", "ElRayo", 35.2f, "= 0", "url_img_1", false, false));
        allRunners.add(new Runner(2, "Andrea Velocidad", "AndreVelo", 30.1f, "↑ 5", "url_img_2", false, true)); // Amigo
        allRunners.add(new Runner(3, "Carlos Gómez", "CarlosG", 28.5f, "↑ 2", "url_img_3", false, false));
        // Este es el usuario actual.
        allRunners.add(new Runner(4, "Juan Pérez", CURRENT_USER_USERNAME, 25.0f, "↓ 1", "url_img_4", true, false));
        allRunners.add(new Runner(5, "María Rodríguez", "MaratonistaHN", 24.5f, "= 0", "url_img_5", false, true)); // Amigo
        allRunners.add(new Runner(6, "Daniela Fitness", "DaniFit", 22.8f, "↑ 8", "url_img_6", false, false));
        allRunners.add(new Runner(7, "Ernesto Trotador", "ETrotador", 19.9f, "↓ 3", "url_img_7", false, false));
        allRunners.add(new Runner(8, "Sofía Correcaminos", "Correcaminos", 15.0f, "= 0", "url_img_8", false, false));
        allRunners.add(new Runner(9, "LeoRunner", "LeoRunner", 12.4f, "↓ 2", "url_img_9", false, false));
        allRunners.add(new Runner(10, "Zoe La Lenta", "ZoeSlow", 9.1f, "↑ 1", "url_img_10", false, false));
    }

    /**
     * Aplica el filtro seleccionado ("Global" o "Amigos") y actualiza el RecyclerView.
     * @param filter El tipo de ranking a mostrar.
     */
    private void loadRankingData(String filter) {
        Toast.makeText(this, "Cargando ranking: " + filter, Toast.LENGTH_SHORT).show();
        List<Runner> filteredList = new ArrayList<>();

        if (filter.equals("Amigos")) {
            // Filtra la lista para incluir solo amigos y al usuario actual
            filteredList = allRunners.stream()
                    .filter(runner -> runner.isFriend() || runner.isCurrentUser())
                    .collect(Collectors.toList());
        } else {
            // Muestra la lista completa (Global)
            filteredList.addAll(allRunners);
        }

        // Actualizar los datos en el adaptador
        if (rankingAdapter != null) {
            rankingAdapter.updateData(filteredList);
        }
    }
}
