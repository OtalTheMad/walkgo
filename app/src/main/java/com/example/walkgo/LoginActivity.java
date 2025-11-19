package com.example.walkgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*; // Aquí es donde se necesita la dependencia activity-ktx

import com.google.android.material.textfield.TextInputEditText;
import com.example.walkgo.network.ApiService;
import com.example.walkgo.network.RetrofitClient;
import com.example.walkgo.models.Usuario;
import com.example.walkgo.models.LoginRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Configuración de Insets (Tu código moderno)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Inicialización de Vistas (Asegúrate de que el layout 'activity_login' tenga un ID 'main')
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progress);

        // 3. Inicialización de Servicios
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        sharedPreferences = getSharedPreferences("RunnerAppPrefs", Context.MODE_PRIVATE);

        // 4. Listener para el Login
        btnLogin.setOnClickListener(v -> attemptLogin());

        // 5. Listener para ir a Registrar
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 6. Verificar sesión previa
        checkSession();
    }

    private void checkSession() {
        String token = sharedPreferences.getString("auth_token", null);
        if (token != null) {
            goToMainActivity();
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Ingresa email y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        LoginRequest loginRequest = new LoginRequest(email, password);

        // POST /api/login
        apiService.loginUser(loginRequest).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Usuario user = response.body();

                    // Asegúrate de que tu objeto Usuario tiene métodos getId() y getToken()
                    String userId = user.getId();
                    String authToken = user.getToken();

                    if (userId != null && authToken != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_id", userId);
                        editor.putString("auth_token", authToken);
                        editor.apply();
                        goToMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Respuesta incompleta del servidor.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales inválidas. Código: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace(); // Imprime la traza para depuración
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? "" : "Entrar");
    }


}