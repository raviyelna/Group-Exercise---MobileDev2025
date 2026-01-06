package com.example.comicreaderapp.readUI;

import android.content.Context;

import com.example.comicreaderapp.api.LegacyApi;
import com.example.comicreaderapp.api.RetrofitClient;

import retrofit2.Retrofit;

/**
 * NOTE: Originally this class provided a Volley RequestQueue.
 * After migrating activities to Retrofit, this helper now provides
 * a Retrofit-based access point for legacy endpoints (LegacyApi).
 *
 * You can replace usages with RetrofitClient.getInstance().create(LegacyApi.class)
 * or keep using this singleton to obtain LegacyApi.
 */
public class NetworkSingleton {
    private static NetworkSingleton instance;
    private LegacyApi legacyApi;
    private static Context ctx;

    private NetworkSingleton(Context context) {
        ctx = context.getApplicationContext();
        Retrofit retrofit = RetrofitClient.getInstance();
        legacyApi = retrofit.create(LegacyApi.class);
    }

    public static synchronized NetworkSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkSingleton(context.getApplicationContext());
        }
        return instance;
    }

    public LegacyApi getLegacyApi() {
        return legacyApi;
    }
}