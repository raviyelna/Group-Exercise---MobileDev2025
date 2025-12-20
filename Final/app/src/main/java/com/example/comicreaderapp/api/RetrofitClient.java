package com.example.comicreaderapp.api;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;

import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "http://10.0.2.2/api/";

    private static Retrofit loggingRetrofit;
    private static Retrofit plainRetrofit;

    public static ApiService getApiService() {
        if (loggingRetrofit == null) {

            HttpLoggingInterceptor logging =
                    new HttpLoggingInterceptor(msg ->
                            android.util.Log.d("OKHTTP_HTTP", msg)
                    );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

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
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return plainRetrofit;
    }
}

