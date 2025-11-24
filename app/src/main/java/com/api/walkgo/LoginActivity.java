package com.api.walkgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.api.walkgo.models.LoginRequest;
import com.api.walkgo.models.LoginResponse;
import com.example.walkgo.HomeActivity;
import com.example.walkgo.R;
import com.example.walkgo.RegistroActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin;
    private Button btnIrRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        RetrofitClient.Init(getApplicationContext());

        btnLogin.setOnClickListener(v -> IniciarSesion());
        btnIrRegistro.setOnClickListener(v -> IrARegistro());
    }

    private void IniciarSesion() {
        String _username = editUsername.getText().toString().trim();
        String _password = editPassword.getText().toString().trim();
        if (_username.isEmpty() || _password.isEmpty()) {
            Toast.makeText(this, "Ingresa usuario y clave", Toast.LENGTH_SHORT).show();
            return;
        }
        LoginRequest _request = new LoginRequest();
        _request.SetUsername(_username);
        _request.SetPassword(_password);

        Retrofit _retrofit = RetrofitClient.GetInstance();
        AuthService _authService = _retrofit.create(AuthService.class);
        Call<LoginResponse> _call = _authService.Login(_request);
        _call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                    return;
                }
                String _token = response.body().GetToken();
                int _userId = JwtUtils.GetUserIdFromToken(_token);

                if (_userId <= 0) {
                    Toast.makeText(LoginActivity.this, "Token inválido", Toast.LENGTH_SHORT).show();
                    return;
                }
                GuardarSesion(_token, _userId);
                RetrofitClient.Init(getApplicationContext());
                IrAHOME();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GuardarSesion(String token, int userId) {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor _editor = _prefs.edit();
        _editor.putString("jwt_token", token);
        _editor.putInt("id_usuario", userId);
        _editor.apply();
    }

    private void IrAHOME() {
        Intent _intent = new Intent(this, HomeActivity.class);
        startActivity(_intent);
        finish();
    }

    private void IrARegistro() {
        Intent _intent = new Intent(this, RegistroActivity.class);
        startActivity(_intent);
    }
}