package com.example.comicreaderapp.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ApiErrorUtils;
import com.example.comicreaderapp.api.ApiService;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.VerifyOtpRequest;
import com.example.comicreaderapp.model.VerifyOtpResponse;
import com.example.comicreaderapp.model.ForgotPasswordRequest;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpAuthActivity extends AppCompatActivity {

    private TextInputEditText editOtp;
    private Button buttonVerify;
    private TextView textResend, textChangeEmail;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_auth);

        editOtp = findViewById(R.id.editText_Otp);
        buttonVerify = findViewById(R.id.button_VerifyOtp);
        textResend = findViewById(R.id.textView_Resend);
        textChangeEmail = findViewById(R.id.textView_ChangeEmail);

        apiService = (ApiService) RetrofitClient.getApiService();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        email = getIntent().getStringExtra("email");

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = editOtp.getText() != null ? editOtp.getText().toString().trim() : "";
                if (TextUtils.isEmpty(otp) || otp.length() < 4) {
                    editOtp.setError("Enter OTP");
                    return;
                }
                verifyOtp(email, otp);
            }
        });

        textResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOtp();
            }
        });

        textChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // quay lại nhập email
            }
        });
    }

    private void verifyOtp(final String email, String otp) {
        progressDialog.show();
        VerifyOtpRequest req = new VerifyOtpRequest(email, otp);
        // Gọi với action=verify-otp
        apiService.verifyOtp("verify-otp", req).enqueue(new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    VerifyOtpResponse body = response.body();
                    if (body.isSuccess()) {
                        String resetToken = body.getResetToken();
                        if (resetToken != null && !resetToken.isEmpty()) {
                            Intent i = new Intent(OtpAuthActivity.this, ResetPasswordActivity.class);
                            i.putExtra("resetToken", resetToken);
                            i.putExtra("email", email);
                            startActivity(i);
                        } else {
                            Toast.makeText(OtpAuthActivity.this, "Missing reset token from server", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(OtpAuthActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    ApiErrorUtils.GenericError err = ApiErrorUtils.parseError(response);
                    String msg = err != null && err.message != null ? err.message : "Invalid OTP";
                    Toast.makeText(OtpAuthActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OtpAuthActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resendOtp() {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "No email to resend to", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        // Gọi với action=forgot để resend
        apiService.forgotPassword("forgot", new ForgotPasswordRequest(email)).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse body = response.body();
                    Toast.makeText(OtpAuthActivity.this, body.getMessage() != null ? body.getMessage() : "OTP resent", Toast.LENGTH_SHORT).show();
                } else {
                    ApiErrorUtils.GenericError err = ApiErrorUtils.parseError(response);
                    String msg = err != null && err.message != null ? err.message : "Failed to resend";
                    Toast.makeText(OtpAuthActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OtpAuthActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}