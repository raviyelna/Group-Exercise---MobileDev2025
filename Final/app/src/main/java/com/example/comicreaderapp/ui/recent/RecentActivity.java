package com.example.comicreaderapp.ui.recent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.manga_model.AllComic;
import com.example.comicreaderapp.manga_model.AllComicsAdapter;
import com.example.comicreaderapp.manga_model.NetworkSingleton;
import com.example.comicreaderapp.readUI.MangaDetailActivity;
import com.example.comicreaderapp.ui.account.AccountActivity;
import com.example.comicreaderapp.ui.bookmarks.BookmarksActivity;
import com.example.comicreaderapp.ui.chat.ChatActivity;
import com.example.comicreaderapp.ui.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecentActivity extends AppCompatActivity {
// this is actually all manga section but Im too lazy to refactor this shit :>
    private static final String API_URL =
            "http://10.0.2.2/api/getData/request.php?r=all_manga";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comics);

        setupBottomNav();
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

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                response -> {
                    try {
                        JSONArray arr = response.getJSONArray("data");
                        list.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            AllComic c = new AllComic();
                            c.manga_id = o.getString("manga_id");
                            c.title = o.getString("title");
                            c.cover = o.getString("cover");

                            list.add(c);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Parse error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this,
                        "API error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show()
        );

        NetworkSingleton
                .getInstance(this)
                .getRequestQueue()
                .add(req);
    }

    // ---------------- Bottom Nav ----------------

    private void setupBottomNav() {

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_recent); // ✅ MENU ID

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            }
            else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
            }
            else if (id == R.id.nav_recent) {
                return true;
            }
            else if (id == R.id.nav_bookmark) {
                startActivity(new Intent(this, BookmarksActivity.class));
            }
            else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
            }

            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }
}
