package com.example.task81;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Map<String, String>> messageList;
    private EditText messageEditText;
    private Button sendButton;

    private OkHttpClient client;
    private Gson gson;
    private String userInitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null && !username.isEmpty()) {
            userInitial = String.valueOf(username.charAt(0)).toUpperCase();
        }

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, userInitial);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        client = new OkHttpClient();
        gson = new Gson();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    Map<String, String> userMessage = new HashMap<>();
                    userMessage.put("User", message);
                    userMessage.put("Llama", "");
                    messageList.add(userMessage);
                    chatAdapter.notifyDataSetChanged();
                    messageEditText.setText("");

                    sendMessageToChatBot(message);
                }
            }
        });
    }

    private void sendMessageToChatBot(String message) {
        String url = "http://10.0.2.2:5000/chat";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JsonObject json = new JsonObject();
        json.addProperty("userMessage", message);
        JsonArray chatHistory = new JsonArray();
        for (Map<String, String> msg : messageList) {
            JsonObject chatItem = new JsonObject();
            chatItem.addProperty("User", msg.get("User"));
            chatItem.addProperty("Llama", msg.get("Llama"));
            chatHistory.add(chatItem);
        }
        json.add("chatHistory", chatHistory);

        RequestBody body = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Failed to connect to ChatBot", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
                    String botResponse = responseJson.get("message").getAsString();
                    runOnUiThread(() -> {
                        Map<String, String> botMessage = new HashMap<>();
                        botMessage.put("User", "");
                        botMessage.put("Llama", botResponse);
                        messageList.add(botMessage);
                        chatAdapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }
}
