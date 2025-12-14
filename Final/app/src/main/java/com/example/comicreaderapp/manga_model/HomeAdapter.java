package com.example.comicreaderapp.manga_model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.readUI.MangaDetailActivity;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FEATURED = 0;
    private static final int TYPE_CATEGORIES = 1;
    private static final int TYPE_RECENT = 2;

    private final Context ctx;
    private final List<Manga> featuredList;
    private final List<CategoryItem> categories;
    private final List<RecentManga> recentMangaList;

    public HomeAdapter(
            Context ctx,
            List<Manga> featured,
            List<CategoryItem> categories,
            List<RecentManga> recent
    ) {
        this.ctx = ctx;
        this.featuredList = featured;
        this.categories = categories;
        this.recentMangaList = recent;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_FEATURED;
        if (position == 1) return TYPE_CATEGORIES;
        return TYPE_RECENT;
    }

    @Override
    public int getItemCount() {
        return 2 + recentMangaList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inf = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_FEATURED) {
            return new VHFeatured(
                    inf.inflate(R.layout.item_story, parent, false)
            );
        }

        if (viewType == TYPE_CATEGORIES) {
            return new VHCategory(
                    inf.inflate(R.layout.item_categories_section, parent, false)
            );
        }

        return new VHRecent(
                inf.inflate(R.layout.item_recently_updated, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHFeatured) {

            VHFeatured v = (VHFeatured) holder;

            FeaturedAdapter adapter =
                    new FeaturedAdapter(ctx, featuredList, manga -> {
                        // Open manga detail - navigate to MangaDetailActivity
                        openMangaDetail(manga.manga_id);
                    });

            v.rv.setLayoutManager(
                    new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
            );
            v.rv.setAdapter(adapter);
        }

        else if (holder instanceof VHCategory) {

            VHCategory v = (VHCategory) holder;

            CategoryAdapter adapter =
                    new CategoryAdapter(categories, category -> {
                        // TODO: open category listing
                        // You can create a CategoryActivity later
                    });

            v.rv.setLayoutManager(
                    new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
            );
            v.rv.setAdapter(adapter);
        }

        else if (holder instanceof VHRecent) {

            VHRecent v = (VHRecent) holder;

            RecentMangaAdapter adapter =
                    new RecentMangaAdapter(
                            ctx,
                            recentMangaList,
                            new RecentMangaAdapter.OnClick() {
                                @Override
                                public void onOpenChapter(RecentManga manga, RecentChapter chapter) {
                                    // Open reader directly with chapter
                                    openReader(chapter.chapter_id, chapter.chapter_name, manga.manga_id);
                                }

                                @Override
                                public void onOpenManga(RecentManga manga) {
                                    // Open manga detail
                                    openMangaDetail(manga.manga_id);
                                }
                            }
                    );

            v.rv.setLayoutManager(new LinearLayoutManager(ctx));
            v.rv.setAdapter(adapter);
        }
    }

    /**
     * Navigate to MangaDetailActivity
     */
    private void openMangaDetail(String mangaId) {
        android.util.Log.d("HomeAdapter", "Opening manga with ID: " + mangaId);

        if (mangaId == null || mangaId.isEmpty()) {
            android.util.Log.e("HomeAdapter", "Manga ID is null or empty!");
            android.widget.Toast.makeText(ctx, "Error: Invalid manga ID", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(ctx, MangaDetailActivity.class);
            intent.putExtra("manga_id", mangaId);
            ctx.startActivity(intent);
            android.util.Log.d("HomeAdapter", "Intent started successfully");
        } catch (Exception e) {
            android.util.Log.e("HomeAdapter", "Error starting activity: " + e.getMessage());
            android.widget.Toast.makeText(ctx, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigate directly to ComicReaderActivity
     */
    private void openReader(String chapterId, String chapterName, String mangaId) {
        Intent intent = new Intent(ctx, com.example.comicreaderapp.ui.reader.ComicReaderActivity.class);
        intent.putExtra("chapter_id", chapterId);
        intent.putExtra("chapter_name", chapterName);
        intent.putExtra("manga_id", mangaId);
        ctx.startActivity(intent);
    }

    // ================= ViewHolders =================

    static class VHFeatured extends RecyclerView.ViewHolder {
        RecyclerView rv;
        VHFeatured(View v) {
            super(v);
            rv = v.findViewById(R.id.rv_featured_section);
        }
    }

    static class VHCategory extends RecyclerView.ViewHolder {
        RecyclerView rv;
        VHCategory(View v) {
            super(v);
            rv = v.findViewById(R.id.rv_categories_section);
        }
    }

    static class VHRecent extends RecyclerView.ViewHolder {
        RecyclerView rv;
        VHRecent(View v) {
            super(v);
            rv = v.findViewById(R.id.rv_recent_manga_section);
        }
    }
}