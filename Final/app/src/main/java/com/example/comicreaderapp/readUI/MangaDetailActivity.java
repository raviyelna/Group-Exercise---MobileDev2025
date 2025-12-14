package com.example.comicreaderapp.readUI;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.manga_model.NetworkSingleton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MangaDetailActivity extends AppCompatActivity {

    private static final String TAG = "MangaDetailActivity";
    private static final String URL = "";
    private static final String BASE_URL =  "http://10.0.2.2/api/getData/request.php";

    ImageView cover;
    TextView title, status, desc;
    ChipGroup genres;
    RecyclerView rv;

    ChapterAdapter adapter;
    List<MangaChapter> chapterList = new ArrayList<>();

    private String currentMangaId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_manga_detail);

        cover = findViewById(R.id.img_manga_cover);
        title = findViewById(R.id.tv_manga_title);
        status = findViewById(R.id.tv_manga_status);
        desc = findViewById(R.id.tv_manga_description);
        genres = findViewById(R.id.chip_group_genres);
        rv = findViewById(R.id.rv_chapters);

        currentMangaId = getIntent().getStringExtra("manga_id");
        if (currentMangaId == null) {
            Toast.makeText(this, "Missing manga_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecycler();
        loadMangaDetail(currentMangaId);
        loadChapters(currentMangaId);
    }

    private void setupRecycler() {
        adapter = new ChapterAdapter(this, chapterList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private String fullUrl(String path) {
        if (path == null || path.isEmpty()) return "";
        if (path.startsWith("http")) return path;
        return "http://10.0.2.2/api/getData/request.php" + (path.startsWith("/") ? "" : "/") + path;
    }

    /**
     * Load manga details (title, cover, description, genres, status)
     */
    private void loadMangaDetail(String mangaId) {
        String url = BASE_URL + "?r=manga_detail&manga_id=" + mangaId;

        StringRequest req = new StringRequest(
                Request.Method.GET,
                url,
                res -> {
                    try {
                        JSONObject root = new JSONObject(res);
                        JSONObject manga = root.optJSONObject("manga");

                        if (manga != null) {
                            // Set title
                            String mangaTitle = manga.optString("title", "Unknown");
                            title.setText(mangaTitle);

                            // Set cover
                            String coverUrl = manga.optString("cover", "");
                            if (!coverUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(coverUrl)
                                        .centerCrop()
                                        .placeholder(R.drawable.placeholder_cover)
                                        .error(R.drawable.placeholder_cover)
                                        .into(cover);
                            }

                            // Set description
                            String description = manga.optString("summary", "No description available");
                            desc.setText(description);

                            // Set status
                            String mangaStatus = manga.optString("status", "Unknown");
                            status.setText("Status: " + mangaStatus);

                            // Set genres
                            JSONArray genresArray = manga.optJSONArray("genres");
                            if (genresArray != null && genresArray.length() > 0) {
                                genres.removeAllViews();
                                for (int i = 0; i < genresArray.length(); i++) {
                                    String genre = genresArray.getString(i);
                                    Chip chip = new Chip(this);
                                    chip.setText(genre);
                                    chip.setClickable(false);
                                    genres.addView(chip);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "manga_detail parse error", e);
                        Toast.makeText(this, "Error loading manga details", Toast.LENGTH_SHORT).show();
                    }
                },
                err -> {
                    Log.e(TAG, "manga_detail network error", err);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        );

        NetworkSingleton.getInstance(this)
                .getRequestQueue()
                .add(req);
    }

    /**
     * Load chapters list
     */
    private void loadChapters(String mangaId) {
        String url = BASE_URL + "?r=chapters_simple&manga_id=" + mangaId;

        StringRequest req = new StringRequest(
                Request.Method.GET,
                url,
                res -> {
                    try {
                        JSONObject root = new JSONObject(res);
                        JSONArray data = root.optJSONArray("data");

                        chapterList.clear();

                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject o = data.getJSONObject(i);

                                MangaChapter c = new MangaChapter();
                                c.chapter_id = o.optString("chapter_id");
                                c.chapter_name = o.optString("chapter_name");
                                c.manga_id = mangaId;

                                chapterList.add(c);
                            }
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e(TAG, "chapters_simple parse error", e);
                    }
                },
                err -> Log.e(TAG, "chapters_simple network error", err)
        );

        NetworkSingleton.getInstance(this)
                .getRequestQueue()
                .add(req);
    }


}