package com.example.comicreaderapp.repository;

import android.content.Context;

import com.example.comicreaderapp.api.ApiService;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.ForgotPasswordRequest;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.LoginRequest;
import com.example.comicreaderapp.model.RegisterRequest;
import com.example.comicreaderapp.model.ResetPasswordRequest;
import com.example.comicreaderapp.model.VerifyOtpRequest;
import com.example.comicreaderapp.model.VerifyOtpResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class AuthRepository {

    private static AuthRepository instance;
    private final ApiService apiService;

    // action phải trùng với PHP: api.php?action=xxx
    private static final String ACTION_FORGOT_PASSWORD = "forgot";
    private static final String ACTION_VERIFY_OTP      = "verify-otp";
    private static final String ACTION_RESET_PASSWORD  = "reset-password";
    private static final String ACTION_LOGIN           = "login";
    private static final String ACTION_REGISTER        = "register";

    private AuthRepository(Context context) {
        // context hiện chưa cần dùng, nhưng giữ lại để khớp với ViewModel
        apiService = RetrofitClient.getApiService();
    }

    public static synchronized AuthRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AuthRepository(context.getApplicationContext());
        }
        return instance;
    }

    // ================ LOGIN ================ //
    public void login(String account,
                      String password,
                      Callback<GenericResponse> callback) {

        LoginRequest body = new LoginRequest(account, password);
        Call<GenericResponse> call = apiService.login(ACTION_LOGIN, body);
        call.enqueue(callback);
    }

    // ================ REGISTER ================ //
    public void register(String username,
                         String email,
                         String password,
                         Callback<GenericResponse> callback) {

        RegisterRequest body = new RegisterRequest(username, email, password);
        Call<GenericResponse> call = apiService.register(ACTION_REGISTER, body);
        call.enqueue(callback);
    }

    // ================ FORGOT PASSWORD (GỬI OTP) ================ //
    public void forgot(String email,
                       Callback<GenericResponse> callback) {

        ForgotPasswordRequest body = new ForgotPasswordRequest(email);
        Call<GenericResponse> call = apiService.forgotPassword(ACTION_FORGOT_PASSWORD, body);
        call.enqueue(callback);
    }

    // ================ VERIFY OTP ================ //
    public void verifyOtp(String email,
                          String otp,
                          Callback<VerifyOtpResponse> callback) {

        VerifyOtpRequest body = new VerifyOtpRequest(email, otp);
        Call<VerifyOtpResponse> call = apiService.verifyOtp(ACTION_VERIFY_OTP, body);
        call.enqueue(callback);
    }

    // ================ RESET PASSWORD ================ //
    public void resetPassword(String token,
                              String newPassword,
                              Callback<GenericResponse> callback) {

        ResetPasswordRequest body = new ResetPasswordRequest(token, newPassword);
        Call<GenericResponse> call = apiService.resetPassword(ACTION_RESET_PASSWORD, body);
        call.enqueue(callback);
    }
}
