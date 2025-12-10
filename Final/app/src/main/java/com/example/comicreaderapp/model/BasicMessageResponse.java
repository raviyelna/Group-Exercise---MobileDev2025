package com.example.comicreaderapp.model;

import com.google.gson.annotations.SerializedName;

public class BasicMessageResponse {

    @SerializedName("message")
    private String message;

    // Optional để nhận thêm lỗi từ backend
    @SerializedName("error")
    private String error;

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public boolean hasError() {
        return error != null && !error.isEmpty();
    }
}