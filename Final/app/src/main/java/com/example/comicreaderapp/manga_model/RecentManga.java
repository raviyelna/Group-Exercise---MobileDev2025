package com.example.comicreaderapp.manga_model;

import java.util.ArrayList;
import java.util.List;

public class RecentManga {
    public String manga_id;
    public String manga_title;
    public String manga_cover;
    // chapters newest-first
    public List<RecentChapter> chapters = new ArrayList<>();
}
