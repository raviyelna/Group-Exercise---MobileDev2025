package com.example.comicreaderapp.manga_model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.readUI.MangaDetailActivity;
import com.example.comicreaderapp.ui.AllComicsActivity;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FEATURED = 0;
    private static final int TYPE_CATEGORIES = 1;
    private static final int TYPE_RECENT_HEADER = 2;
    private static final int TYPE_MANGA = 3;

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
    public int getItemCount() {
        return 3 + (recentMangaList == null ? 0 : recentMangaList.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_FEATURED;
        if (position == 1) return TYPE_CATEGORIES;
        if (position == 2) return TYPE_RECENT_HEADER;
        return TYPE_MANGA;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inf = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_FEATURED) {
            return new VHFeatured(inf.inflate(R.layout.item_story, parent, false));
        }

        if (viewType == TYPE_CATEGORIES) {
            return new VHCategory(inf.inflate(R.layout.item_categories_section, parent, false));
        }

        if (viewType == TYPE_RECENT_HEADER) {
            return new VHRecentHeader(inf.inflate(R.layout.item_recent_header, parent, false));
        }

        return new VHRecent(inf.inflate(R.layout.item_recently_updated, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHFeatured) {

            VHFeatured v = (VHFeatured) holder;

            FeaturedAdapter adapter =
                    new FeaturedAdapter(ctx, featuredList,
                            manga -> openMangaDetail(manga.manga_id));

            v.rv.setLayoutManager(
                    new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
            v.rv.setAdapter(adapter);
        }

        else if (holder instanceof VHCategory) {

            VHCategory v = (VHCategory) holder;

            CategoryAdapter adapter =
                    new CategoryAdapter(categories, category -> {});

            v.rv.setLayoutManager(
                    new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
            v.rv.setAdapter(adapter);
        }

        else if (holder instanceof VHRecentHeader) {

            holder.itemView.setOnClickListener(v ->
                    ctx.startActivity(new Intent(ctx, AllComicsActivity.class)));
        }

        else if (holder instanceof VHRecent) {

            int mangaPos = position - 3;
            RecentManga manga = recentMangaList.get(mangaPos);

            VHRecent v = (VHRecent) holder;

            v.title.setText(manga.manga_title);

            Glide.with(ctx)
                    .load(manga.manga_cover)
                    .placeholder(R.drawable.placeholder_cover)
                    .error(R.drawable.placeholder_cover)
                    .centerCrop()
                    .into(v.cover);

            v.itemView.setOnClickListener(view ->
                    openMangaDetail(manga.manga_id));

            v.btnRead.setOnClickListener(view ->
                    openMangaDetail(manga.manga_id));
        }
    }

    private void openMangaDetail(String mangaId) {
        Intent i = new Intent(ctx, MangaDetailActivity.class);
        i.putExtra("manga_id", mangaId);
        ctx.startActivity(i);
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

    static class VHRecentHeader extends RecyclerView.ViewHolder {
        VHRecentHeader(View v) {
            super(v);
        }
    }

    static class VHRecent extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        Button btnRead;

        VHRecent(View v) {
            super(v);
            cover = v.findViewById(R.id.img_recent_cover);
            title = v.findViewById(R.id.tv_recent_title);
            btnRead = v.findViewById(R.id.btn_read);
        }
    }
}
