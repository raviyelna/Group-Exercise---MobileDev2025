package com.example.comicreaderapp.ui.auth;

import android.app.ProgressDialog;
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
import com.example.comicreaderapp.model.ResetPasswordRequest;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.*;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText editNew, editConfirm;
    private Button buttonUpdate;
    private TextView textBack;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private String resetToken;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editNew = findViewById(R.id.editText_NewPassword);
        editConfirm = findViewById(R.id.editText_ConfirmNewPassword);
        buttonUpdate = findViewById(R.id.button_ResetPassword);
        textBack = findViewById(R.id.textView_BackToLoginFromReset);

        apiService = (ApiService) RetrofitClient.getApiService();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        resetToken = getIntent().getStringExtra("resetToken");
        email = getIntent().getStringExtra("email");

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = editNew.getText() != null ? editNew.getText().toString() : "";
                String confirm = editConfirm.getText() != null ? editConfirm.getText().toString() : "";
                if (TextUtils.isEmpty(pass) || pass.length() < 6) {
                    editNew.setError("Password must be at least 6 characters");
                    return;
                }
                if (!pass.equals(confirm)) {
                    editConfirm.setError("Passwords do not match");
                    return;
                }
                performReset(pass);
            }
        });

        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void performReset(String newPassword) {
        if (resetToken == null || resetToken.isEmpty()) {
            Toast.makeText(this, "Missing reset token", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.show();
        ResetPasswordRequest req = new ResetPasswordRequest(resetToken, newPassword);
        // Gọi với action=reset-password
        apiService.resetPassword("reset-password", req).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse body = response.body();
                    if (body.isSuccess()) {
                        Toast.makeText(ResetPasswordActivity.this, "Password updated", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    ApiErrorUtils.GenericError err = ApiErrorUtils.parseError(response);
                    String msg = err != null && err.message != null ? err.message : "Failed to update password";
                    Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
