package com.example.gameradarmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class GameDetailsActivity extends AppCompatActivity {

    ImageView gameImage;
    TextView titleLabel, summaryBox, devValue, pubValue, releaseValue, ratingBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        titleLabel = findViewById(R.id.titleLabel);
        summaryBox = findViewById(R.id.summaryBox);
        gameImage = findViewById(R.id.gameImage);
        devValue = findViewById(R.id.devValue);
        pubValue = findViewById(R.id.pubValue);
        releaseValue = findViewById(R.id.releaseValue);
        ratingBox = findViewById(R.id.ratingBox);

        // Get data passed from adapter
        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String summary = intent.getStringExtra("SHORT_DESC");
        String developer = intent.getStringExtra("developer");
        String publisher = intent.getStringExtra("publisher");
        String releaseDate = intent.getStringExtra("releaseDate");
        String imageUrl = intent.getStringExtra("imageUrl");
        int rating = intent.getIntExtra("rating", 0);

        // Fill UI
        titleLabel.setText(title);
        summaryBox.setText(summary);
        devValue.setText(developer);
        pubValue.setText(publisher);
        releaseValue.setText(releaseDate);
        ratingBox.setText(String.valueOf(rating));
        if (rating <= 30) {
            ratingBox.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (rating <= 70) {
            ratingBox.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            ratingBox.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(gameImage);

        // Buttons
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        findViewById(R.id.reviewButton).setOnClickListener(v -> {
            Intent reviews = new Intent(GameDetailsActivity.this, GameReviewsActivity.class);
            reviews.putExtra("game_id", getIntent().getIntExtra("id", -1));
            reviews.putExtra("game_name", titleLabel.getText().toString());

            // Pass the userData string along
            String userDataString = getIntent().getStringExtra("userData");
            if (userDataString != null) {
                reviews.putExtra("user_data", userDataString);
            }

            startActivity(reviews);
        });

    }
}
