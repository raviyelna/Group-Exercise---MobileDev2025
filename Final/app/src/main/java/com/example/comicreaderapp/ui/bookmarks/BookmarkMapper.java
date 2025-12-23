package com.example.comicreaderapp.ui.bookmarks;

import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.model.Bookmark;

import java.util.ArrayList;
import java.util.List;

public class BookmarkMapper {

    public static List<Manga> toMangaList(List<Bookmark> bookmarks) {
        List<Manga> list = new ArrayList<>();
        for (Bookmark b : bookmarks) {
            Manga m = new Manga();
            m.manga_id = b.manga_id;
            m.title = b.title;
            m.cover = b.cover_image; // ✅ now correct
            list.add(m);
        }
        return list;
    }
}
