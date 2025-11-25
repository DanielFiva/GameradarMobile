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
        String summary = intent.getStringExtra("summary");
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

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(gameImage);

        // Buttons
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        findViewById(R.id.reviewButton).setOnClickListener(v -> {
            // TODO: open reviews page
        });
    }
}
