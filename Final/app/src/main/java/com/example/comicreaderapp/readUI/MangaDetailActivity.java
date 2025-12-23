package com.example.comicreaderapp.readUI;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
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
import com.example.comicreaderapp.ui.account.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MangaDetailActivity extends AppCompatActivity {

    private static final String TAG = "MangaDetailActivity";
    private static final String BASE_URL = "http://10.0.2.2/api/getData/request.php";

    ImageView cover;
    ImageView  btnBookmark;
    TextView title, status, desc;
    ChipGroup genres;
    RecyclerView rv;

    ChapterAdapter adapter;
    List<MangaChapter> chapterList = new ArrayList<>();

    private String currentMangaId;
    private String userId;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_manga_detail);

        cover = findViewById(R.id.img_manga_cover);
        btnBookmark = findViewById(R.id.btn_bookmark);
        title = findViewById(R.id.tv_manga_title);
        status = findViewById(R.id.tv_manga_status);
        desc = findViewById(R.id.tv_manga_description);
        genres = findViewById(R.id.chip_group_genres);
        rv = findViewById(R.id.rv_chapters);

        // ✅ GET USER ID FROM SESSION
        SessionManager session = new SessionManager(this);
        userId = session.getUserId();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentMangaId = getIntent().getStringExtra("manga_id");
        if (currentMangaId == null) {
            Toast.makeText(this, "Missing manga_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecycler();
        loadMangaDetail(currentMangaId);
        loadChapters(currentMangaId);
        checkBookmarkStatus();

        btnBookmark.setOnClickListener(v -> toggleBookmark());
    }


    private void setupRecycler() {
        adapter = new ChapterAdapter(this, chapterList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    /* ===================== BOOKMARK LOGIC ===================== */

    private void checkBookmarkStatus() {
        String url = BASE_URL + "?r=bookmark&user_id=" + userId;

        StringRequest req = new StringRequest(
                Request.Method.GET,
                url,
                res -> {
                    try {
                        JSONObject root = new JSONObject(res);
                        JSONArray arr = root.optJSONArray("data");
                        isBookmarked = false;

                        if (arr != null) {
                            for (int i = 0; i < arr.length(); i++) {
                                if (currentMangaId.equals(arr.getString(i))) {
                                    isBookmarked = true;
                                    break;
                                }
                            }
                        }
                        updateBookmarkIcon();
                    } catch (JSONException e) {
                        Log.e(TAG, "bookmark check parse error", e);
                    }
                },
                err -> Log.e(TAG, "bookmark check error", err)
        );

        NetworkSingleton.getInstance(this).getRequestQueue().add(req);
    }

    private void toggleBookmark() {
        String action = isBookmarked ? "remove" : "add";

        JSONObject body = new JSONObject();
        try {
            body.put("user_id", userId);
            body.put("manga_id", currentMangaId);
            body.put("action", action);
        } catch (JSONException e) {
            return;
        }

        StringRequest req = new StringRequest(
                Request.Method.POST,
                BASE_URL + "?r=bookmark",
                res -> {
                    isBookmarked = !isBookmarked;
                    updateBookmarkIcon();
                },
                err -> Toast.makeText(this, "Bookmark failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public byte[] getBody() {
                return body.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        NetworkSingleton.getInstance(this).getRequestQueue().add(req);
    }

    private void updateBookmarkIcon() {
        int color = isBookmarked
                ? getResources().getColor(R.color.accent_purple)
                : getResources().getColor(R.color.white);

        btnBookmark.setColorFilter(color);
    }

    /* ===================== LOAD MANGA DETAIL ===================== */

    private void loadMangaDetail(String mangaId) {
        String url = BASE_URL + "?r=manga_detail&manga_id=" + mangaId;

        StringRequest req = new StringRequest(
                Request.Method.GET,
                url,
                res -> {
                    try {
                        JSONObject root = new JSONObject(res);
                        JSONObject manga = root.optJSONObject("manga");

                        if (manga == null) return;

                        title.setText(manga.optString("title", "Unknown"));

                        String coverUrl = manga.optString("cover", "");
                        if (!coverUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(coverUrl)
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder_cover)
                                    .error(R.drawable.placeholder_cover)
                                    .into(cover);
                        }

                        desc.setText(manga.optString("summary", "No description"));
                        status.setText("Status: " + manga.optString("status", "Unknown"));

                        genres.removeAllViews();
                        JSONArray arr = manga.optJSONArray("genres");
                        if (arr != null) {
                            for (int i = 0; i < arr.length(); i++) {
                                Chip chip = new Chip(this);
                                chip.setText(arr.getString(i));
                                chip.setClickable(false);
                                genres.addView(chip);
                            }
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "manga_detail parse error", e);
                    }
                },
                err -> Log.e(TAG, "manga_detail network error", err)
        );

        NetworkSingleton.getInstance(this).getRequestQueue().add(req);
    }

    /* ===================== LOAD CHAPTERS ===================== */

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

        NetworkSingleton.getInstance(this).getRequestQueue().add(req);
    }
}
