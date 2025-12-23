package com.example.comicreaderapp.ui.bookmarks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ApiService;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.manga_model.FeaturedAdapter;
import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.model.BookmarkResponse;
import com.example.comicreaderapp.readUI.MangaDetailActivity;
import com.example.comicreaderapp.ui.account.AccountActivity;
import com.example.comicreaderapp.ui.account.SessionManager;
import com.example.comicreaderapp.ui.chat.ChatActivity;
import com.example.comicreaderapp.ui.home.HomeActivity;
import com.example.comicreaderapp.ui.recent.RecentActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarksActivity extends AppCompatActivity {

    private RecyclerView rvManga;
    private ProgressBar progressBar;
    private TextView tvEmpty, tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Hide status bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );
        // Optional: Also hide the action bar if you have one
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_genre_manga);

        Log.e("BACKSTACK", "isTaskRoot = " + isTaskRoot());

        tvTitle = findViewById(R.id.tv_title);
        progressBar = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);
        rvManga = findViewById(R.id.rv_manga);

        tvTitle.setText("Bookmarks");

        rvManga.setLayoutManager(new LinearLayoutManager(this));
        rvManga.setHasFixedSize(true);

        SessionManager session = new SessionManager(this);
        String userId = session.getUserId();

        loadBookmarks(userId);
    }

    private void loadBookmarks(String userId) {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        ApiService api = RetrofitClient.getApiService();

        api.getBookmarks("bookmarks", userId)
                .enqueue(new Callback<BookmarkResponse>() {
                    @Override
                    public void onResponse(
                            Call<BookmarkResponse> call,
                            Response<BookmarkResponse> response) {

                        progressBar.setVisibility(View.GONE);

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().data == null
                                || response.body().data.isEmpty()) {

                            tvEmpty.setVisibility(View.VISIBLE);
                            return;
                        }

                        // ✅ Convert Bookmark → Manga
                        List<Manga> mangaList =
                                BookmarkMapper.toMangaList(response.body().data);

                        // ✅ REUSE FeaturedAdapter
                        FeaturedAdapter adapter = new FeaturedAdapter(
                                BookmarksActivity.this,
                                mangaList,
                                manga -> {
                                    Intent i = new Intent(
                                            BookmarksActivity.this,
                                            MangaDetailActivity.class
                                    );
                                    i.putExtra("manga_id", manga.manga_id);
                                    startActivity(i);
                                }
                        );

                        rvManga.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<BookmarkResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav == null) return;

        nav.setSelectedItemId(R.id.nav_bookmark);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_recent) {
                startActivity(new Intent(this, RecentActivity.class));
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                return true;
            }

            overridePendingTransition(0, 0);
            return true;
        });
    }



}
