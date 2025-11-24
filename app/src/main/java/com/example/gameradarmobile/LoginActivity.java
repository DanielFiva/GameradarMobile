package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    SocketClient client;

    EditText usernameInput, passwordInput;
    Button btnLogin, btnExit, btnCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.inputUsername);
        passwordInput = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnExit = findViewById(R.id.btnExit);
        btnCreateUser = findViewById(R.id.btnCreateUser);

        // Disable login until connected
        btnLogin.setEnabled(false);

        // --- Connect to server ---
        client = new SocketClient("4.tcp.ngrok.io", 12761, new SocketClient.MessageListener() {

            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Conectado al servidor", Toast.LENGTH_SHORT).show();
                });
            }

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

        // --- Login button ---
        btnLogin.setOnClickListener(v -> {
            String user = usernameInput.getText().toString();
            String pass = passwordInput.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Rellena usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            client.login(user, pass);
        });

        // --- Exit button ---
        btnExit.setOnClickListener(v -> finishAffinity());

        // --- Create user button ---
        btnCreateUser.setOnClickListener(v -> {
            // Intent intent = new Intent(this, CreateUserActivity.class);
            // startActivity(intent);
        });
    }


    private void handleResponse(JSONObject resp) {
        try {
            if (resp.has("status") && resp.getString("status").equals("OK")) {

                JSONArray userArray = resp.getJSONArray("user_data");
                JSONArray userData = userArray.getJSONArray(0);

                // Start CatalogActivity and pass the user data as a string
                Intent intent = new Intent(this, CatalogActivity.class);
                intent.putExtra("user_data", userData.toString());
                startActivity(intent);

                // Optionally finish the login so user can't go back to it
                finish();

            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
    }
}
