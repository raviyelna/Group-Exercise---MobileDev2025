package com.example.comicreaderapp.utils;

import android.util.Log;

import com.example.comicreaderapp.api.LegacyApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.manga_model.RecentManga;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class HomeDataPreloader {

    private static final String TAG = "HomeDataPreloader";
    private static boolean started = false;

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    /** Gọi 1 lần duy nhất */
    public static void start() {
        if (started) return;
        started = true;

        executor.submit(() -> {
            try {
                LegacyApi api =
                        RetrofitClient.getInstance().create(LegacyApi.class);

                preloadFeatured(api);
                preloadRecent(api);

                Log.d(TAG, "Preload done. Cache size=" +
                        AppDataCache.SEARCH_SOURCE.size());

            } catch (Exception e) {
                Log.e(TAG, "Preload error", e);
            }
        });
    }

    private static void preloadFeatured(LegacyApi api) throws Exception {
        Call<ResponseBody> call =
                api.getData("featured",
                        java.util.Collections.singletonMap("limit", "50"));

        Response<ResponseBody> res = call.execute();
        if (!res.isSuccessful() || res.body() == null) return;

        JSONObject root = new JSONObject(res.body().string());
        JSONArray arr = root.optJSONArray("data");

        ArrayList<Manga> list = new ArrayList<>();
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Manga m = new Manga();
                m.manga_id = o.optString("manga_id");
                m.title = o.optString("title");
                m.cover = o.optString("cover");
                list.add(m);
            }
        }

        AppDataCache.clear();
        AppDataCache.addFeatured(list);
    }

    private static void preloadRecent(LegacyApi api) throws Exception {
        Call<ResponseBody> call =
                api.getData("recent_chapters",
                        java.util.Collections.singletonMap("limit", "50"));

        Response<ResponseBody> res = call.execute();
        if (!res.isSuccessful() || res.body() == null) return;

        JSONObject root = new JSONObject(res.body().string());
        JSONArray arr = root.optJSONArray("data");

        Map<String, RecentManga> map = new LinkedHashMap<>();
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String mid = o.optString("manga_id");

                if (!map.containsKey(mid)) {
                    RecentManga rm = new RecentManga();
                    rm.manga_id = mid;
                    rm.manga_title = o.optString("manga_title");
                    rm.manga_cover = o.optString("manga_cover");
                    map.put(mid, rm);
                }
            }
        }

        AppDataCache.addRecent(new ArrayList<>(map.values()));
    }
}
