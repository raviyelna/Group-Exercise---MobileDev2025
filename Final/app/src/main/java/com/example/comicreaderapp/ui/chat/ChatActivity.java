package com.example.comicreaderapp.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ChatApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.Conversation;
import com.example.comicreaderapp.model.Message;
import com.example.comicreaderapp.ui.account.AccountActivity;
import com.example.comicreaderapp.ui.account.SessionManager;
import com.example.comicreaderapp.ui.bookmarks.BookmarksActivity;
import com.example.comicreaderapp.ui.home.HomeActivity;
import com.example.comicreaderapp.ui.recent.RecentActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    RecyclerView rv;
    ChatApi api;
    String myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 🔐 Get logged-in user
        SessionManager session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
            return;
        }

        this.myUserId = session.getUserId();
        //Log.d("CHAT_DEBUG", "myUserId = " + myUserId);


        rv = findViewById(R.id.rv_messages);
        rv.setLayoutManager(new LinearLayoutManager(this));

        api = RetrofitClient.getInstance().create(ChatApi.class);

        setupBottomNav();
        loadConversations();
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
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
        api.getMessages(conversationId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call,
                                   Response<List<Message>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    rv.setAdapter(new MessageAdapter(res.body(), myUserId));
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
