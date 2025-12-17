package com.example.comicreaderapp.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.model.Message;

import java.util.List;

public class MessageAdapter
        extends RecyclerView.Adapter<MessageAdapter.VH> {

    private final List<Message> list;
    private final String myUserId;

    public MessageAdapter(List<Message> list, String myUserId) {
        this.list = list;
        this.myUserId = myUserId;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView msg;

        VH(View v) {
            super(v);
            msg = v.findViewById(android.R.id.text1);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        h.msg.setText(list.get(i).content);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
