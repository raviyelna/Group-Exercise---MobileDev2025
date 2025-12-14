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

public class AllComicsAdapter extends RecyclerView.Adapter<AllComicsAdapter.VH> {

    public interface OnClick {
        void onOpen(AllComic comic);
    }

    private final Context ctx;
    private final List<AllComic> list;
    private final OnClick listener;

    public AllComicsAdapter(Context ctx, List<AllComic> list, OnClick listener) {
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
    public void onBindViewHolder(VH h, int pos) {
        AllComic c = list.get(pos);

        // title
        h.title.setText(c.title != null ? c.title : "");

        // cover
        Glide.with(ctx)
                .load(c.cover)
                .placeholder(R.drawable.placeholder_cover)
                .error(R.drawable.placeholder_cover)
                .centerCrop()
                .into(h.cover);

        // ❌ hide chapter text
        h.chapter.setVisibility(View.GONE);

        // ❌ hide read button
        h.btnRead.setVisibility(View.GONE);

        // open manga detail
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(c);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView title;
        TextView chapter;
        Button btnRead;

        VH(View v) {
            super(v);
            cover = v.findViewById(R.id.img_recent_cover);
            title = v.findViewById(R.id.tv_recent_title);
            chapter = v.findViewById(R.id.tv_recent_chapter);
            btnRead = v.findViewById(R.id.btn_read);
        }
    }
}
