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

        // Title
        h.title.setText(rm.manga_title != null ? rm.manga_title : "");

        // Compose up to 3 chapter lines (each on its own line)
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(3, rm.chapters.size());
        for (int i = 0; i < limit; i++) {
            RecentChapter c = rm.chapters.get(i);
            // show chapter name plus optional time (created_at)
            String line = (c.chapter_name != null ? c.chapter_name : "");
            if (c.created_at != null && !c.created_at.isEmpty()) {
                // short format: keep original or you can format time-ago
                line += " • " + c.created_at;
            }
            sb.append(line);
            if (i < limit - 1) sb.append("\n");
        }
        h.chapter.setMaxLines(3);
        h.chapter.setText(sb.toString());

        // Cover
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

        // Item click opens manga details
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpenManga(rm);
        });

        // Read button: open the newest chapter (first in list) if present
        h.btnRead.setOnClickListener(v -> {
            if (listener != null) {
                if (!rm.chapters.isEmpty()) listener.onOpenChapter(rm, rm.chapters.get(0));
                else listener.onOpenManga(rm);
            }
        });
    }

    @Override public int getItemCount() { return list == null ? 0 : list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, chapter;
        Button btnRead;

        VH(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.img_recent_cover);
            title = itemView.findViewById(R.id.tv_recent_title);
            chapter = itemView.findViewById(R.id.tv_recent_chapter);
            btnRead = itemView.findViewById(R.id.btn_read);
        }
    }
}
