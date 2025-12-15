package com.example.comicreaderapp.model;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Object data;
    // getters / setters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() {
        return data;
    }

}
