package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

public class CatalogActivity extends AppCompatActivity {

    EditText searchInput;
    RecyclerView recyclerGames;
    Button btnVerUsuarios, btnBottomAction, btnBack;
    GameAdapter adapter;
    JSONArray userData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Toolbar (if present in layout)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Catálogo");
        }

        searchInput = findViewById(R.id.searchInput);
        recyclerGames = findViewById(R.id.recyclerGames);
        btnVerUsuarios = findViewById(R.id.btnVerUsuarios);
        btnBottomAction = findViewById(R.id.btnBottomAction);
        btnBack = findViewById(R.id.btnBack);

        // Get incoming user data
        Intent intent = getIntent();
        String userDataString = intent.getStringExtra("user_data");
        if (userDataString != null) {
            try {
                // store it for later use
                userData = new JSONArray(userDataString);
            } catch (Exception e) {
                Toast.makeText(this, "Error parseando user_data", Toast.LENGTH_SHORT).show();
            }
        }

        // Setup RecyclerView with an empty adapter for now
        adapter = new GameAdapter(); // initially empty
        recyclerGames.setLayoutManager(new LinearLayoutManager(this));
        recyclerGames.setAdapter(adapter);

        // Back button - finish activity
        btnBack.setOnClickListener(v -> finish());

        // "Ver usuarios" button - currently does nothing (placeholder)
        btnVerUsuarios.setOnClickListener(v -> {
            Toast.makeText(this, "Ver usuarios (pendiente)", Toast.LENGTH_SHORT).show();
            // TODO: Open users list or do something with userData
        });

        // Bottom action button - placeholder
        btnBottomAction.setOnClickListener(v -> {
            Toast.makeText(this, "Acción inferior (pendiente)", Toast.LENGTH_SHORT).show();
        });

        // TODO: add search filtering logic for adapter when you populate the adapter data
        // Example: add a TextWatcher on searchInput and filter adapter items.
    }
}
