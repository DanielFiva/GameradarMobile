package com.example.gameradarmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class CatalogActivity extends AppCompatActivity {

    EditText searchInput;
    RecyclerView recyclerGames;
    Button btnVerUsuarios, btnBottomAction, btnBack;
    GameAdapter adapter;
    JSONArray userData = null;

    SocketClient client;
    int currentPage = 0;
    boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Catálogo");

        searchInput = findViewById(R.id.searchInput);
        recyclerGames = findViewById(R.id.recyclerGames);
        btnVerUsuarios = findViewById(R.id.btnVerUsuarios);
        btnVerUsuarios.setVisibility(View.GONE);
        btnBottomAction = findViewById(R.id.btnBottomAction);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        String userDataString = intent.getStringExtra("user_data");
        if (userDataString != null) {
            try {
                userData = new JSONArray(userDataString);
            } catch (Exception e) {
                Toast.makeText(this, "Error parseando user_data", Toast.LENGTH_SHORT).show();
            }
        }

        adapter = new GameAdapter();
        recyclerGames.setLayoutManager(new LinearLayoutManager(this));
        recyclerGames.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnVerUsuarios.setOnClickListener(v -> Toast.makeText(this, "Ver usuarios (pendiente)", Toast.LENGTH_SHORT).show());
        btnBottomAction.setOnClickListener(v -> {
            Intent chatIntent = new Intent(CatalogActivity.this, ChatActivity.class);
            startActivity(chatIntent);
        });

        recyclerGames.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (!loading && lm != null && lm.findLastVisibleItemPosition() >= adapter.getItemCount() - 5) {
                        loading = true;
                        currentPage++;
                        client.solicitarJuegos(searchInput.getText().toString(), currentPage);
                    }
                }
            }
        });

        client = new SocketClient("2.tcp.ngrok.io", 12632, new SocketClient.MessageListener() {
            @Override public void onConnected() { client.solicitarJuegos("", 0); }
            @Override public void onNewMessage(JSONObject msg) {}
            @Override public void onMessageHistory(JSONObject history) {}
            @Override public void onGenericResponse(JSONObject resp) { runOnUiThread(() -> handleServerResponse(resp)); }
        });
        client.connect();
    }

    private void handleServerResponse(JSONObject resp) {
        try {
            if (resp.has("type") && resp.getString("type").equals("GAMES")) {
                if (resp.getString("status").equals("OK")) {
                    JSONArray games = resp.getJSONArray("games");
                    adapter.addGames(games);
                    loading = false;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error procesando juegos", Toast.LENGTH_SHORT).show();
        }
    }
}
