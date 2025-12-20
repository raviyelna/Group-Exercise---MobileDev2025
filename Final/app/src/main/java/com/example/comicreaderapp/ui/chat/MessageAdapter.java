package com.example.comicreaderapp.ui.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.model.Message;

import java.util.List;

import com.example.comicreaderapp.ui.chat.MiniProfileBottomSheet;

public class MessageAdapter
        extends RecyclerView.Adapter<MessageAdapter.VH> {

    private final List<Message> list;
    private final AppCompatActivity activity;

    public MessageAdapter(AppCompatActivity activity, List<Message> list) {
        this.activity = activity;
        this.list = list;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, msg, time;

        VH(View v) {
            super(v);
            avatar = v.findViewById(R.id.img_sender_avatar);
            name = v.findViewById(R.id.tv_sender_name);
            msg = v.findViewById(R.id.tv_message);
            time = v.findViewById(R.id.tv_time);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_group, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Message m = list.get(i);

        Log.d("MSG_DEBUG", "senderId = " + m.senderId);

        h.name.setText(m.username);
        h.msg.setText(m.content);
        h.time.setText(m.createdAt);

        Glide.with(activity)
                .load(
                        m.avatar == null || m.avatar.isEmpty()
                                ? R.drawable.ic_account
                                : m.avatar
                )
                .into(h.avatar);

        h.avatar.setOnClickListener(v -> {
            Log.d("MSG_DEBUG", "Clicked avatar, senderId = " + m.senderId);

            MiniProfileBottomSheet sheet =
                    MiniProfileBottomSheet.newInstance(
                            m.senderId,
                            m.username,
                            m.avatar
                    );

            sheet.show(
                    activity.getSupportFragmentManager(),
                    "MiniProfile"
            );
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
