package com.example.comicreaderapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.VerifyOtpResponse;
import com.example.comicreaderapp.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<String> error = new MutableLiveData<>();

    public MutableLiveData<GenericResponse> loginResult = new MutableLiveData<>();
    public MutableLiveData<GenericResponse> forgotResult = new MutableLiveData<>();
    public MutableLiveData<VerifyOtpResponse> verifyOtpResult = new MutableLiveData<>();
    public MutableLiveData<GenericResponse> resetPasswordResult = new MutableLiveData<>();
    public MutableLiveData<GenericResponse> registerResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = AuthRepository.getInstance(application.getApplicationContext());
        loading.setValue(false);
    }

    // ============== LOGIN ============== //
    public void login(String account, String password) {
        loading.setValue(true);
        authRepository.login(account, password, new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call,
                                   Response<GenericResponse> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.postValue(response.body());
                } else {
                    error.postValue("Lỗi đăng nhập");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }

    // ============== FORGOT (GỬI OTP) ============== //
    public void forgot(String email) {
        loading.setValue(true);
        authRepository.forgot(email, new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call,
                                   Response<GenericResponse> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    forgotResult.postValue(response.body());
                } else {
                    error.postValue("Lỗi gửi OTP");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }

    // ============== VERIFY OTP ============== //
    public void verifyOtp(String email, String otp) {
        loading.setValue(true);
        authRepository.verifyOtp(email, otp, new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyOtpResponse> call,
                                   Response<VerifyOtpResponse> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    verifyOtpResult.postValue(response.body());
                } else {
                    error.postValue("OTP không hợp lệ");
                }
            }

            @Override
            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                loading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }

    // ============== RESET PASSWORD ============== //
    public void resetPassword(String token, String newPassword) {
        loading.setValue(true);
        authRepository.resetPassword(token, newPassword, new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call,
                                   Response<GenericResponse> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    resetPasswordResult.postValue(response.body());
                } else {
                    error.postValue("Lỗi đổi mật khẩu");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }

    // ============== REGISTER ============== //
    public void register(String username, String email, String password) {
        loading.setValue(true);
        authRepository.register(username, email, password, new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call,
                                   Response<GenericResponse> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    registerResult.postValue(response.body());
                } else {
                    error.postValue("Lỗi đăng ký");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loading.postValue(false);
                error.postValue(t.getMessage());
            }
        });
    }
}
