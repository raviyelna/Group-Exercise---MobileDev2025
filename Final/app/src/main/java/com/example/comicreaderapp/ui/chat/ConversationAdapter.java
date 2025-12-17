package com.example.comicreaderapp.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.model.Conversation;

import java.util.List;

public class ConversationAdapter
        extends RecyclerView.Adapter<ConversationAdapter.VH> {

    public interface OnClick {
        void onOpen(Conversation conversation);
    }

    private final List<Conversation> list;
    private final OnClick listener;

    public ConversationAdapter(List<Conversation> list, OnClick listener) {
        this.list = list;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView title;

        VH(View v) {
            super(v);
            avatar = v.findViewById(R.id.ChatAvatar);
            title = v.findViewById(R.id.textView);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chatgroup, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Conversation c = list.get(i);
        h.title.setText(c.title);

        Glide.with(h.itemView)
                .load(c.avatar)
                .placeholder(R.drawable.avatar_border)
                .into(h.avatar);

        h.itemView.setOnClickListener(v -> listener.onOpen(c));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
