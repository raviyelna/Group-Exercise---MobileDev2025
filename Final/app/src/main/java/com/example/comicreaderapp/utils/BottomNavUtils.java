package com.example.comicreaderapp.utils;

import android. app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.IdRes;
import androidx.core.content. ContextCompat;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.ui.account.AccountActivity;
import com. example.comicreaderapp. ui.bookmarks.BookmarksActivity;
import com.example.comicreaderapp.ui.chat.ChatActivity;
import com. example.comicreaderapp. ui.home.HomeActivity;
import com. example.comicreaderapp. ui.recent.RecentActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.res.ColorStateList;

public class BottomNavUtils {
    private static final String TAG = "BottomNavUtils";

    public static void apply(BottomNavigationView nav, Context ctx) {
        if (nav == null || ctx == null) {
            Log.w(TAG, "apply:  nav or ctx is null");
            return;
        }

        try {
            // Force label visibility
            nav.setLabelVisibilityMode(
                    com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            );
            Log.d(TAG, "✅ Label visibility set");
        } catch (Throwable t) {
            Log.w(TAG, "setLabelVisibilityMode failed", t);
        }

        // Background
        try {
            nav.setBackgroundColor(ContextCompat.getColor(ctx, R.color.bottom_nav_bg));
            Log.d(TAG, "✅ Background color set");
        } catch (Throwable t) {
            Log.w(TAG, "setBackgroundColor failed", t);
        }

        // Icon size
        int iconPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24,
                ctx.getResources().getDisplayMetrics()
        );
        try {
            nav.setItemIconSize(iconPx);
            Log.d(TAG, "✅ Icon size set to " + iconPx + "px");
        } catch (Throwable t) {
            Log.w(TAG, "setItemIconSize failed", t);
        }

        // Color state list
        try {
            ColorStateList csl = ContextCompat.getColorStateList(ctx, R.color.bottom_nav_color_state);
            if (csl != null) {
                nav.setItemIconTintList(csl);
                nav.setItemTextColor(csl);
                Log.d(TAG, "✅ Color state list applied");
            } else {
                Log.w(TAG, "ColorStateList is null");
            }
        } catch (Throwable t) {
            Log.w(TAG, "Color tint failed", t);
        }

        nav.setElevation(8f);
    }

    public static void setupNavigation(BottomNavigationView nav, Activity current, @IdRes int currentMenuId) {
        if (nav == null || current == null) {
            Log.w(TAG, "setupNavigation: nav or activity is null");
            return;
        }

        // Validate menu
        if (nav.getMenu() == null || nav.getMenu().size() == 0) {
            Log.e(TAG, "❌ Menu is NULL or EMPTY!");
            return;
        }

        Log.d(TAG, "Menu has " + nav.getMenu().size() + " items");

        // Set selected item
        try {
            nav.setSelectedItemId(currentMenuId);
            Log.d(TAG, "✅ Selected item set to: " + currentMenuId);
        } catch (Throwable t) {
            Log.w(TAG, "Failed to set selected item", t);
        }

        // Clear old listener
        try {
            nav. setOnItemSelectedListener(null);
        } catch (Throwable ignore) {}

        // Set new listener
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Log.d(TAG, "🔘 Item clicked: id=" + id + " title=" + item.getTitle());

            if (id == currentMenuId) {
                Log.d(TAG, "Already on this screen, ignoring");
                return true;
            }

            Intent intent = null;

            if (id == R.id.nav_home) {
                Log.d(TAG, "→ Navigate to Home");
                intent = new Intent(current, HomeActivity.class);
            } else if (id == R.id.nav_recent) {
                Log.d(TAG, "→ Navigate to Recent");
                intent = new Intent(current, RecentActivity.class);
            } else if (id == R.id.nav_bookmark) {
                Log.d(TAG, "→ Navigate to Bookmarks");
                intent = new Intent(current, BookmarksActivity.class);
            } else if (id == R.id.nav_account) {
                Log. d(TAG, "→ Navigate to Account");
                intent = new Intent(current, AccountActivity.class);
            } else if (id == R.id.nav_chat) {
                Log.d(TAG, "→ Navigate to Chat");
                intent = new Intent(current, ChatActivity.class);
            } else {
                Log.w(TAG, "❌ Unknown menu item: " + id);
                return false;
            }

            if (intent != null) {
                try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    current.startActivity(intent);
                    current.overridePendingTransition(0, 0);
                    current.finish();
                    Log.d(TAG, "✅ Navigation successful");
                    return true;
                } catch (Throwable t) {
                    Log.e(TAG, "❌ Navigation failed", t);
                    return false;
                }
            }

            return false;
        });

        Log.d(TAG, "✅ setupNavigation complete");
    }
}