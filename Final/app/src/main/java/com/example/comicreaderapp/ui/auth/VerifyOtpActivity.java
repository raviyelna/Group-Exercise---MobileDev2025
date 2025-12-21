package com.example.comicreaderapp.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ApiErrorUtils;
import com.example.comicreaderapp.api.ApiService;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.VerifyOtpRequest;
import com.example.comicreaderapp.model.VerifyOtpResponse;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText[] otpBoxes;
    private MaterialButton btnVerify;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_auth);

        email = getIntent().getStringExtra("email");

        otpBoxes = new EditText[]{
                findViewById(R.id.otp_box1),
                findViewById(R.id.otp_box2),
                findViewById(R.id.otp_box3),
                findViewById(R.id.otp_box4),
                findViewById(R.id.otp_box5),
                findViewById(R.id.otp_box6)
        };

        btnVerify = findViewById(R.id.btn_verify_otp);

        apiService = RetrofitClient.getApiService();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xác thực OTP...");
        progressDialog.setCancelable(false);

        setupOtpInput();

        btnVerify.setOnClickListener(v -> verifyOtp());
    }

    private void setupOtpInput() {
        for (int i = 0; i < otpBoxes.length; i++) {
            final int index = i;
            otpBoxes[i].setOnClickListener(v -> {
                // Đơn giản: giả sử bạn dùng custom keyboard / dialog
            });
        }
    }

    private void verifyOtp() {
        StringBuilder sb = new StringBuilder();
        for (TextView tv : otpBoxes) {
            sb.append(tv.getText().toString().trim());
        }

        String otp = sb.toString();

        if (otp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("otp", otp);

        VerifyOtpRequest req = new VerifyOtpRequest(email, otp);

        apiService.verifyOtp("verify-otp", req).enqueue(new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    VerifyOtpResponse res = response.body();

                    if (res.isSuccess()) {
                        Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("resetToken", res.getResetToken());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(
                                VerifyOtpActivity.this,
                                res.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                } else {
                    Toast.makeText(
                            VerifyOtpActivity.this,
                            "OTP verification failed",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(
                        VerifyOtpActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
    }
