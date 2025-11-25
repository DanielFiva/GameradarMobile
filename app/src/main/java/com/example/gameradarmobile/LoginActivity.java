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

        btnLogin.setEnabled(false);

        client = new SocketClient("2.tcp.ngrok.io", 12632, new SocketClient.MessageListener() {

            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Conectado al servidor", Toast.LENGTH_SHORT).show();
                });
            }

            @Override public void onNewMessage(JSONObject msg) {}
            @Override public void onMessageHistory(JSONObject history) {}
            @Override
            public void onGenericResponse(JSONObject resp) {
                runOnUiThread(() -> handleResponse(resp));
            }
        });

        client.connect();

        btnLogin.setOnClickListener(v -> {
            String user = usernameInput.getText().toString();
            String pass = passwordInput.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Rellena usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            client.login(user, pass);
        });

        btnExit.setOnClickListener(v -> finishAffinity());
        btnCreateUser.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateUserActivity.class);
            startActivity(intent);
        });
    }

    private void handleResponse(JSONObject resp) {
        try {
            if (resp.has("status") && resp.getString("status").equals("OK")) {

                JSONArray userArray = resp.getJSONArray("user_data");
                JSONArray userData = userArray.getJSONArray(0);
                String loggedUsername = userData.getString(1);
                Intent intent = new Intent(this, CatalogActivity.class);
                intent.putExtra("user_data", userData.toString());
                startActivity(intent);


            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
    }
}
