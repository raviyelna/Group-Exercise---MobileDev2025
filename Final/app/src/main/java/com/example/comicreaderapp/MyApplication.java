package com.example.comicreaderapp;

import android.app.Application;

import com.example.comicreaderapp.utils.HomeDataPreloader;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 🔥 Chạy cache song song khi app start
        HomeDataPreloader.start();
    }
}
