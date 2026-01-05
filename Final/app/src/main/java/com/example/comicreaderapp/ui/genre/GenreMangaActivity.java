package com.example.comicreaderapp.ui.genre;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.manga_model.FeaturedAdapter;
import com.example.comicreaderapp.manga_model.NetworkSingleton;
import com.example.comicreaderapp.readUI.MangaDetailActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GenreMangaActivity extends AppCompatActivity {

    private static final String TAG = "GenreMangaActivity";
    private static final String BASE_URL = "http://10.0.2.2/api/getData/request.php";

    RecyclerView rv;
    ProgressBar progress;
    TextView tvTitle, tvEmpty;

    ArrayList<Manga> list = new ArrayList<>();
    FeaturedAdapter adapter;

    String genreId, genreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_manga);

        genreId = getIntent().getStringExtra("genre_id");
        genreName = getIntent().getStringExtra("genre_name");

        tvTitle = findViewById(R.id.tv_title);
        rv = findViewById(R.id.rv_manga);
        progress = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);

        tvTitle.setText(genreName);

        adapter = new FeaturedAdapter(this, list, m -> {
            Intent i = new Intent(this, MangaDetailActivity.class);
            i.putExtra("manga_id", m.manga_id);
            startActivity(i);
        });

        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(adapter);

        loadByGenre();
    }

    private void loadByGenre() {
        progress.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        String url = BASE_URL + "?r=manga_by_genre&genre_id=" + genreId;

        StringRequest sr = new StringRequest(Request.Method.GET, url, res -> {
            try {
                JSONObject root = new JSONObject(res);
                JSONArray arr = root.optJSONArray("data");
                list.clear();

                if (arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        Manga m = new Manga();
                        m.manga_id = o.optString("manga_id");
                        m.title = o.optString("title");
                        m.cover = ensureFullUrl(o.optString("cover"));
                        list.add(m);
                    }
                }

                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);

            } catch (Exception e) {
                Log.e(TAG, "parse error", e);
                Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
            } finally {
                progress.setVisibility(View.GONE);
            }
        }, err -> {
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
        });

        NetworkSingleton.getInstance(this).getRequestQueue().add(sr);
    }

    private String ensureFullUrl(String cover) {
        if (cover == null || cover.isEmpty()) return "";
        if (cover.startsWith("http")) return cover;
        return "http://10.0.2.2" + (cover.startsWith("/") ? "" : "/") + cover;
    }
}
