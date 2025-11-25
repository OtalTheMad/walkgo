package com.example.walkgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.api.walkgo.PerfilService;
import com.api.walkgo.RetrofitClient;
import com.api.walkgo.models.Perfil;
import com.api.walkgo.models.PerfilUpdateRequest;
import com.example.walkgo.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditarPerfilActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGEN = 1001;

    private EditText editPaisPerfil;
    private EditText editBiografiaPerfil;
    private EditText editFechaNacPerfil;
    private ImageView imgFotoPerfilEditar;
    private Button btnSeleccionarFoto;
    private Button btnGuardarPerfil;

    private String fotoBase64Actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        editPaisPerfil = findViewById(R.id.editPaisPerfil);
        editBiografiaPerfil = findViewById(R.id.editBiografiaPerfil);
        editFechaNacPerfil = findViewById(R.id.editFechaNacPerfil);
        imgFotoPerfilEditar = findViewById(R.id.imgFotoPerfilEditar);
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);

        fotoBase64Actual = null;

        RetrofitClient.Init(getApplicationContext());

        ConfigurarFormatoFechaNacimiento();
        CargarPerfil();

        btnSeleccionarFoto.setOnClickListener(v -> SeleccionarFoto());
        btnGuardarPerfil.setOnClickListener(v -> GuardarCambios());
    }

    private void ConfigurarFormatoFechaNacimiento() {
        editFechaNacPerfil.addTextChangedListener(new TextWatcher() {

            private boolean _actualizando;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_actualizando) {
                    return;
                }

                String _texto = s.toString();

                String _soloDigitos = _texto.replaceAll("[^0-9]", "");
                if (_soloDigitos.length() > 8) {
                    _soloDigitos = _soloDigitos.substring(0, 8);
                }

                StringBuilder _formateado = new StringBuilder();
                for (int _i = 0; _i < _soloDigitos.length(); _i++) {
                    _formateado.append(_soloDigitos.charAt(_i));
                    if (_i == 3 || _i == 5) {
                        if (_i < _soloDigitos.length() - 1) {
                            _formateado.append("-");
                        }
                    }
                }

                String _nuevoTexto = _formateado.toString();

                _actualizando = true;
                editFechaNacPerfil.setText(_nuevoTexto);
                editFechaNacPerfil.setSelection(editFechaNacPerfil.getText().length());
                _actualizando = false;
            }
        });
    }

    private int GetLoggedUserId() {
        SharedPreferences _prefs = getSharedPreferences("WALKGO_PREFS", Context.MODE_PRIVATE);
        int _id = _prefs.getInt("id_usuario", -1);
        return _id;
    }

    private void CargarPerfil() {
        int _idUsuario = GetLoggedUserId();
        if (_idUsuario <= 0) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Retrofit _retrofit = RetrofitClient.GetInstance();
        PerfilService _service = _retrofit.create(PerfilService.class);
        Call<Perfil> _call = _service.GetPerfil(_idUsuario);
        _call.enqueue(new Callback<Perfil>() {
            @Override
            public void onResponse(Call<Perfil> call, Response<Perfil> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(EditarPerfilActivity.this, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show();
                    return;
                }
                Perfil _perfil = response.body();
                if (_perfil.GetPais() != null) {
                    editPaisPerfil.setText(_perfil.GetPais());
                }
                if (_perfil.GetBiografia() != null) {
                    editBiografiaPerfil.setText(_perfil.GetBiografia());
                }
                if (_perfil.GetFechaNac() != null) {
                    editFechaNacPerfil.setText(_perfil.GetFechaNac());
                }
                String _fotoBase64 = _perfil.GetFoto();
                if (_fotoBase64 != null && !_fotoBase64.isEmpty()) {
                    try {
                        byte[] _bytes = Base64.getDecoder().decode(_fotoBase64);
                        Bitmap _bitmap = BitmapFactory.decodeByteArray(_bytes, 0, _bytes.length);
                        imgFotoPerfilEditar.setImageBitmap(_bitmap);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Perfil> call, Throwable t) {
                Toast.makeText(EditarPerfilActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SeleccionarFoto() {
        Intent _intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(_intent, REQUEST_CODE_IMAGEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGEN && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri _uri = data.getData();
            try {
                InputStream _inputStream = getContentResolver().openInputStream(_uri);
                byte[] _bytes = LeerBytesDesdeInputStream(_inputStream);
                Bitmap _bitmap = BitmapFactory.decodeByteArray(_bytes, 0, _bytes.length);
                imgFotoPerfilEditar.setImageBitmap(_bitmap);
                fotoBase64Actual = Base64.getEncoder().encodeToString(_bytes);
            } catch (Exception e) {
                Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] LeerBytesDesdeInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream _buffer = new ByteArrayOutputStream();
        byte[] _data = new byte[4096];
        int _nRead;
        while ((_nRead = inputStream.read(_data, 0, _data.length)) != -1) {
            _buffer.write(_data, 0, _nRead);
        }
        return _buffer.toByteArray();
    }

    private void GuardarCambios() {
        int _idUsuario = GetLoggedUserId();
        if (_idUsuario <= 0) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String _pais = editPaisPerfil.getText().toString().trim();
        String _bio = editBiografiaPerfil.getText().toString().trim();
        String _fecha = editFechaNacPerfil.getText().toString().trim();

        PerfilUpdateRequest _request = new PerfilUpdateRequest();
        _request.SetPais(_pais.isEmpty() ? null : _pais);
        _request.SetBiografia(_bio.isEmpty() ? null : _bio);
        _request.SetFechaNac(_fecha.isEmpty() ? null : _fecha);
        _request.SetEstado("activo");
        if (fotoBase64Actual != null) {
            _request.SetFotoBase64(fotoBase64Actual);
        }

        Retrofit _retrofit = RetrofitClient.GetInstance();
        PerfilService _service = _retrofit.create(PerfilService.class);
        Call<Perfil> _call = _service.UpdatePerfil(_idUsuario, _request);
        _call.enqueue(new Callback<Perfil>() {
            @Override
            public void onResponse(Call<Perfil> call, Response<Perfil> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(EditarPerfilActivity.this, "No se pudo guardar el perfil", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Perfil> call, Throwable t) {
                Toast.makeText(EditarPerfilActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}