package com.example.comicreaderapp.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ChatApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.MiniProfileUser;
import com.example.comicreaderapp.model.User;
import com.example.comicreaderapp.ui.chat.ChatActivity;
import com.example.comicreaderapp.ui.account.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiniProfileBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_AVATAR = "avatar";

    public static MiniProfileBottomSheet newInstance(
            String userId,
            String username,
            String avatar
    ) {
        MiniProfileBottomSheet sheet = new MiniProfileBottomSheet();
        Bundle b = new Bundle();
        b.putString(ARG_USER_ID, userId);
        b.putString(ARG_USERNAME, username);
        b.putString(ARG_AVATAR, avatar);
        sheet.setArguments(b);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.activity_mini_profile, container, false);

        Bundle args = getArguments();
        if (args == null) return v;

        String targetUserId = args.getString(ARG_USER_ID);
        String username = args.getString(ARG_USERNAME);
        String avatarUrl = args.getString(ARG_AVATAR);

        String myUserId = new SessionManager(requireContext()).getUserId();
        ChatApi api = RetrofitClient.getLoggingRetrofit().create(ChatApi.class);

        TextView tvName = v.findViewById(R.id.tv_user_name);
        TextView tvEmail = v.findViewById(R.id.tv_user_email);
        ImageView avatar = v.findViewById(R.id.img_avatar);
        Button btnMessage = v.findViewById(R.id.MessageButton);

        // Instant UI
        tvName.setText(username);
        tvEmail.setText("loading...");

        Glide.with(requireContext())
                .load(avatarUrl)
                .placeholder(R.drawable.ic_account)
                .into(avatar);

        // Load email
        api.getUserProfile(targetUserId).enqueue(new Callback<MiniProfileUser>() {
            @Override
            public void onResponse(Call<MiniProfileUser> call,
                                   Response<MiniProfileUser> res) {
                if (res.isSuccessful() && res.body() != null) {

                    tvEmail.setText(res.body().email);

                    if (res.body().avatar != null && !res.body().avatar.isEmpty()) {
                        Glide.with(requireContext())
                                .load(res.body().avatar)
                                .placeholder(R.drawable.ic_account)
                                .error(R.drawable.ic_account)
                                .into(avatar);
                    } else {
                        avatar.setImageResource(R.drawable.ic_account);
                    }
                }
            }

            @Override
            public void onFailure(Call<MiniProfileUser> call, Throwable t) {
                tvEmail.setText("");
                avatar.setImageResource(R.drawable.ic_account);
            }
        });



        // ✅ MESSAGE BUTTON (NOW WORKS)
        btnMessage.setOnClickListener(view -> {
            api.getOrCreateDM(myUserId, targetUserId)
                    .enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(
                                Call<Map<String, Object>> call,
                                Response<Map<String, Object>> res
                        ) {
                            if (res.isSuccessful() && res.body() != null) {

                                int conversationId =
                                        Integer.parseInt(
                                                res.body().get("conversation_id").toString()
                                        );

                                Intent intent = new Intent(requireContext(), ChatActivity.class);
                                intent.putExtra("open_conversation_id", conversationId);
                                intent.addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                                );
                                startActivity(intent);

                                dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
        });


        return v;
    }
}
