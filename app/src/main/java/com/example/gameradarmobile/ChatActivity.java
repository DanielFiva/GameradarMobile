package com.example.gameradarmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerChat;
    EditText inputChat;
    Button btnSend, btnBack;

    ArrayList<ChatMessage> messages = new ArrayList<>();
    ChatAdapter adapter;

    SocketClient client;
    String username = "User"; // Optional: pass real username via Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerChat = findViewById(R.id.recyclerChat);
        inputChat = findViewById(R.id.inputChat);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);

        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(messages);
        recyclerChat.setAdapter(adapter);

        if (getIntent().hasExtra("username"))
            username = getIntent().getStringExtra("username");

        client = new SocketClient("2.tcp.ngrok.io", 12632, new SocketClient.MessageListener() {
            @Override public void onConnected() {
                client.solicitarMensajesChat();
            }

            @Override public void onNewMessage(JSONObject msg) {
                runOnUiThread(() -> addNewMessage(msg));
            }

            @Override public void onMessageHistory(JSONObject history) {
                runOnUiThread(() -> loadHistory(history));
            }

            @Override public void onGenericResponse(JSONObject resp) {}
        });

        client.connect();

        btnSend.setOnClickListener(v -> {
            String text = inputChat.getText().toString().trim();
            if (text.isEmpty()) return;

            client.enviarMensajeChat(username, text);
            inputChat.setText("");
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadHistory(JSONObject history) {
        try {
            JSONArray arr = history.getJSONArray("messages");
            messages.clear();

            for (int i = 0; i < arr.length(); i++) {
                JSONArray row = arr.getJSONArray(i);
                messages.add(new ChatMessage(row.getString(0), row.getString(1)));
            }

            adapter.notifyDataSetChanged();
            recyclerChat.scrollToPosition(messages.size() - 1);

        } catch (Exception ignored) {}
    }

    private void addNewMessage(JSONObject msg) {
        try {
            String sender = msg.getString("sender");
            String message = msg.getString("message");

            messages.add(new ChatMessage(sender, message));
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerChat.scrollToPosition(messages.size() - 1);

        } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }
}
