package com.example.comicreaderapp.manga_model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;

import java.util.List;

public class RecentMangaAdapter extends RecyclerView.Adapter<RecentMangaAdapter.VH> {

    public interface OnClick {
        void onOpenChapter(RecentManga manga, RecentChapter chapter);
        void onOpenManga(RecentManga manga);
    }

    private final Context ctx;
    private final List<RecentManga> list;
    private final OnClick listener;

    public RecentMangaAdapter(Context ctx, List<RecentManga> list, OnClick listener) {
        this.ctx = ctx;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recently_updated, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int position) {
        RecentManga rm = list.get(position);

        // Title (ONLY title)
        h.title.setText(rm.manga_title != null ? rm.manga_title : "");

        // Cover (ONLY cover)
        String cover = rm.manga_cover != null ? rm.manga_cover : "";
        if (!cover.isEmpty()) {
            Glide.with(ctx)
                    .load(cover)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_cover)
                    .error(R.drawable.placeholder_cover)
                    .into(h.cover);
        } else {
            h.cover.setImageResource(R.drawable.placeholder_cover);
        }

        // Item click -> open manga
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpenManga(rm);
        });

        // Read button -> open manga
        h.btnRead.setOnClickListener(v -> {
            if (listener != null) listener.onOpenManga(rm);
        });
    }

    @Override public int getItemCount() { return list == null ? 0 : list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        //TextView chapter;
        Button btnRead;

        VH(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.img_recent_cover);
            title = itemView.findViewById(R.id.tv_recent_title);
            //chapter = itemView.findViewById(R.id.tv_recent_chapter);
            btnRead = itemView.findViewById(R.id.btn_read);
        }
    }
}
