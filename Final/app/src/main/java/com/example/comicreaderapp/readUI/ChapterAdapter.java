package com.example.comicreaderapp.readUI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private Context context;
    private List<MangaChapter> chapterList;

    public ChapterAdapter(Context context, List<MangaChapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        MangaChapter chapter = chapterList.get(position);

        holder.tvChapterName.setText(chapter.chapter_name);

        // Set click listener to open reader
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReaderActivity.class);
            intent.putExtra("manga_id", chapter.manga_id);
            intent.putExtra("chapter_name", chapter.chapter_name);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterName;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterName = itemView.findViewById(R.id.tv_chapter_name);
        }
    }
}