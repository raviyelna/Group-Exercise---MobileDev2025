package com.example.comicreaderapp.model;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("message_id")
    public int messageId;

    @SerializedName("content")
    public String content;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("user_id")
    public String senderId;

    @SerializedName("username")
    public String username;

    @SerializedName("avatar")
    public String avatar;
}
