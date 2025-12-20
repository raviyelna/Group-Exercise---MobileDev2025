package com.example.comicreaderapp.model;

import com.google.gson.annotations.SerializedName;

public class MiniProfileUser {

    @SerializedName("user_id")
    public String userId;

    @SerializedName("username")
    public String username;

    @SerializedName("email")
    public String email;

    @SerializedName("ImageURL")
    public String avatar;
}
