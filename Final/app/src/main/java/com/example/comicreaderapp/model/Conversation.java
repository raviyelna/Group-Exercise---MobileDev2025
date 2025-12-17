package com.example.comicreaderapp.model;

import com.google.gson.annotations.SerializedName;

public class Conversation {

    @SerializedName("conversation_id")
    public int conversationId;

    @SerializedName("type")
    public String type;

    @SerializedName("title")
    public String title;

    @SerializedName("avatar")
    public String avatar;
}
