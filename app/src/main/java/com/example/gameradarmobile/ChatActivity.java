package com.example.gameradarmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    LinearLayout messageContainer;
    EditText messageInput;
    Button sendButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageContainer = findViewById(R.id.messageContainer);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if(!msg.isEmpty()) {
                addMessage(msg, true); // true = user message
                messageInput.setText("");

                // TODO: Send to server or simulate reply
                // Example: addMessage("Bot reply to: " + msg, false);
            }
        });
    }

    private void addMessage(String message, boolean isUser) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(isUser ? 0xFFdcddde : 0xFFaaaaaa);
        tv.setBackgroundColor(isUser ? 0xFF5C7E10 : 0xFF40444b);
        tv.setPadding(16, 8, 16, 8);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);
        params.gravity = isUser ? android.view.Gravity.END : android.view.Gravity.START;
        tv.setLayoutParams(params);

        messageContainer.addView(tv);

        // Scroll to bottom
        messageContainer.post(() -> ((ScrollView) messageContainer.getParent()).fullScroll(View.FOCUS_DOWN));
    }
}
