package com.example.comicreaderapp.ui.bookmarks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.model.Bookmark;
import com.example.comicreaderapp.readUI.MangaDetailActivity;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.VH> {

    private final Context context;
    private final List<Bookmark> list;

    public BookmarkAdapter(Context context, List<Bookmark> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_recently_updated, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Bookmark b = list.get(position);

        h.title.setText(b.title != null ? b.title : "");

        Glide.with(context)
                .load(b.cover_image)
                .placeholder(R.drawable.placeholder_cover)
                .error(R.drawable.placeholder_cover)
                .centerCrop()
                .into(h.cover);

        // hide unused views
        h.chapter.setVisibility(View.GONE);
        h.btnRead.setVisibility(View.GONE);

        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, MangaDetailActivity.class);
            i.putExtra("manga_id", b.manga_id); // STRING ✔
            context.startActivity(i);
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
        TextView btnRead; // or Button if your layout uses Button

        VH(@NonNull View v) {
            super(v);
            cover = v.findViewById(R.id.img_recent_cover);
            title = v.findViewById(R.id.tv_recent_title);
            chapter = v.findViewById(R.id.tv_recent_chapter);
            btnRead = v.findViewById(R.id.btn_read);
        }
    }
}
