package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;

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
        styleSelected(btnPositive);
        styleUnselected(btnNegative);

        // Type buttons logic
        btnPositive.setOnClickListener(v -> {
            reviewType = "positiva";
            styleSelected(btnPositive);
            styleUnselected(btnNegative);
        });

        btnNegative.setOnClickListener(v -> {
            reviewType = "negativa";
            styleSelected(btnNegative);
            styleUnselected(btnPositive);
        });

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Publish button
        btnPublish.setOnClickListener(v -> publishReview());
    }

    // ----------------------------
    //   BUTTON VISUAL STYLING
    // ----------------------------

    private void styleSelected(Button btn) {
        // Animation
        btn.animate().scaleX(1.03f).scaleY(1.03f).setDuration(120).start();

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(0xFF171A1E);          // dark interior
        shape.setCornerRadius(24f);          // rounded corners
        shape.setStroke(4, 0xFF3B82F6);      // blue border

        btn.setBackground(shape);
        btn.setTextColor(0xFF3B82F6);        // blue text
        btn.setElevation(8f);
    }

    private void styleUnselected(Button btn) {
        // Animation
        btn.animate().scaleX(1f).scaleY(1f).setDuration(120).start();

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(0xFF16191D);        // darker background
        shape.setCornerRadius(24f);        // rounded corners
        shape.setStroke(2, 0xFF3B3F45);    // subtle gray border

        btn.setBackground(shape);
        btn.setTextColor(0xFFC7D5E0);      // normal text
        btn.setElevation(0f);
    }

    // ----------------------------
    //   SEND REVIEW (TEMP LOGIC)
    // ----------------------------

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
