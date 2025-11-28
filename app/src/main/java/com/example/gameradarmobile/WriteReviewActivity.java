package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;

import org.json.JSONObject;

public class WriteReviewActivity extends AppCompatActivity {

    EditText editScore, editComment;
    Button btnPositive, btnNegative;
    Button btnPublish, btnBack;

    String reviewType = "positiva"; // default
    int gameId; // receive from intent
    int userId; // receive from intent

    SocketClient client;

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

        // Receive gameId and userId from intent
        gameId = getIntent().getIntExtra("game_id", -1);
        userId = getIntent().getIntExtra("userId", -1);

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
    // BUTTON VISUAL STYLING
    // ----------------------------
    private void styleSelected(Button btn) {
        btn.animate().scaleX(1.03f).scaleY(1.03f).setDuration(120).start();
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(0xFF171A1E);
        shape.setCornerRadius(24f);
        shape.setStroke(4, 0xFF3B82F6);
        btn.setBackground(shape);
        btn.setTextColor(0xFF3B82F6);
        btn.setElevation(8f);
    }

    private void styleUnselected(Button btn) {
        btn.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(0xFF16191D);
        shape.setCornerRadius(24f);
        shape.setStroke(2, 0xFF3B3F45);
        btn.setBackground(shape);
        btn.setTextColor(0xFFC7D5E0);
        btn.setElevation(0f);
    }

    // ----------------------------
    // SEND REVIEW TO SERVER
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

        try {
            // Create a new client instance
            client = new SocketClient("8.tcp.ngrok.io", 16743, new SocketClient.MessageListener() {
                @Override
                public void onConnected() {
                    try {
                        JSONObject reviewJson = new JSONObject();
                        reviewJson.put("game_id", gameId);
                        reviewJson.put("user_id", userId);
                        reviewJson.put("review_text", comment);
                        reviewJson.put("rating", score);
                        reviewJson.put("type", reviewType);

                        // Send review
                        client.enviarReview(reviewJson);

                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(WriteReviewActivity.this,
                                "Error creando JSON: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onGenericResponse(JSONObject resp) {
                    runOnUiThread(() -> {
                        try {
                            if ("OK".equals(resp.optString("status"))) {
                                Toast.makeText(WriteReviewActivity.this,
                                        "Reseña publicada correctamente", Toast.LENGTH_SHORT).show();
                                client.disconnect();

                                // Set result to notify GameReviewsActivity to reload reviews
                                setResult(RESULT_OK);
                                finish(); // close this activity
                            } else {
                                String error = resp.optString("error", "Error desconocido");
                                Toast.makeText(WriteReviewActivity.this,
                                        "No se pudo publicar la reseña: " + error, Toast.LENGTH_SHORT).show();
                                client.disconnect();
                            }
                        } catch (Exception e) {
                            Toast.makeText(WriteReviewActivity.this,
                                    "Error procesando respuesta: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            client.disconnect();
                        }
                    });
                }

                @Override public void onNewMessage(JSONObject msg) {}
                @Override public void onMessageHistory(JSONObject history) {}
            });

            client.connect();

        } catch (Exception e) {
            Toast.makeText(this, "Error conectando al servidor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
