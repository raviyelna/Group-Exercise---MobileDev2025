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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.manga_model.CategoryAdapter;
import com.example.comicreaderapp.manga_model.CategoryItem;
import com.example.comicreaderapp.manga_model.FeaturedAdapter;
import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.manga_model.NetworkSingleton;
import com.example.comicreaderapp.manga_model.RecentChapter;
import com.example.comicreaderapp.manga_model.RecentManga;
import com.example.comicreaderapp.manga_model.RecentMangaAdapter;
import com.example.comicreaderapp.readUI.MangaDetailActivity;
import com.example.comicreaderapp.ui.reader.ComicReaderActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.comicreaderapp.ui.account.AccountActivity;
import com.example.comicreaderapp.ui.bookmarks.BookmarksActivity;
import com.example.comicreaderapp.ui.chat.ChatActivity;
import com.example.comicreaderapp.ui.recent.RecentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String BASE_URL = "http://10.0.2.2/api/getData/request.php";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Navigation
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_recent) {
                startActivity(new Intent(this, RecentActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            if (id == R.id.nav_bookmark) {
                startActivity(new Intent(this, BookmarksActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            return false;
        });

        // UI init
        ImageButton btnSearch = findViewById(R.id.btn_search);
        TextView tvTitle = findViewById(R.id.tv_home_title);

        rvFeatured = findViewById(R.id.rv_featured);
        rvCategories = findViewById(R.id.rv_categories);
        rvRecently = findViewById(R.id.rv_recently_updated);

        progressFeatured = findViewById(R.id.progress_featured);
        progressCategories = findViewById(R.id.progress_categories);
        progressRecent = findViewById(R.id.progress_recent);

        emptyFeatured = findViewById(R.id.empty_featured);
        emptyCategories = findViewById(R.id.empty_categories);
        emptyRecent = findViewById(R.id.empty_recent);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
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
            Toast.makeText(HomeActivity.this, "Category: " + c.name + " (" + c.id + ")", Toast.LENGTH_SHORT).show();
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
        showProgress(progressFeatured, true);
        showEmpty(emptyFeatured, false);
        String url = BASE_URL + "?r=featured&limit=10";
        StringRequest sr = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject root = new JSONObject(response);
                JSONArray data = root.optJSONArray("data");
                featuredList.clear();
                if (data != null && data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject o = data.getJSONObject(i);
                        Manga m = new Manga();
                        m.manga_id = o.optString("manga_id");
                        m.title = o.optString("title");
                        m.summary = o.optString("summary");
                        m.cover = ensureFullUrl(o.optString("cover"));
                        m.created_at = o.optString("created_at");
                        featuredList.add(m);
                    }
                    showEmpty(emptyFeatured, false);
                } else {
                    showEmpty(emptyFeatured, true);
                }
                featuredAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e(TAG, "featured parse error", e);
                Toast.makeText(this, "Parse error loading featured", Toast.LENGTH_SHORT).show();
            } finally {
                showProgress(progressFeatured, false);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        }, error -> {
            Log.e(TAG, "featured error", error);
            Toast.makeText(this, "Network error loading featured: " + error.getMessage(), Toast.LENGTH_LONG).show();
            showProgress(progressFeatured, false);
            showEmpty(emptyFeatured, true);
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
        });
        NetworkSingleton.getInstance(this).getRequestQueue().add(sr);
    }

    private void loadCategories() {
        showProgress(progressCategories, true);
        showEmpty(emptyCategories, false);

        String url = BASE_URL + "?r=genres";
        StringRequest sr = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject root = new JSONObject(response);
                JSONArray arr = root.optJSONArray("data");
                categories.clear();
                if (arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject g = arr.getJSONObject(i);
                        String genreId = g.optString("genre_id", "");
                        String name = g.optString("name", "");
                        Log.d(TAG, "genre parsed: id=" + genreId + " name=" + name);
                        categories.add(new CategoryItem(genreId, name));
                    }
                    showEmpty(emptyCategories, false);
                } else {
                    Log.w(TAG, "genres array empty or missing");
                    showEmpty(emptyCategories, true);
                }
                runOnUiThread(() -> categoryAdapter.notifyDataSetChanged());
            } catch (JSONException e) {
                Log.e(TAG, "genres parse error", e);
                Toast.makeText(this, "Parse error loading genres", Toast.LENGTH_SHORT).show();
            } finally {
                showProgress(progressCategories, false);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        }, error -> {
            Log.e(TAG, "genres error", error);
            Toast.makeText(this, "Network error loading genres: " + error.getMessage(), Toast.LENGTH_LONG).show();
            showProgress(progressCategories, false);
            showEmpty(emptyCategories, true);
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
        });
        NetworkSingleton.getInstance(this).getRequestQueue().add(sr);
    }

    private void loadRecentChapters() {
        showProgress(progressRecent, true);
        showEmpty(emptyRecent, false);

        // fetch many recent chapters and group by manga_id -> keep top 3 per manga
        String url = BASE_URL + "?r=recent_chapters&limit=50";

        StringRequest sr = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject root = new JSONObject(response);
                JSONArray arr = root.optJSONArray("data");

                // maintain insertion order: newest chapters first in API response
                Map<String, RecentManga> map = new LinkedHashMap<>();

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

                // Build list: one RecentManga per manga, each with up to 3 newest chapters
                recentMangaList.clear();
                for (RecentManga rm : map.values()) {
                    if (rm.chapters.size() > 3) {
                        rm.chapters = new ArrayList<>(rm.chapters.subList(0, 3));
                    }
                    recentMangaList.add(rm);
                }

                runOnUiThread(() -> {
                    recentMangaAdapter.notifyDataSetChanged();
                    showEmpty(emptyRecent, recentMangaList.isEmpty());
                });

            } catch (JSONException e) {
                Log.e(TAG, "parse recent grouped", e);
                runOnUiThread(() -> Toast.makeText(this, "Parse error loading recent", Toast.LENGTH_SHORT).show());
            } finally {
                showProgress(progressRecent, false);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        }, error -> {
            Log.e(TAG, "recent grouped error", error);
            showProgress(progressRecent, false);
            showEmpty(emptyRecent, true);
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
        });

        NetworkSingleton.getInstance(this).getRequestQueue().add(sr);
    }
}
