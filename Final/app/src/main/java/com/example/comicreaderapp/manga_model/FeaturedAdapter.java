package com.example.comicreaderapp.manga_model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;

import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.VH> {
    private static final String TAG = "FeaturedAdapter";
    private final List<Manga> data;
    private final Context ctx;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Manga m);
    }

    public FeaturedAdapter(Context ctx, List<Manga> data, OnItemClickListener l) {
        this.ctx = ctx;
        this.data = data;
        this.listener = l;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Manga m = data.get(position);

        holder.tv.setText(m.title);

        Glide.with(ctx)
                .load(m.cover)
                .centerCrop()
                .placeholder(R.drawable.placeholder_cover)
                .error(R.drawable.placeholder_cover)
                .into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(m);
        });
    }


    @Override
    public int getItemCount() { return data == null ? 0 : data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tv;
        VH(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.story_cover);
            tv = itemView.findViewById(R.id.story_title);
        }
    }
}
