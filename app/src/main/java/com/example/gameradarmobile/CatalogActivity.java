package com.example.gameradarmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class CatalogActivity extends AppCompatActivity {

    EditText searchInput;
    RecyclerView recyclerGames;
    Button btnVerUsuarios, btnBottomAction, btnBack;
    TextView txtNoResults;
    GameAdapter adapter;
    JSONArray userData = null;

    SocketClient client;
    int currentPage = 0;
    boolean loading = false;

    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Catálogo");

        searchInput = findViewById(R.id.searchInput);
        recyclerGames = findViewById(R.id.recyclerGames);
        txtNoResults = findViewById(R.id.txtNoResults);
        btnVerUsuarios = findViewById(R.id.btnVerUsuarios);
        btnVerUsuarios.setVisibility(View.GONE);
        btnBottomAction = findViewById(R.id.btnBottomAction);
        btnBack = findViewById(R.id.btnBack);

        String userDataString = getIntent().getStringExtra("user_data");
        if (userDataString != null) {
            try {
                userData = new JSONArray(userDataString);
            } catch (Exception e) {
                Toast.makeText(this, "Error parseando user_data", Toast.LENGTH_SHORT).show();
            }
        }

        client = new SocketClient("2.tcp.ngrok.io", 12632, new SocketClient.MessageListener() {
            @Override
            public void onConnected() { requestGames(""); }

            @Override public void onNewMessage(JSONObject msg) {}
            @Override public void onMessageHistory(JSONObject history) {}
            @Override public void onGenericResponse(JSONObject resp) {
                runOnUiThread(() -> handleServerResponse(resp));
            }
        });
        client.connect();

        adapter = new GameAdapter(client); // pass client to adapter
        recyclerGames.setLayoutManager(new LinearLayoutManager(this));
        recyclerGames.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnVerUsuarios.setOnClickListener(v -> Toast.makeText(this, "Ver usuarios (pendiente)", Toast.LENGTH_SHORT).show());
        btnBottomAction.setOnClickListener(v -> {
            Intent chatIntent = new Intent(CatalogActivity.this, ChatActivity.class);
            chatIntent.putExtra("user_data", userData.toString());
            startActivity(chatIntent);
        });

        // Scroll listener for pagination
        recyclerGames.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (!loading && lm != null && lm.findLastVisibleItemPosition() >= adapter.getItemCount() - 5) {
                        loading = true;
                        currentPage++;
                        requestGames(searchInput.getText().toString());
                    }
                }
            }
        });

        // Real-time search
        searchInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(String s) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);

                searchRunnable = () -> {
                    adapter.clearGames(); // clear previous results
                    currentPage = 0;
                    requestGames(s);
                };

                // delay search by 400ms to reduce server load
                searchHandler.postDelayed(searchRunnable, 400);
            }
        });
    }

    private void requestGames(String query) {
        if (client != null) client.solicitarJuegos(query, currentPage);
    }

    private void handleServerResponse(JSONObject resp) {
        try {
            if (resp.has("type")) {
                switch (resp.getString("type")) {
                    case "GAMES":
                        if (resp.getString("status").equals("OK")) {
                            JSONArray games = resp.getJSONArray("games");
                            adapter.addGames(games);
                            loading = false;

                            // Show no results if empty
                            if (adapter.getItemCount() == 0) {
                                txtNoResults.setVisibility(View.VISIBLE);
                            } else {
                                txtNoResults.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case "GAME_DATA":
                        if (resp.getString("status").equals("OK")) {
                            JSONArray gameArray = resp.getJSONArray("game_data");

                            if (gameArray.length() >= 8) {
                                int gameId = gameArray.optInt(0);
                                String name = gameArray.optString(1);
                                String description = gameArray.optString(2);
                                String imageUrl = gameArray.optString(3);
                                int metacriticScore = gameArray.optInt(4);
                                String releaseDate = gameArray.optString(5);
                                String developer = gameArray.optString(6);
                                String publisher = gameArray.optString(7);

                                Intent intent = new Intent(CatalogActivity.this, GameDetailsActivity.class);
                                intent.putExtra("id", gameId);
                                intent.putExtra("title", name);
                                intent.putExtra("SHORT_DESC", description);
                                intent.putExtra("imageUrl", imageUrl);
                                intent.putExtra("rating", metacriticScore);
                                intent.putExtra("releaseDate", releaseDate);
                                intent.putExtra("developer", developer);
                                intent.putExtra("publisher", publisher);
                                startActivity(intent);
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error procesando respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
    }
}
