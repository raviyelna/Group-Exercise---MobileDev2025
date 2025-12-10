package com.example.comicreaderapp.ui.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.ui.account.AccountActivity;
import com.example.comicreaderapp.ui.bookmarks.BookmarksActivity;
import com.example.comicreaderapp.ui.home.HomeActivity;
import com.example.comicreaderapp.ui.recent.RecentActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_chat);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            else if (id == R.id.nav_recent) {
                startActivity(new Intent(this, RecentActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            else if (id == R.id.nav_bookmark) {
                startActivity(new Intent(this, BookmarksActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }

            return true;
        });

    }
}