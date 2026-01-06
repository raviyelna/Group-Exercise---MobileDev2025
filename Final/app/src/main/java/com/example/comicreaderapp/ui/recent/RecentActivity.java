package com.example.comicreaderapp.ui.recent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.LegacyApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.manga_model.AllComic;
import com.example.comicreaderapp.manga_model.AllComicsAdapter;
import com.example.comicreaderapp.readUI.MangaDetailActivity;
import com.example.comicreaderapp.utils.BottomNavUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentActivity extends AppCompatActivity {
    // this is actually all manga section but Im too lazy to refactor this shit :>
    private static final String TAG = "RecentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comics);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav != null) {
            nav.bringToFront();
            BottomNavUtils.apply(nav, this);
            BottomNavUtils.setupNavigation(nav, this, R.id.nav_recent);
        }

        setupRecycler();
    }

    // ---------------- Recycler ----------------

    private void setupRecycler() {

        RecyclerView rv = findViewById(R.id.rv_recent_full);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        List<AllComic> list = new ArrayList<>();

        AllComicsAdapter adapter = new AllComicsAdapter(
                this,
                list,
                comic -> {
                    Intent i = new Intent(this, MangaDetailActivity.class);
                    i.putExtra("manga_id", comic.manga_id);
                    startActivity(i);
                }
        );

        rv.setAdapter(adapter);

        loadAllManga(list, adapter);
    }

    private void loadAllManga(List<AllComic> list, AllComicsAdapter adapter) {

        LegacyApi api = RetrofitClient.getInstance().create(LegacyApi.class);
        Call<ResponseBody> call = api.getData("all_manga");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RecentActivity.this,
                            "API error: empty response",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    String raw = response.body().string();
                    JSONObject root = new JSONObject(raw);
                    JSONArray arr = root.optJSONArray("data");
                    list.clear();

                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            AllComic c = new AllComic();
                            c.manga_id = o.optString("manga_id");
                            c.title = o.optString("title");
                            c.cover = o.optString("cover");

                            list.add(c);
                        }
                    }

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e(TAG, "Parse error", e);
                    Toast.makeText(RecentActivity.this,
                            "Parse error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RecentActivity.this,
                        "API error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Network error", t);
            }
        });
    }

    // ---------------- Bottom Nav ----------------

    private void setupBottomNav() {

        com.google.android.material.bottomnavigation.BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_recent); // ✅ MENU ID

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, com.example.comicreaderapp.ui.home.HomeActivity.class));
            }
            else if (id == R.id.nav_account) {
                startActivity(new Intent(this, com.example.comicreaderapp.ui.account.AccountActivity.class));
            }
            else if (id == R.id.nav_recent) {
                return true;
            }
            else if (id == R.id.nav_bookmark) {
                startActivity(new Intent(this, com.example.comicreaderapp.ui.bookmarks.BookmarksActivity.class));
            }
            else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, com.example.comicreaderapp.ui.chat.ChatActivity.class));
            }

            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }
}