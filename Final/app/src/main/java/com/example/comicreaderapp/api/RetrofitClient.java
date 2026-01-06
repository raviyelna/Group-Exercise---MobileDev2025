package com.example.comicreaderapp.api;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.ConnectionPool;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "https://comicreaderapp-mobile.azurewebsites.net/";

    private static Retrofit loggingRetrofit;
    private static Retrofit plainRetrofit;
    private static OkHttpClient sharedClient;

    private static OkHttpClient buildDefaultClient() {
        if (sharedClient != null) return sharedClient;

        HttpLoggingInterceptor logging =
                new HttpLoggingInterceptor(msg -> android.util.Log.d("OKHTTP_HTTP", msg));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        // Build client with explicit timeouts so requests won't hang forever
        sharedClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .build();

        return sharedClient;
    }

    public static ApiService getApiService() {
        if (loggingRetrofit == null) {
            OkHttpClient client = buildDefaultClient();

            loggingRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return loggingRetrofit.create(ApiService.class);
    }

    public static Retrofit getLoggingRetrofit() {
        if (loggingRetrofit == null) {
            getApiService();
        }
        return loggingRetrofit;
    }

    public static Retrofit getInstance() {
        if (plainRetrofit == null) {
            plainRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(buildDefaultClient()) // use same client so logging & timeouts apply everywhere
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return plainRetrofit;
    }
}