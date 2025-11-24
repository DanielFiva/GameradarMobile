package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

public class CreateUserActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput, ageInput, phoneInput;
    Button btnAccept, btnBack;

    SocketClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        usernameInput = findViewById(R.id.UsernameEditText);
        passwordInput = findViewById(R.id.PasswordEditText);
        ageInput = findViewById(R.id.AgeEditText);
        phoneInput = findViewById(R.id.PhoneEditText);

        btnAccept = findViewById(R.id.btnAccept);
        btnBack = findViewById(R.id.btnBack);

        // Get the existing SocketClient from LoginActivity or create new
        client = new SocketClient("4.tcp.ngrok.io", 12761, new SocketClient.MessageListener() {
            @Override
            public void onConnected() {}
            @Override
            public void onNewMessage(JSONObject msg) {}
            @Override
            public void onMessageHistory(JSONObject history) {}
            @Override
            public void onGenericResponse(JSONObject resp) {
                runOnUiThread(() -> handleResponse(resp));
            }
        });
        client.connect();

        btnAccept.setOnClickListener(v -> createUser());
        btnBack.setOnClickListener(v -> finish());
    }

    private void createUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String ageStr = ageInput.getText().toString();
        String phone = phoneInput.getText().toString();

        if (username.isEmpty() || password.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            JSONObject userData = new JSONObject();
            userData.put("username", username);
            userData.put("password", password);
            userData.put("age", age);
            userData.put("phone", phone);
            userData.put("role", "user"); // default role

            client.crearUsuario(userData);

        } catch (Exception e) {
            Toast.makeText(this, "Error creando usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleResponse(JSONObject resp) {
        try {
            if (resp.has("status") && resp.getString("status").equals("OK")) {
                Toast.makeText(this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else if (resp.has("error") && resp.getString("error").equals("USERNAME_TAKEN")) {
                Toast.makeText(this, "Nombre de usuario ya existente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al crear usuario", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
    }
}
