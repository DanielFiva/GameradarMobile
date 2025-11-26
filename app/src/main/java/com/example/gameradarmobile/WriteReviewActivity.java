package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WriteReviewActivity extends AppCompatActivity {

    EditText editScore, editComment;
    Button btnPositive, btnNegative;
    Button btnPublish, btnBack;

    String reviewType = "positiva"; // default

    int gameId; // receive from intent if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // Bind views
        editScore = findViewById(R.id.editScore);
        editComment = findViewById(R.id.editComment);
        btnPositive = findViewById(R.id.btnPositive);
        btnNegative = findViewById(R.id.btnNegative);
        btnPublish = findViewById(R.id.btnPublishReview);
        btnBack = findViewById(R.id.btnBackWriteReview);

        // Receive gameId from intent
        gameId = getIntent().getIntExtra("game_id", -1);

        // Default selection
        btnPositive.setSelected(true);
        btnNegative.setSelected(false);

        // Type buttons logic
        btnPositive.setOnClickListener(v -> {
            reviewType = "positiva";
            btnPositive.setSelected(true);
            btnNegative.setSelected(false);
        });

        btnNegative.setOnClickListener(v -> {
            reviewType = "negativa";
            btnPositive.setSelected(false);
            btnNegative.setSelected(true);
        });

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Publish button
        btnPublish.setOnClickListener(v -> publishReview());
    }

    private void publishReview() {
        String scoreStr = editScore.getText().toString().trim();
        String comment = editComment.getText().toString().trim();

        if (scoreStr.isEmpty() || comment.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int score;
        try {
            score = Integer.parseInt(scoreStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Puntaje inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (score < 0 || score > 100) {
            Toast.makeText(this, "El puntaje debe estar entre 0 y 100", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement actual sending to server
        Toast.makeText(this, "Reseña lista para publicar:\nTipo: " + reviewType +
                "\nPuntaje: " + score + "\nComentario: " + comment, Toast.LENGTH_LONG).show();
    }
}
