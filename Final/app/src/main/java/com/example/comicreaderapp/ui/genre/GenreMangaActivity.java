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

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.LegacyApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.manga_model.FeaturedAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreMangaActivity extends AppCompatActivity {

    private static final String TAG = "GenreMangaActivity";

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
            Intent i = new Intent(this, com.example.comicreaderapp.readUI.MangaDetailActivity.class);
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

        LegacyApi api = RetrofitClient.getInstance().create(LegacyApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("genre_id", genreId);

        Call<ResponseBody> call = api.getData("manga_by_genre", params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    progress.setVisibility(View.GONE);

                    if (!response.isSuccessful() || response.body() == null) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    String raw = response.body().string();
                    JSONObject root = new JSONObject(raw);
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
                    Toast.makeText(GenreMangaActivity.this, "Parse error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(GenreMangaActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String ensureFullUrl(String cover) {
        if (cover == null || cover.isEmpty()) return "";
        if (cover.startsWith("http")) return cover;
        return "http://10.0.2.2" + (cover.startsWith("/") ? "" : "/") + cover;
    }
}