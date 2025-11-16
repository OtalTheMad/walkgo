package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.content.Intent; // Importación necesaria
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.Random;

public class PerfilActivity extends AppCompatActivity {

    private TextInputEditText etName, etUsername, etBiography;
    private TextInputLayout tilName, tilUsername, tilBiography;

    // Vistas de Visualización
    private TextView tvUsernameDisplay, tvNameDisplay;

    // KPIs
    private TextView tvWeeklyKm, tvTotalKm, tvFriendsCount, tvAge;

    // Botones
    private ImageView btnEditProfile, btnAddFriend;
    private Button btnChangePassword;
    private TextView tvToolbarTitle; // Referencia para el título de la toolbar

    // Estado y Datos
    private PerfilData currentProfile;
    private boolean isEditMode = false;
    private final DecimalFormat kmFormat = new DecimalFormat("0.0 km");

    // Simulación del usuario que está logueado
    private final String LOGGED_IN_USERNAME = "CorredorUltra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        initializeViews();

        // 1. Obtener el nombre de usuario del Intent
        Intent intent = getIntent();
        String targetUsername = intent.getStringExtra("username"); // Clave usada en RankingAdapter

        // 2. Determinar si es perfil propio o ajeno
        boolean isOwnProfile;
        if (targetUsername == null || targetUsername.isEmpty() || targetUsername.equals(LOGGED_IN_USERNAME)) {
            isOwnProfile = true;
        } else {
            isOwnProfile = false;
        }

        // 3. Cargar datos según el tipo de perfil
        loadProfileData(isOwnProfile, targetUsername);

        // 4. Configurar Listeners basado en si es perfil propio
        if (currentProfile != null && currentProfile.isOwnProfile()) {
            btnEditProfile.setOnClickListener(v -> toggleEditMode());
            btnChangePassword.setOnClickListener(v -> handleChangePassword());
            // Ocultar botón de amigo en perfil propio
            btnAddFriend.setVisibility(View.GONE);
        } else if (currentProfile != null && !currentProfile.isOwnProfile()) {
            // Mostrar botón de amigo en perfil ajeno
            btnAddFriend.setOnClickListener(v -> handleAddFriend());
            btnEditProfile.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        // Inputs de Edición
        tilName = findViewById(R.id.til_name);
        etName = findViewById(R.id.et_name);
        tilUsername = findViewById(R.id.til_username);
        etUsername = findViewById(R.id.et_username);
        tilBiography = findViewById(R.id.til_biography);
        etBiography = findViewById(R.id.et_biography);

        tvUsernameDisplay = findViewById(R.id.tv_username_display);
        tvNameDisplay = findViewById(R.id.tv_name_display);

        // KPIs
        tvWeeklyKm = findViewById(R.id.tv_weekly_km);
        tvTotalKm = findViewById(R.id.tv_total_km);
        tvFriendsCount = findViewById(R.id.tv_friends_count);
        tvAge = findViewById(R.id.tv_age);

        // Botones y Toolbar Title
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnAddFriend = findViewById(R.id.btn_add_friend);
        btnChangePassword = findViewById(R.id.btn_change_password);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title); // Asumiendo que esta es tu vista de título

        RecyclerView rvFriendsList = findViewById(R.id.rv_friends_list);
    }

    /**
     * Carga datos simulados del perfil según si es propio o ajeno.
     * En una aplicación real, aquí harías una llamada a la API.
     * @param isOwn Indica si es el perfil del usuario logueado.
     * @param targetUsername El username del perfil a cargar (nulo si es propio o no especificado).
     */
    private void loadProfileData(boolean isOwn, String targetUsername) {
        if (isOwn) {
            // Perfil Propio
            currentProfile = new PerfilData(
                    "Juan Pérez", LOGGED_IN_USERNAME, "Honduras", "1995-05-15",
                    "Corredor aficionado, disfruto de las mañanas frescas para sumar kilómetros en la montaña.",
                    "url_default_photo", 14.0, 230.5, 42, true
            );
            tvToolbarTitle.setText("Mi Perfil");
        } else {
            // Perfil Ajeno (Amigo o del Ranking)
            // Aquí se usaría targetUsername para cargar datos específicos de ese usuario
            // Simulamos datos para targetUsername, si es nulo, usamos el de María
            String usernameToLoad = (targetUsername != null && !targetUsername.isEmpty()) ? targetUsername : "MaratonistaHN";

            // SIMULACIÓN: Podrías tener una lista de perfiles simulados
            if (usernameToLoad.equals("MaratonistaHN")) {
                currentProfile = new PerfilData(
                        "María Rodríguez", "MaratonistaHN", "Honduras", "1998-10-20",
                        "Entrenando para mi primer maratón. ¡Acepto desafíos!",
                        "url_default_photo_2", 8.2, 110.0, 15, false
                );
            } else {
                // Otro perfil de ejemplo (para simular clic en Ranking)
                currentProfile = new PerfilData(
                        "Carlos Gómez", usernameToLoad, "Costa Rica", "1990-01-01",
                        "Deporte y naturaleza. ¡Siempre adelante!",
                        "url_default_photo_3", 22.0, 450.0, 50, false
                );
            }

            tvToolbarTitle.setText("Perfil de " + usernameToLoad);
        }

        updateUIData();
        setFieldsEnabled(false);
    }


    private void updateUIData() {
        if (currentProfile == null) return; // Fallo seguro

        tvUsernameDisplay.setText(currentProfile.getUsername());
        tvNameDisplay.setText(currentProfile.getName() + " (" + currentProfile.getCountry() + ")");

        // KPIs
        tvWeeklyKm.setText(kmFormat.format(currentProfile.getWeeklyKilometers()));
        tvTotalKm.setText(kmFormat.format(currentProfile.getTotalKilometers()));
        tvFriendsCount.setText(String.valueOf(currentProfile.getFriendsCount()));
        // tvAge.setText(calculateAge(currentProfile.getBirthDate())); // Implementar cálculo real
        tvAge.setText("30"); // Mock

        // Campos de edición
        etName.setText(currentProfile.getName());
        etUsername.setText(currentProfile.getUsername());
        etBiography.setText(currentProfile.getBiography());
    }


    private void toggleEditMode() {
        isEditMode = !isEditMode;

        if (isEditMode) {
            btnEditProfile.setImageResource(R.drawable.ic_save); // Asumo que existe ic_save
            btnEditProfile.setContentDescription("Guardar Cambios");
            setFieldsEnabled(true);
            // Uso de color de la paleta: green_700
            tvUsernameDisplay.setTextColor(ContextCompat.getColor(this, R.color.green_700));
            Toast.makeText(this, "Modo Edición Activado", Toast.LENGTH_SHORT).show();
        } else {
            if (saveProfileChanges()) {
                btnEditProfile.setImageResource(R.drawable.ic_edit); // Asumo que existe ic_edit
                btnEditProfile.setContentDescription("Editar Perfil");
                setFieldsEnabled(false);
                tvUsernameDisplay.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                Toast.makeText(this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
            } else {
                isEditMode = true; // Si la validación falla, nos quedamos en modo edición
            }
        }
    }

    /**
     * Habilita o deshabilita los campos de texto para edición.
     */
    private void setFieldsEnabled(boolean enabled) {
        etName.setEnabled(enabled);
        etUsername.setEnabled(enabled);
        etBiography.setEnabled(enabled);

        if (!enabled) {
            tilName.setError(null);
            tilUsername.setError(null);
        }
    }

    /**
     * Valida y guarda los cambios del perfil (simulado).
     * @return true si el guardado fue exitoso, false si falló la validación.
     */
    private boolean saveProfileChanges() {
        String newName = etName.getText().toString().trim();
        String newUsername = etUsername.getText().toString().trim();
        String newBiography = etBiography.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            tilName.setError("El nombre no puede estar vacío.");
            return false;
        }

        if (TextUtils.isEmpty(newUsername)) {
            tilUsername.setError("El nombre de usuario es obligatorio.");
            return false;
        }

        // SIMULACIÓN DE VALIDACIÓN DE BASE DE DATOS REMOTA
        if (!currentProfile.getUsername().equals(newUsername)) {
            if (!isUsernameAvailable(newUsername)) {
                tilUsername.setError("El usuario '" + newUsername + "' ya está en uso.");
                return false;
            }
        }

        // Aplicar y Persistir Cambios
        currentProfile.setName(newName);
        currentProfile.setUsername(newUsername);
        currentProfile.setBiography(newBiography);

        updateUIData();

        return true;
    }

    /** * SIMULACIÓN: Verifica la disponibilidad del nombre de usuario en la base de datos.
     */
    private boolean isUsernameAvailable(String username) {
        // En el 20% de los casos, falla para simular que el usuario ya existe (e.g., colisión)
        return new Random().nextInt(100) > 20;
    }

    /** * Maneja la acción de cambiar contraseña (solo perfil propio).
     */
    private void handleChangePassword() {
        Toast.makeText(this, "Abriendo diálogo de cambio de contraseña...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Maneja la acción de agregar amigo (solo perfil ajeno).
     */
    private void handleAddFriend() {
        if (currentProfile == null) return;
        // Aquí iría la lógica real de agregar amigo (API Call)
        btnAddFriend.setVisibility(View.GONE);
        Toast.makeText(this, "Solicitud de amistad enviada a " + currentProfile.getUsername(), Toast.LENGTH_SHORT).show();
    }
}