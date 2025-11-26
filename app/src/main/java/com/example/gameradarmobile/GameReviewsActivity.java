package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameReviewsActivity extends AppCompatActivity {

    RecyclerView recyclerReviews;
    ReviewAdapter adapter;
    ArrayList<Review> reviewList = new ArrayList<>();

    SocketClient client;
    int gameId;

    TextView title;
    Button btnBack, btnWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_reviews);

        recyclerReviews = findViewById(R.id.reviewsRecycler);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReviewAdapter(reviewList);
        recyclerReviews.setAdapter(adapter);

        title = findViewById(R.id.titleReviews);
        btnBack = findViewById(R.id.btnBackReviews);
        btnWrite = findViewById(R.id.btnWriteReview);

        btnBack.setOnClickListener(v -> finish());

        // Receive game_id
        gameId = getIntent().getIntExtra("game_id", -1);

        client = new SocketClient("2.tcp.ngrok.io", 12632, new SocketClient.MessageListener() {
            @Override public void onConnected() {
                client.solicitarReviews(gameId);
            }

            @Override public void onNewMessage(JSONObject msg) {}

            @Override public void onMessageHistory(JSONObject history) {}

            @Override public void onGenericResponse(JSONObject resp) {
                runOnUiThread(() -> handleServerResponse(resp));
            }
        });

        client.connect();
    }

    private void handleServerResponse(JSONObject resp) {
        try {
            if (resp.has("reviews")) {

                JSONArray arr = resp.getJSONArray("reviews");
                reviewList.clear();

                for (int i = 0; i < arr.length(); i++) {
                    JSONArray r = arr.getJSONArray(i);

                    Review review = new Review(
                            r.getInt(0),      // review_id
                            r.getInt(1),      // user_id
                            r.getInt(2),      // game_id
                            r.getInt(3),      // rating
                            r.getString(4),   // comment
                            r.getString(5),   // creation_date
                            r.getString(6)    // type
                    );

                    reviewList.add(review);
                }

                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }
}
