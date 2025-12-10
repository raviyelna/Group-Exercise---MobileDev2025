package com.example.comicreaderapp.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.model.VerifyOtpResponse;
import com.example.comicreaderapp.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class OtpAuthActivity extends AppCompatActivity {

    private TextView otpBox1, otpBox2, otpBox3, otpBox4, otpBox5, otpBox6;
    private TextView tvResend;
    private MaterialButton btnVerifyOtp;

    private ProgressDialog progressDialog;
    private AuthViewModel authViewModel;

    private String email;   // nhận từ ForgotPasswordActivity

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_auth);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");

        // Ánh xạ view
        otpBox1 = findViewById(R.id.otp_box1);
        otpBox2 = findViewById(R.id.otp_box2);
        otpBox3 = findViewById(R.id.otp_box3);
        otpBox4 = findViewById(R.id.otp_box4);
        otpBox5 = findViewById(R.id.otp_box5);
        otpBox6 = findViewById(R.id.otp_box6);
        tvResend = findViewById(R.id.tv_resend);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang kiểm tra OTP...");
        progressDialog.setCancelable(false);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // ====== Observe loading / error ======
        authViewModel.loading.observe(this, isLoading -> {
            if (isLoading != null) {
                if (isLoading) progressDialog.show();
                else progressDialog.dismiss();
            }
        });

        authViewModel.error.observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // ====== Observe verifyOtpResult ======
        authViewModel.verifyOtpResult.observe(this, res -> {
            if (res == null) return;

            if (res.isSuccess()) {
                Toast.makeText(this, res.getMessage(), Toast.LENGTH_SHORT).show();

                // Lấy resetToken để đổi mật khẩu
                String resetToken = res.getResetToken();

                Intent intent = new Intent(this, ResetPasswordActivity.class);
                intent.putExtra("reset_token", resetToken);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, res.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // ====== Xử lý click Verify ======
        btnVerifyOtp.setOnClickListener(v -> {
            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "Thiếu email, vui lòng quay lại bước trước", Toast.LENGTH_SHORT).show();
                return;
            }

            String otp = collectOtpFromBoxes();
            if (otp.length() != 6) {
                Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.verifyOtp(email, otp);
        });

        // ====== Xử lý Resend (nếu muốn gọi lại forgot) ======
        tvResend.setOnClickListener(v -> {
            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "Thiếu email, không thể gửi lại OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi lại forgot để backend gửi lại OTP
            authViewModel.forgot(email);
        });
    }

    /**
     * Ghép OTP từ 6 ô TextView
     */
    private String collectOtpFromBoxes() {
        String d1 = safeText(otpBox1);
        String d2 = safeText(otpBox2);
        String d3 = safeText(otpBox3);
        String d4 = safeText(otpBox4);
        String d5 = safeText(otpBox5);
        String d6 = safeText(otpBox6);
        return d1 + d2 + d3 + d4 + d5 + d6;
    }

    private String safeText(TextView tv) {
        CharSequence cs = tv.getText();
        return cs == null ? "" : cs.toString().trim();
    }
}
