package com.example.comicreaderapp.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ChatApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.api.SendMessageBody;
import com.example.comicreaderapp.model.Conversation;
import com.example.comicreaderapp.model.Message;
import com.example.comicreaderapp.ui.account.AccountActivity;
import com.example.comicreaderapp.ui.account.SessionManager;
import com.example.comicreaderapp.ui.bookmarks.BookmarksActivity;
import com.example.comicreaderapp.ui.home.HomeActivity;
import com.example.comicreaderapp.ui.recent.RecentActivity;
import com.example.comicreaderapp.utils.BottomNavUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    RecyclerView rv;
    ChatApi api;
    String myUserId;

    LinearLayout inputBar;
    EditText etMessage;
    ImageButton btnSend;

    int currentConversationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SessionManager session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
            return;
        }

        myUserId = session.getUserId();

        inputBar = findViewById(R.id.chat_input_bar);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        inputBar.setVisibility(View.GONE);
        ImageButton btnBack = findViewById(R.id.btn_back);
        rv = findViewById(R.id.rv_messages);
        rv.setLayoutManager(new LinearLayoutManager(this));

        api = RetrofitClient.getInstance().create(ChatApi.class);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav != null) {
            nav.bringToFront();
            BottomNavUtils.apply(nav, this);
            BottomNavUtils.setupNavigation(nav, this, R.id.nav_chat);
        }


        loadConversations();

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty() || currentConversationId == -1) return;

            SendMessageBody body = new SendMessageBody(
                    String.valueOf(currentConversationId),
                    myUserId,
                    text
            );

            api.sendMessage(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call,
                                       Response<Map<String, Object>> res) {
                    if (res.isSuccessful()) {
                        etMessage.setText("");
                        openChat(currentConversationId);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        });

        btnBack.setOnClickListener(v -> {
            if (currentConversationId != -1) {
                // If currently inside a chat → go back to conversation list
                currentConversationId = -1;
                inputBar.setVisibility(View.GONE);
                loadConversations();
            } else {
                // Otherwise → exit ChatActivity
                finish();
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav == null) return;
        nav.setSelectedItemId(R.id.nav_chat);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_recent) {
                startActivity(new Intent(this, RecentActivity.class));
            } else if (id == R.id.nav_bookmark) {
                startActivity(new Intent(this, BookmarksActivity.class));
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
            } else if (id == R.id.nav_chat) {
                return true;
            }

            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    void loadConversations() {
        api.getConversations(myUserId).enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call,
                                   Response<List<Conversation>> res) {


                if (res.isSuccessful() && res.body() != null) {
                    rv.setAdapter(new ConversationAdapter(res.body(), c -> {
                        openChat(c.conversationId);
                    }));
                }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void openChat(int conversationId) {
        currentConversationId = conversationId;
        inputBar.setVisibility(View.VISIBLE);

        api.getMessages(conversationId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call,
                                   Response<List<Message>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    rv.setAdapter(new MessageAdapter(ChatActivity.this, res.body()));
                    rv.scrollToPosition(res.body().size() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }


}
