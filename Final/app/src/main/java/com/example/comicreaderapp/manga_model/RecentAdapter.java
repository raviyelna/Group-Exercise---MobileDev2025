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

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.VH> {

    public interface OnClick {
        void onOpenChapter(RecentChapter rc);
        void onOpenManga(RecentChapter rc);
    }

    private final List<RecentChapter> list;
    private final Context ctx;
    private final OnClick listener;

    public RecentAdapter(Context ctx, List<RecentChapter> list, OnClick listener) {
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

        // Use the correct model
        RecentChapter rc = list.get(position);

        // Title = Manga title
        h.title.setText(rc.manga_title != null ? rc.manga_title : "");

        // Build subtitle
        StringBuilder sb = new StringBuilder();
        if (rc.chapter_name != null && !rc.chapter_name.trim().isEmpty()) {
            sb.append(rc.chapter_name.trim());
        }

        if (rc.created_at != null && !rc.created_at.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" • ");
            sb.append(rc.created_at.trim());
        }

        h.chapter.setText(sb.toString());

        // Cover image
        Glide.with(ctx)
                .load(rc.manga_cover != null && !rc.manga_cover.isEmpty() ? rc.manga_cover : R.drawable.placeholder_cover)
                .centerCrop()
                .placeholder(R.drawable.placeholder_cover)
                .error(R.drawable.placeholder_cover)
                .into(h.cover);

        // Click -> open manga detail
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpenManga(rc);
        });

        // Click -> open chapter reader
        h.btnRead.setOnClickListener(v -> {
            if (listener != null) listener.onOpenChapter(rc);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

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
