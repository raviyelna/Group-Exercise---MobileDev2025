package com.example.comicreaderapp.api;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    // Khi thành công: server trả {"data": {...}}
    @SerializedName("data")
    private T data;

    // Một số API có thể trả "message"
    @SerializedName("message")
    private String message;

    // Khi lỗi: server trả {"error": "..."}
    @SerializedName("error")
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    // Optional: tiện cho UI, lấy msg hiển thị
    public String getDisplayMessage() {
        if (message != null && !message.isEmpty()) return message;
        if (error != null && !error.isEmpty()) return error;
        return "";
    }
}
