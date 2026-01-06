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

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.LegacyApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.ui.account.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaDetailActivity extends AppCompatActivity {

    private static final String TAG = "MangaDetailActivity";

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

    private LegacyApi legacyApi;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_manga_detail);

        legacyApi = RetrofitClient.getInstance().create(LegacyApi.class);

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
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);

        legacyApi.getData("bookmark", params).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e(TAG, "bookmark check: empty response");
                        return;
                    }

                    String raw = response.body().string();
                    JSONObject root = new JSONObject(raw);
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
                } catch (Exception e) {
                    Log.e(TAG, "bookmark check parse error", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "bookmark check error", t);
            }
        });
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

        RequestBody reqBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                body.toString()
        );

        legacyApi.postData("bookmark", reqBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // toggle locally if success (we assume backend succeeded if HTTP 200)
                if (response.isSuccessful()) {
                    isBookmarked = !isBookmarked;
                    updateBookmarkIcon();
                } else {
                    Toast.makeText(MangaDetailActivity.this, "Bookmark failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MangaDetailActivity.this, "Bookmark failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "bookmark toggle error", t);
            }
        });
    }

    private void updateBookmarkIcon() {
        int color = isBookmarked
                ? getResources().getColor(R.color.accent_purple)
                : getResources().getColor(R.color.white);

        btnBookmark.setColorFilter(color);
    }

    /* ===================== LOAD MANGA DETAIL ===================== */

    private void loadMangaDetail(String mangaId) {
        Map<String, String> params = new HashMap<>();
        params.put("manga_id", mangaId);

        legacyApi.getData("manga_detail", params).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) return;

                    String raw = response.body().string();
                    JSONObject root = new JSONObject(raw);
                    JSONObject manga = root.optJSONObject("manga");

                    if (manga == null) return;

                    title.setText(manga.optString("title", "Unknown"));

                    String coverUrl = manga.optString("cover", "");
                    if (!coverUrl.isEmpty()) {
                        Glide.with(MangaDetailActivity.this)
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
                            Chip chip = new Chip(MangaDetailActivity.this);
                            chip.setText(arr.getString(i));
                            chip.setClickable(false);
                            genres.addView(chip);
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, "manga_detail parse error", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "manga_detail network error", t);
            }
        });
    }

    /* ===================== LOAD CHAPTERS ===================== */

    private void loadChapters(String mangaId) {
        Map<String, String> params = new HashMap<>();
        params.put("manga_id", mangaId);

        legacyApi.getData("chapters_simple", params).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) return;

                    String raw = response.body().string();
                    JSONObject root = new JSONObject(raw);
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

                } catch (Exception e) {
                    Log.e(TAG, "chapters_simple parse error", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "chapters_simple network error", t);
            }
        });
    }
}