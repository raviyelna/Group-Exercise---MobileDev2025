package com.example.comicreaderapp.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ApiErrorUtils;
import com.example.comicreaderapp.api.ApiService;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.ForgotPasswordRequest;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText editEmail;
    private Button buttonSend;
    private ProgressDialog progressDialog;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editEmail = findViewById(R.id.et_forgot_email);
        buttonSend = findViewById(R.id.btn_send_reset);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang gửi OTP...");
        progressDialog.setCancelable(false);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.loading.observe(this, isLoading -> {
            if (isLoading != null) {
                if (isLoading) progressDialog.show();
                else progressDialog.dismiss();
            }
        });

        authViewModel.error.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );

        authViewModel.forgotResult.observe(this, response -> {
            if (response == null) return;

            if (response.isSuccess()) {
                Toast.makeText(this, "OTP đã được gửi vào email", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, VerifyOtpActivity.class);
                intent.putExtra("email", editEmail.getText().toString().trim());
                startActivity(intent);
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        buttonSend.setOnClickListener(v -> {
            String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.forgot(email);
        });
    }
}

