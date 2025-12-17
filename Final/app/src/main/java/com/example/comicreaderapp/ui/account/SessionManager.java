package com.example.comicreaderapp.ui.account;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.comicreaderapp.model.User;

public class SessionManager {

    private static final String PREF = "user_session";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_SESSION_TOKEN = "session_token";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        if (user == null) return;

        pref.edit()
                .putString(KEY_USER_ID, user.userId)
                .putString(KEY_NAME, user.username)
                .putString(KEY_EMAIL, user.email)
                .putString(KEY_AVATAR, user.ImageURL)
                .putString(KEY_SESSION_TOKEN, user.sessionToken)
                .apply();
    }

    public boolean isLoggedIn() {
        return pref.contains(KEY_USER_ID) && pref.contains(KEY_SESSION_TOKEN);
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getUsername() {
        return pref.getString(KEY_NAME, "Anonymous User");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "Anonymous@example.com");
    }

    public String getAvatar() {
        return pref.getString(KEY_AVATAR, null);
    }
    public void saveUserName(String username) {
        pref.edit().putString(KEY_NAME, username).apply();
    }

    public void saveAvatar(String avatarUrl) {
        pref.edit().putString(KEY_AVATAR, avatarUrl).apply();
    }
    public String getSessionToken() {
        return pref.getString(KEY_SESSION_TOKEN, null);
    }

    public void clear() {
        pref.edit().clear().apply();
    }
}
