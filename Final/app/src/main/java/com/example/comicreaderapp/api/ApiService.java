package com.example.comicreaderapp.api;

import com.example.comicreaderapp.model.ForgotPasswordRequest;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.LoginRequest;
import com.example.comicreaderapp.model.RegisterRequest;
import com.example.comicreaderapp.model.ResetPasswordRequest;
import com.example.comicreaderapp.model.VerifyOtpRequest;
import com.example.comicreaderapp.model.VerifyOtpResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("api.php")
    Call<GenericResponse> forgotPassword(@Query("action") String action, @Body ForgotPasswordRequest body);

    @POST("api.php")
    Call<VerifyOtpResponse> verifyOtp(@Query("action") String action, @Body VerifyOtpRequest body);

    @POST("api.php")
    Call<GenericResponse> resetPassword(@Query("action") String action, @Body ResetPasswordRequest body);

    @POST("api.php")
    Call<GenericResponse> login(@Query("action") String action, @Body LoginRequest body);

    @POST("api.php")
    Call<GenericResponse> register(@Query("action") String action, @Body RegisterRequest body);

    @Multipart
    @POST("api.php")
    Call<GenericResponse> updateProfile(
            @Query("action") String action,
            @Part("username") RequestBody username,
            @Part("avatar_link") RequestBody avatarLink,
            @Part MultipartBody.Part avatar
    );





}
