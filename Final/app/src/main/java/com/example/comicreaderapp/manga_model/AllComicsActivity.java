package com.example.comicreaderapp.ui;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllComicsActivity extends AppCompatActivity {

    private static final String TAG = "AllComicsActivity";

    private ExecutorService parseExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comics);

        parseExecutor = Executors.newSingleThreadExecutor();

        RecyclerView rv = findViewById(R.id.rv_recent_full);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<AllComic> list = new ArrayList<>();

        AllComicsAdapter adapter = new AllComicsAdapter(
                this,
                list,
                comic -> {
                    Intent i = new Intent(this, com.example.comicreaderapp.readUI.MangaDetailActivity.class);
                    i.putExtra("manga_id", comic.manga_id);
                    startActivity(i);
                }
        );

        rv.setAdapter(adapter);

        loadAllManga(list, adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (parseExecutor != null && !parseExecutor.isShutdown()) {
            parseExecutor.shutdownNow();
        }
    }

    private void loadAllManga(List<AllComic> list, AllComicsAdapter adapter) {

        // Use logging retrofit so HTTP requests/responses are visible in Logcat
        LegacyApi api = RetrofitClient.getLoggingRetrofit().create(LegacyApi.class);
        Call<ResponseBody> call = api.getData("all_manga");

        Log.d(TAG, "enqueue all_manga request");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: code=" + response.code() + " successful=" + response.isSuccessful());

                if (!response.isSuccessful()) {
                    String errBody = null;
                    try {
                        ResponseBody eb = response.errorBody();
                        if (eb != null) errBody = eb.string();
                    } catch (Exception e) {
                        Log.e(TAG, "errorBody read failed", e);
                    }
                    Log.e(TAG, "API returned non-200. code=" + response.code() + " message=" + response.message() + " errorBody=" + errBody);
                    Toast.makeText(AllComicsActivity.this,
                            "API error: code=" + response.code(),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.body() == null) {
                    Log.e(TAG, "Response body is null");
                    Toast.makeText(AllComicsActivity.this,
                            "API error: empty response",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Parse in background to avoid blocking UI (important if response large)
                parseExecutor.submit(() -> {
                    String raw = null;
                    try {
                        raw = response.body().string();
                        Log.d(TAG, "raw response length=" + (raw != null ? raw.length() : "null"));
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to read response body", e);
                        runOnUiThread(() -> Toast.makeText(AllComicsActivity.this, "Read response failed", Toast.LENGTH_LONG).show());
                        return;
                    } catch (IllegalStateException ise) {
                        Log.e(TAG, "Response body already consumed", ise);
                        runOnUiThread(() -> Toast.makeText(AllComicsActivity.this, "Response already consumed", Toast.LENGTH_LONG).show());
                        return;
                    }

                    try {
                        JSONObject root = new JSONObject(raw);
                        JSONArray arr = root.optJSONArray("data");

                        List<AllComic> parsed = new ArrayList<>();
                        if (arr != null) {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject o = arr.getJSONObject(i);

                                AllComic c = new AllComic();
                                c.manga_id = o.optString("manga_id");
                                c.title = o.optString("title");
                                c.cover = o.optString("cover");

                                parsed.add(c);
                            }
                        }

                        // Post result to UI thread
                        runOnUiThread(() -> {
                            list.clear();
                            list.addAll(parsed);
                            adapter.notifyDataSetChanged();
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Parse error", e);
                        runOnUiThread(() -> Toast.makeText(AllComicsActivity.this,
                                "Parse error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Network failure in all_manga request", t);
                Toast.makeText(AllComicsActivity.this,
                        "API error: " + (t.getMessage() != null ? t.getMessage() : "unknown"),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}