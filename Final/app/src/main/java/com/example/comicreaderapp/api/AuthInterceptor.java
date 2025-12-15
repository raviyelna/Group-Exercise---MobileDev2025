package com.example.comicreaderapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;
    private static final String PREF = "user_session";
    private static final String KEY_TOKEN = "session_token";

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Get token from SharedPreferences (same PREF name as SessionManager)
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);

        // If token exists, add Authorization header
        if (token != null && !token.isEmpty()) {
            Request authorizedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authorizedRequest);
        }

        return chain.proceed(originalRequest);
    }
}