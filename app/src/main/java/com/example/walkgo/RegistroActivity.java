package com.example.walkgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.api.walkgo.AuthService;
import com.api.walkgo.JwtUtils;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.models.LoginResponse;
import com.api.walkgo.models.RegisterRequest;
import com.example.walkgo.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistroActivity extends AppCompatActivity {

    private EditText editUsuario;
    private EditText editClave;
    private EditText editRepiteClave;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editUsuario = findViewById(R.id.idUsuario);
        editClave = findViewById(R.id.idClave);
        editRepiteClave = findViewById(R.id.idRepiteClave);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        RetrofitClient.Init(getApplicationContext());

        btnRegistrar.setOnClickListener(v -> RegistrarUsuario());
    }

    private void RegistrarUsuario() {
        String _usuario = editUsuario.getText().toString().trim();
        String _clave = editClave.getText().toString().trim();
        String _repiteClave = editRepiteClave.getText().toString().trim();

        if (_usuario.isEmpty() || _clave.isEmpty() || _repiteClave.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!_clave.equals(_repiteClave)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (_usuario.length() > 30 || _clave.length() > 30) {
            Toast.makeText(this, "Máximo 30 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest _request = new RegisterRequest();
        _request.SetUsuario(_usuario);
        _request.SetClave(_clave);

        Retrofit _retrofit = RetrofitClient.GetInstance();
        AuthService _authService = _retrofit.create(AuthService.class);
        Call<LoginResponse> _call = _authService.Register(_request);
        _call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    int _code = response.code();
                    if (_code == 400) {
                        Toast.makeText(RegistroActivity.this, "Datos inválidos", Toast.LENGTH_SHORT).show();
                    } else if (_code == 409) {
                        Toast.makeText(RegistroActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistroActivity.this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                String _token = response.body().GetToken();
                int _userId = JwtUtils.GetUserIdFromToken(_token);
                if (_userId <= 0) {
                    Toast.makeText(RegistroActivity.this, "Token inválido", Toast.LENGTH_SHORT).show();
                    return;
                }
                GuardarSesion(_token, _userId);
                RetrofitClient.Init(getApplicationContext());
                IrAHome();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
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

    private void IrAHome() {
        Intent _intent = new Intent(this, HomeActivity.class);
        startActivity(_intent);
        finish();
    }
}