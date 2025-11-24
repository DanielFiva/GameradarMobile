package com.example.gameradarmobile;


import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {

    private String host;
    private int port;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private boolean connected = false;

    // Listener callbacks
    public interface MessageListener {
        void onNewMessage(JSONObject msg);
        void onMessageHistory(JSONObject history);
        void onGenericResponse(JSONObject resp);
    }

    private MessageListener listener;

    public SocketClient(String host, int port, MessageListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    public void connect() {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                output = new PrintWriter(socket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected = true;

                Log.d("CLIENT", "Connected to server");

                // Listening loop
                String line;
                while ((line = input.readLine()) != null) {
                    try {
                        JSONObject obj = new JSONObject(line);

                        if (obj.has("type")) {
                            switch (obj.getString("type")) {
                                case "NEW_MESSAGE":
                                    listener.onNewMessage(obj);
                                    break;

                                case "MESSAGE_HISTORY":
                                    listener.onMessageHistory(obj);
                                    break;

                                default:
                                    listener.onGenericResponse(obj);
                            }
                        } else {
                            listener.onGenericResponse(obj);
                        }

                    } catch (Exception e) {
                        Log.e("CLIENT", "Invalid JSON: " + line);
                    }
                }

            } catch (Exception e) {
                Log.e("CLIENT", "Connection failed: " + e);
                connected = false;
            }
        }).start();
    }

    public void disconnect() {
        try {
            connected = false;
            if (socket != null) socket.close();
        } catch (IOException e) {
            Log.e("CLIENT", "Disconnect error: " + e);
        }
    }

    public void send(String message) {
        if (connected && output != null) {
            output.println(message);
            output.flush();
        }
    }

    // High-level API (like your Python client)
    public void login(String user, String pass) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("username", user);
            obj.put("password", pass);
            send("LOGIN " + obj.toString());
        } catch (Exception ignored) {}
    }

    public void solicitarReviews(int gameId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("game_id", gameId);
            send("GET_REVIEWS " + obj.toString());
        } catch (Exception ignored) {}
    }

    public void enviarReview(JSONObject review) {
        send("ADD_REVIEW " + review.toString());
    }

    public void crearUsuario(JSONObject userData) {
        send("CREATE_USER " + userData.toString());
    }

    public void enviarMensajeChat(String username, String content) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("content", content);
            send("SEND_MESSAGE " + obj.toString());
        } catch (Exception ignored) {}
    }

    public void solicitarMensajesChat() {
        send("GET_MESSAGES");
    }
}
