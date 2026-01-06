package com.example.comicreaderapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.ui.auth.LoginActivity;
import com.example.comicreaderapp.ui.bookmarks.BookmarksActivity;
import com.example.comicreaderapp.ui.chat.ChatActivity;
import com.example.comicreaderapp.ui.home.HomeActivity;
import com.example.comicreaderapp.ui.profile.EditProfileActivity;
import com.example.comicreaderapp.ui.recent.RecentActivity;
import com.example.comicreaderapp.utils.BottomNavUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class AccountActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private ImageView imgAvatar;
    private MaterialButton btn;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Setup bottom navigation using the shared helper to keep behavior consistent
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav != null) {
            nav.bringToFront();
            BottomNavUtils.apply(nav, this);
            BottomNavUtils.setupNavigation(nav, this, R.id.nav_account);
        }

        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        imgAvatar = findViewById(R.id.img_avatar);
        btn = findViewById(R.id.btn);

        session = new SessionManager(this);

        renderUI();

        btn.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                // ===== LOGOUT =====
                session.clear();
                renderUI();
            } else {
                // ===== LOGIN =====
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        imgAvatar.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                startActivity(new Intent(this, EditProfileActivity.class));
            }
        });
    }

    private void renderUI() {
        if (session.isLoggedIn()) {
            // ===== LOGGED IN =====
            tvUserName.setText(session.getUsername());
            tvUserEmail.setText(session.getEmail());
            btn.setText("Logout");

            String avatarUrl = session.getAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(avatarUrl)
                        .circleCrop()
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.ic_account);
            }

        } else {
            // ===== ANONYMOUS =====
            tvUserName.setText("Anonymous User");
            tvUserEmail.setText("Anonymous@example.com");
            imgAvatar.setImageResource(R.drawable.ic_account);
            btn.setText("Log In");
        }
    }
}