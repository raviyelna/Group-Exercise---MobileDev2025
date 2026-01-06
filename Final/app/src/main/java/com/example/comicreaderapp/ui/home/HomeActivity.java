package com.example.comicreaderapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.LegacyApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.manga_model.CategoryAdapter;
import com.example.comicreaderapp.manga_model.CategoryItem;
import com.example.comicreaderapp.manga_model.FeaturedAdapter;
import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.manga_model.RecentChapter;
import com.example.comicreaderapp.manga_model.RecentManga;
import com.example.comicreaderapp.manga_model.RecentMangaAdapter;
import com.example.comicreaderapp.readUI.MangaDetailActivity;
import com.example.comicreaderapp.ui.genre.GenreMangaActivity;
import com.example.comicreaderapp.ui.reader.ComicReaderActivity;
import com.example.comicreaderapp.utils.BottomNavUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    // RecyclerViews
    private RecyclerView rvFeatured, rvCategories, rvRecently;

    // Adapters & lists
    private FeaturedAdapter featuredAdapter;
    private CategoryAdapter categoryAdapter;
    private RecentMangaAdapter recentMangaAdapter;

    private ArrayList<Manga> featuredList = new ArrayList<>();
    private ArrayList<CategoryItem> categories = new ArrayList<>();

    private ArrayList<RecentManga> recentMangaList = new ArrayList<>();

    // UI helpers
    private ProgressBar progressFeatured, progressCategories, progressRecent;
    private TextView emptyFeatured, emptyCategories, emptyRecent;
    private SwipeRefreshLayout swipeRefresh;

    private LegacyApi legacyApi;

    // Executor for parsing JSON / heavy tasks off main thread
    private final ExecutorService parseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        legacyApi = RetrofitClient.getInstance().create(LegacyApi. class);

        // ========== SETUP BOTTOM NAVIGATION FIRST ==========
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav != null) {
            Log.d(TAG, "✅ Bottom nav found");
            nav.bringToFront();

            // Apply styling
            BottomNavUtils.apply(nav, this);

            // Setup navigation
            BottomNavUtils.setupNavigation(nav, this, R.id.nav_home);

            Log.d(TAG, "Bottom nav setup complete.  Menu size: " + nav.getMenu().size());
        } else {
            Log.e(TAG, "❌ bottom_nav is NULL!  Check layout file.");
        }

        // UI init
        ImageButton btnSearch = findViewById(R.id.btn_search);
        TextView tvTitle = findViewById(R.id.tv_home_title);

        rvFeatured = findViewById(R.id. rv_featured);
        rvCategories = findViewById(R.id. rv_categories);
        rvRecently = findViewById(R.id.rv_recently_updated);

        progressFeatured = findViewById(R.id. progress_featured);
        progressCategories = findViewById(R.id. progress_categories);
        progressRecent = findViewById(R.id.progress_recent);

        emptyFeatured = findViewById(R.id.empty_featured);
        emptyCategories = findViewById(R. id.empty_categories);
        emptyRecent = findViewById(R.id.empty_recent);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh. setOnRefreshListener(() -> {
                loadFeatured();
                loadCategories();
                loadRecentChapters();
            });
        }

        setupRecyclerViews();

        // initial load
        loadFeatured();
        loadCategories();
        loadRecentChapters();
    }

    private void setupRecyclerViews() {
        // featured
        featuredAdapter = new FeaturedAdapter(this, featuredList, m -> {
            openMangaDetail(m.manga_id);
        });
        rvFeatured.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFeatured.setAdapter(featuredAdapter);

        // categories
        categoryAdapter = new CategoryAdapter(categories, c -> {
            Intent i = new Intent(HomeActivity.this, GenreMangaActivity.class);
            i.putExtra("genre_id", c.id);
            i.putExtra("genre_name", c.name);
            startActivity(i);
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
        rvCategories.setNestedScrollingEnabled(false);

        // recent mangas (each card shows up to 3 chapters)
        recentMangaAdapter = new RecentMangaAdapter(this, recentMangaList, new RecentMangaAdapter.OnClick() {
            @Override
            public void onOpenChapter(RecentManga manga, RecentChapter chapter) {
                openReader(chapter.chapter_id, chapter.chapter_name, manga.manga_id);
            }

            @Override
            public void onOpenManga(RecentManga manga) {
                openMangaDetail(manga.manga_id);
            }
        });
        rvRecently.setLayoutManager(new LinearLayoutManager(this));
        rvRecently.setAdapter(recentMangaAdapter);
        rvRecently.setNestedScrollingEnabled(false);
    }

    private void openMangaDetail(String mangaId) {
        if (mangaId == null || mangaId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid manga ID", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MangaDetailActivity.class);
        intent.putExtra("manga_id", mangaId);
        startActivity(intent);
    }

    private void openReader(String chapterId, String chapterName, String mangaId) {
        Intent intent = new Intent(this, ComicReaderActivity.class);
        intent.putExtra("chapter_id", chapterId);
        intent.putExtra("chapter_name", chapterName);
        intent.putExtra("manga_id", mangaId);
        startActivity(intent);
    }

    private void showProgress(ProgressBar p, boolean show) {
        if (p == null) return;
        p.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmpty(TextView t, boolean show) {
        if (t == null) return;
        t.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private String ensureFullUrl(String cover) {
        if (cover == null || cover.isEmpty()) return "";
        if (cover.startsWith("http://") || cover.startsWith("https://")) return cover;
        return "http://10.0.2.2" + (cover.startsWith("/") ? "" : "/") + cover;
    }

    private void loadFeatured() {
        Log.d(TAG, "loadFeatured: start");
        showProgress(progressFeatured, true);
        showEmpty(emptyFeatured, false);

        Call<ResponseBody> call = legacyApi.getData("featured", java.util.Collections.singletonMap("limit", "10"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "loadFeatured:onResponse code=" + response.code() + " success=" + response.isSuccessful());
                showProgress(progressFeatured, false);

                if (!response.isSuccessful()) {
                    Log.w(TAG, "loadFeatured: response not successful, code=" + response.code());
                    Toast.makeText(HomeActivity.this, "Server error loading featured: code=" + response.code(), Toast.LENGTH_LONG).show();
                    showEmpty(emptyFeatured, true);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    return;
                }
                if (response.body() == null) {
                    Log.w(TAG, "loadFeatured: response.body() == null");
                    Toast.makeText(HomeActivity.this, "Empty response body for featured", Toast.LENGTH_LONG).show();
                    showEmpty(emptyFeatured, true);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    return;
                }

                ResponseBody rb = response.body();
                parseExecutor.submit(() -> {
                    ArrayList<Manga> parsed = new ArrayList<>();
                    try {
                        String raw = rb.string();
                        Log.d(TAG, "loadFeatured: raw length=" + (raw != null ? raw.length() : "null"));
                        JSONObject root = new JSONObject(raw);
                        JSONArray data = root.optJSONArray("data");

                        if (data != null && data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject o = data.getJSONObject(i);
                                Manga m = new Manga();
                                m.manga_id = o.optString("manga_id");
                                m.title = o.optString("title");
                                m.summary = o.optString("summary");
                                m.cover = ensureFullUrl(o.optString("cover"));
                                m.created_at = o.optString("created_at");
                                parsed.add(m);
                            }
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "loadFeatured: parse error", e);
                    }

                    runOnUiThread(() -> {
                        featuredList.clear();
                        featuredList.addAll(parsed);
                        featuredAdapter.notifyDataSetChanged();
                        showEmpty(emptyFeatured, featuredList.isEmpty());
                        if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                        Log.d(TAG, "loadFeatured: UI updated, size=" + featuredList.size());
                    });
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "loadFeatured:onFailure", t);
                Toast.makeText(HomeActivity.this, "Network error loading featured: " + t.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(progressFeatured, false);
                showEmpty(emptyFeatured, true);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void loadCategories() {
        Log.d(TAG, "loadCategories: start");
        showProgress(progressCategories, true);
        showEmpty(emptyCategories, false);

        Call<ResponseBody> call = legacyApi.getData("genres");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "loadCategories:onResponse code=" + response.code() + " success=" + response.isSuccessful());
                showProgress(progressCategories, false);

                if (!response.isSuccessful()) {
                    Log.w(TAG, "loadCategories: response not successful, code=" + response.code());
                    Toast.makeText(HomeActivity.this, "Server error loading categories: code=" + response.code(), Toast.LENGTH_LONG).show();
                    showEmpty(emptyCategories, true);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    return;
                }
                if (response.body() == null) {
                    Log.w(TAG, "loadCategories: response.body() == null");
                    Toast.makeText(HomeActivity.this, "Empty response body for categories", Toast.LENGTH_LONG).show();
                    showEmpty(emptyCategories, true);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    return;
                }

                ResponseBody rb = response.body();
                parseExecutor.submit(() -> {
                    ArrayList<CategoryItem> parsed = new ArrayList<>();
                    try {
                        String raw = rb.string();
                        Log.d(TAG, "loadCategories: raw length=" + (raw != null ? raw.length() : "null"));
                        JSONObject root = new JSONObject(raw);
                        JSONArray arr = root.optJSONArray("data");

                        if (arr != null && arr.length() > 0) {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject g = arr.getJSONObject(i);
                                String genreId = g.optString("genre_id", "");
                                String name = g.optString("name", "");
                                parsed.add(new CategoryItem(genreId, name));
                            }
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "loadCategories: parse error", e);
                    }

                    runOnUiThread(() -> {
                        categories.clear();
                        categories.addAll(parsed);
                        categoryAdapter.notifyDataSetChanged();
                        showEmpty(emptyCategories, categories.isEmpty());
                        if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                        Log.d(TAG, "loadCategories: UI updated, size=" + categories.size());
                    });
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "loadCategories:onFailure", t);
                Toast.makeText(HomeActivity.this, "Network error loading categories: " + t.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(progressCategories, false);
                showEmpty(emptyCategories, true);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void loadRecentChapters() {
        Log.d(TAG, "loadRecentChapters: start");
        showProgress(progressRecent, true);
        showEmpty(emptyRecent, false);

        Call<ResponseBody> call = legacyApi.getData("recent_chapters", java.util.Collections.singletonMap("limit", "50"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "loadRecentChapters:onResponse code=" + response.code() + " success=" + response.isSuccessful());
                showProgress(progressRecent, false);
                if (!response.isSuccessful()) {
                    Log.w(TAG, "loadRecentChapters: response not successful, code=" + response.code());
                    Toast.makeText(HomeActivity.this, "Server error loading recent: code=" + response.code(), Toast.LENGTH_LONG).show();
                    showEmpty(emptyRecent, true);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    return;
                }
                if (response.body() == null) {
                    Log.w(TAG, "loadRecentChapters: response.body() == null");
                    Toast.makeText(HomeActivity.this, "Empty response body for recent", Toast.LENGTH_LONG).show();
                    showEmpty(emptyRecent, true);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    return;
                }

                ResponseBody rb = response.body();
                parseExecutor.submit(() -> {
                    Map<String, RecentManga> map = new LinkedHashMap<>();
                    try {
                        String raw = rb.string();
                        Log.d(TAG, "loadRecentChapters: raw length=" + (raw != null ? raw.length() : "null"));
                        JSONObject root = new JSONObject(raw);
                        JSONArray arr = root.optJSONArray("data");

                        if (arr != null) {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject o = arr.getJSONObject(i);
                                RecentChapter rc = new RecentChapter();
                                rc.chapter_id = o.optString("chapter_id");
                                rc.manga_id = o.optString("manga_id");
                                rc.chapter_name = o.optString("chapter_name");
                                rc.chapter_url = o.optString("chapter_url");
                                rc.created_at = o.optString("created_at");

                                JSONArray imgs = o.optJSONArray("images");
                                if (imgs != null) {
                                    for (int j = 0; j < imgs.length(); j++) rc.images.add(imgs.optString(j));
                                }

                                String mid = rc.manga_id != null ? rc.manga_id : "";

                                String mtitle = o.optString("manga_title", "");
                                String mcover = ensureFullUrl(o.optString("manga_cover", ""));

                                RecentManga rm = map.get(mid);
                                if (rm == null) {
                                    rm = new RecentManga();
                                    rm.manga_id = mid;
                                    rm.manga_title = mtitle;
                                    rm.manga_cover = mcover;
                                    map.put(mid, rm);
                                }
                                rm.chapters.add(rc);
                            }
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "loadRecentChapters: parse error", e);
                    }

                    ArrayList<RecentManga> newList = new ArrayList<>();
                    for (RecentManga rm : map.values()) {
                        if (rm.chapters.size() > 3) {
                            rm.chapters = new ArrayList<>(rm.chapters.subList(0, 3));
                        }
                        newList.add(rm);
                    }

                    runOnUiThread(() -> {
                        recentMangaList.clear();
                        recentMangaList.addAll(newList);
                        recentMangaAdapter.notifyDataSetChanged();
                        showEmpty(emptyRecent, recentMangaList.isEmpty());
                        if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                        Log.d(TAG, "loadRecentChapters: UI updated, size=" + recentMangaList.size());
                    });
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "loadRecentChapters:onFailure", t);
                Toast.makeText(HomeActivity.this, "Network error loading recent: " + t.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(progressRecent, false);
                showEmpty(emptyRecent, true);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // shutdown executor to avoid leaks
        parseExecutor.shutdownNow();
    }
}