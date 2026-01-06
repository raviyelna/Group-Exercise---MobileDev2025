package com.example.comicreaderapp;

import android.app.Application;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.BuildConfig;

/**
 * Application class — đặt tên .ComicReaderApp trong AndroidManifest (android:name).
 * Đây là nơi ta ép toàn bộ app chạy Dark Mode.
 */
public class ComicReaderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Ép toàn bộ app ở dark theme bất kể cài đặt hệ thống
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Debug helper: bật StrictMode để phát hiện I/O / network / disk trên main thread
        // Chỉ bật trong debug builds (BuildConfig.DEBUG)
        try {
            if (BuildConfig.DEBUG) {
                StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        // .penaltyDialog() // optional: show dialog on violation (may be intrusive)
                        .build();
                StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build();
                StrictMode.setThreadPolicy(threadPolicy);
                StrictMode.setVmPolicy(vmPolicy);
            }
        } catch (Throwable t) {
            // don't crash if StrictMode APIs not available for some reason
            t.printStackTrace();
        }
        // Nếu bạn còn khởi tạo global singletons / logging / DI, làm ở đây
    }
}