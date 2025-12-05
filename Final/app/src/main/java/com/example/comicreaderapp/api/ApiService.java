package com.example.comicreaderapp.api;

import com.example.comicreaderapp.model.ForgotPasswordRequest;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.ResetPasswordRequest;
import com.example.comicreaderapp.model.VerifyOtpRequest;
import com.example.comicreaderapp.model.VerifyOtpResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("api.php")
    Call<GenericResponse> forgotPassword(@Query("action") String action, @Body ForgotPasswordRequest body);

    @POST("api.php")
    Call<VerifyOtpResponse> verifyOtp(@Query("action") String action, @Body VerifyOtpRequest body);

    @POST("api.php")
    Call<GenericResponse> resetPassword(@Query("action") String action, @Body ResetPasswordRequest body);

}
