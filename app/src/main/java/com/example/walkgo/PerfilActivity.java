package com.example.walkgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView; // Importación necesaria para la lista de amigos
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Usamos Toast para simular mensajes modales/validaciones

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.Random; // Para simular la validación de usuario

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

    // Estado y Datos
    private PerfilData currentProfile;
    private boolean isEditMode = false;
    private final DecimalFormat kmFormat = new DecimalFormat("0.0 km");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // 1. Inicializar Vistas
        initializeViews();

        // 2. Cargar Datos (Simulación)
        // En una app real, obtendrías el ID de usuario de los Intent Extras aquí
        // Por ahora, simulamos cargar el perfil PROPIO
        loadMockData(true); // Cargar perfil PROPIO

        // 3. Configurar Listeners
        if (currentProfile.isOwnProfile()) {
            btnEditProfile.setOnClickListener(v -> toggleEditMode());
            btnChangePassword.setOnClickListener(v -> handleChangePassword());
        } else {
            btnAddFriend.setOnClickListener(v -> handleAddFriend());
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

        // Vistas de Cabecera
        tvUsernameDisplay = findViewById(R.id.tv_username_display);
        tvNameDisplay = findViewById(R.id.tv_name_display);

        // KPIs
        tvWeeklyKm = findViewById(R.id.tv_weekly_km);
        tvTotalKm = findViewById(R.id.tv_total_km);
        tvFriendsCount = findViewById(R.id.tv_friends_count);
        tvAge = findViewById(R.id.tv_age);

        // Botones
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnAddFriend = findViewById(R.id.btn_add_friend);
        btnChangePassword = findViewById(R.id.btn_change_password);

        // RecyclerView de Amigos (solo para referencia, no implementaremos el Adapter aquí)
        RecyclerView rvFriendsList = findViewById(R.id.rv_friends_list);
        // rvFriendsList.setLayoutManager(new LinearLayoutManager(this));
        // rvFriendsList.setAdapter(new FriendsAdapter(friendsData));
    }

    /**
     * Simula la carga de datos del perfil, distinguiendo si es el perfil propio o ajeno.
     * @param isOwn Indica si se carga el perfil del usuario logeado.
     */
    private void loadMockData(boolean isOwn) {
        if (isOwn) {
            // Perfil Propio
            currentProfile = new PerfilData(
                    "Juan Pérez", "CorredorUltra", "Honduras", "1995-05-15",
                    "Corredor aficionado, disfruto de las mañanas frescas para sumar kilómetros en la montaña.",
                    "url_default_photo", 14.0, 230.5, 42, true
            );
            // Configuración de UI para perfil propio
            btnAddFriend.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.VISIBLE);
            btnChangePassword.setVisibility(View.VISIBLE);
            findViewById(R.id.tv_toolbar_title).setAccessibilityHeading(true);
            ((TextView) findViewById(R.id.tv_toolbar_title)).setText("Mi Perfil");
        } else {
            // Perfil Ajeno (Amigo o Corredor a seguir)
            currentProfile = new PerfilData(
                    "María Rodríguez", "MaratonistaHN", "Honduras", "1998-10-20",
                    "Entrenando para mi primer maratón. ¡Acepto desafíos!",
                    "url_default_photo_2", 8.2, 110.0, 15, false
            );
            // Configuración de UI para perfil ajeno (Modo Solo Lectura)
            btnAddFriend.setVisibility(View.VISIBLE);
            btnEditProfile.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tv_toolbar_title)).setText("Perfil de MaratonistaHN");
            // Nota: En un perfil ajeno, podrías ocultar o mostrar solo un subconjunto de KPIs
        }

        // Llenar datos de la UI
        updateUIData();

        // Asegurar que inicie en modo solo lectura si es el perfil propio
        setFieldsEnabled(false);
    }

    /** * Rellena la UI con los datos cargados en currentProfile.
     */
    private void updateUIData() {
        // Cabecera
        tvUsernameDisplay.setText(currentProfile.getUsername());
        tvNameDisplay.setText(currentProfile.getName() + " (" + currentProfile.getCountry() + ")");

        // KPIs
        tvWeeklyKm.setText(kmFormat.format(currentProfile.getWeeklyKilometers()));
        tvTotalKm.setText(kmFormat.format(currentProfile.getTotalKilometers()));
        tvFriendsCount.setText(String.valueOf(currentProfile.getFriendsCount()));
        // Simulación de cálculo de Edad (Simple, solo para UI)
        tvAge.setText("30"); // Reemplazar con lógica real de cálculo (fecha_nac - año actual)

        // Campos Editables
        etName.setText(currentProfile.getName());
        etUsername.setText(currentProfile.getUsername());
        etBiography.setText(currentProfile.getBiography());

        // Nota: La imagen de perfil (iv_profile_photo) requeriría una librería como Glide/Picasso
        // para cargar profilePhotoUrl, lo cual se omite por simplicidad.
    }

    /**
     * Alterna entre el modo de visualización y el modo de edición.
     */
    private void toggleEditMode() {
        isEditMode = !isEditMode;

        if (isEditMode) {
            // MODO EDICIÓN ACTIVADO
            btnEditProfile.setImageResource(R.drawable.ic_save); // Icono de Guardar
            btnEditProfile.setContentDescription("Guardar Cambios");
            setFieldsEnabled(true);
            tvUsernameDisplay.setTextColor(ContextCompat.getColor(this, R.color.green_700));
            Toast.makeText(this, "Modo Edición Activado", Toast.LENGTH_SHORT).show();

        } else {
            // MODO EDICIÓN DESACTIVADO (Guardar)

            if (saveProfileChanges()) {
                btnEditProfile.setImageResource(R.drawable.ic_edit); // Icono de Editar
                btnEditProfile.setContentDescription("Editar Perfil");
                setFieldsEnabled(false);
                tvUsernameDisplay.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                Toast.makeText(this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
            } else {
                // Si la validación falla (ej: usuario no disponible), volvemos al modo edición
                isEditMode = true;
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

        // Si no estamos en modo edición, quitamos el foco y los errores
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
        // 1. Obtener nuevos valores
        String newName = etName.getText().toString().trim();
        String newUsername = etUsername.getText().toString().trim();
        String newBiography = etBiography.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            tilName.setError("El nombre no puede estar vacío.");
            return false;
        }

        // 2. Validación de Nombre de Usuario (Criterio de Aceptación: Usuario disponible)
        if (TextUtils.isEmpty(newUsername)) {
            tilUsername.setError("El nombre de usuario es obligatorio.");
            return false;
        }

        // SIMULACIÓN DE VALIDACIÓN DE BASE DE DATOS REMOTA
        if (!currentProfile.getUsername().equals(newUsername)) {
            // Si el nombre de usuario cambió, simulamos verificar disponibilidad
            if (!isUsernameAvailable(newUsername)) {
                tilUsername.setError("El usuario '" + newUsername + "' ya está en uso.");
                return false;
            }
        }

        // 3. Aplicar y Persistir Cambios
        currentProfile.setName(newName);
        currentProfile.setUsername(newUsername);
        currentProfile.setBiography(newBiography);

        // Aquí iría la llamada a tu REST API para realizar el UPDATE (CRUD) de los datos
        // callUpdateProfileApi(currentProfile);

        // 4. Actualizar UI con los nuevos datos (Cabecera)
        updateUIData();

        return true;
    }

    /** * SIMULACIÓN: Verifica la disponibilidad del nombre de usuario en la base de datos.
     * En el 20% de los casos, falla para simular que el usuario ya existe.
     */
    private boolean isUsernameAvailable(String username) {
        // En una aplicación real, esto sería una llamada SÍNCRONA o ASÍNCRONA a la REST API
        return new Random().nextInt(100) > 20;
    }

    /** * Maneja la acción de cambiar contraseña (solo perfil propio).
     */
    private void handleChangePassword() {
        // Esto abriría un Modal o una nueva Activity para ingresar la contraseña actual y la nueva
        Toast.makeText(this, "Abriendo diálogo de cambio de contraseña...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Maneja la acción de agregar amigo (solo perfil ajeno).
     */
    private void handleAddFriend() {
        // Esto enviaría una solicitud de amistad a la REST API
        btnAddFriend.setVisibility(View.GONE);
        Toast.makeText(this, "Solicitud de amistad enviada a " + currentProfile.getUsername(), Toast.LENGTH_SHORT).show();
    }
}