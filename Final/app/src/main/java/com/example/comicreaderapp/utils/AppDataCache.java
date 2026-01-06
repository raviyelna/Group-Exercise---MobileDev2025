package com.example.comicreaderapp.utils;

import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.manga_model.RecentManga;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AppDataCache {

    // Nguồn dữ liệu search offline
    public static final ArrayList<Manga> SEARCH_SOURCE = new ArrayList<>();

    public static void clear() {
        SEARCH_SOURCE.clear();
    }

    // Add từ featured
    public static void addFeatured(ArrayList<Manga> list) {
        if (list == null) return;
        SEARCH_SOURCE.addAll(list);
    }

    // Add từ recent (convert RecentManga -> Manga)
    public static void addRecent(ArrayList<RecentManga> list) {
        if (list == null) return;

        Set<String> existed = new HashSet<>();
        for (Manga m : SEARCH_SOURCE) {
            existed.add(m.manga_id);
        }

        for (RecentManga rm : list) {
            if (!existed.contains(rm.manga_id)) {
                Manga m = new Manga();
                m.manga_id = rm.manga_id;
                m.title = rm.manga_title;
                m.cover = rm.manga_cover;
                SEARCH_SOURCE.add(m);
            }
        }
    }
}
