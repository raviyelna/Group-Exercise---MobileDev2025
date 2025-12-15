package com.example.comicreaderapp.ui.account;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.comicreaderapp.model.User;

public class SessionManager {

    private static final String PREF = "user_session";
    private static final String KEY_NAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR = "avatar";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        if (user == null) return;
        pref.edit()
                .putString(KEY_NAME, user.username)
                .putString(KEY_EMAIL, user.email)
                .putString(KEY_AVATAR, user.ImageURL)
                .apply();
    }

    public boolean isLoggedIn() {
        return pref.contains(KEY_EMAIL) && pref.contains(KEY_NAME);
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


    public void clear() {
        pref.edit().clear().apply();
    }
}

