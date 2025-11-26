package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
    int userId;

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
        btnWrite.setOnClickListener(v -> {
            Intent intent = new Intent(GameReviewsActivity.this, WriteReviewActivity.class);
            intent.putExtra("game_id", gameId);
            intent.putExtra("user_id", userId);
            startActivityForResult(intent, 100);
        });

        gameId = getIntent().getIntExtra("game_id", -1);

        client = new SocketClient("2.tcp.ngrok.io", 12632, new SocketClient.MessageListener() {
            @Override
            public void onConnected() {
                client.solicitarReviews(gameId);
            }

            @Override public void onNewMessage(JSONObject msg) {}
            @Override public void onMessageHistory(JSONObject history) {}

            @Override
            public void onGenericResponse(JSONObject resp) {
                runOnUiThread(() -> handleServerResponse(resp));
            }
        });

        client.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (client != null) client.solicitarReviews(gameId);
        }
    }

    private void handleServerResponse(JSONObject resp) {
        try {
            if (resp.has("reviews")) {
                JSONArray arr = resp.getJSONArray("reviews");
                reviewList.clear();

                for (int i = 0; i < arr.length(); i++) {
                    JSONArray r = arr.getJSONArray(i);
                    int reviewId = r.getInt(0);
                    int userId = r.getInt(1);
                    int gameId = r.getInt(2);
                    int rating = r.getInt(3);
                    String comment = r.getString(4);
                    String date = r.getString(5);
                    String type = r.getString(6);

                    // Create Review with temporary username as userId string
                    Review review = new Review(reviewId, String.valueOf(userId), gameId, rating, comment, date, type);
                    reviewList.add(review);

                    // Fetch username asynchronously
                    int finalIndex = reviewList.size() - 1;
                    client.solicitarUsuario(userId);
                }

                adapter.notifyDataSetChanged();
            }

            // Handle username responses
            if ("USER_DATA".equals(resp.optString("type")) && "OK".equals(resp.optString("status"))) {
                JSONArray userData = resp.getJSONArray("user_data");
                int userIdResp = userData.optInt(0);
                String username = userData.optString(1, "Unknown");

                // Update the review in the list
                for (Review review : reviewList) {
                    if (review.username.equals(String.valueOf(userIdResp))) {
                        review.username = username;
                    }
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
