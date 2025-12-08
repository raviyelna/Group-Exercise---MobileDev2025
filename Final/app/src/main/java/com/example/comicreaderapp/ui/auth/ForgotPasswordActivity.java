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

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ApiErrorUtils;
import com.example.comicreaderapp.api.ApiService;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.ForgotPasswordRequest;
import com.example.comicreaderapp.model.GenericResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText editEmail;
    private Button buttonSend;
    private TextView textBack;
    private ProgressDialog progressDialog;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        editEmail = findViewById(R.id.editText_ForgotEmail);
        buttonSend = findViewById(R.id.button_SendReset);
        textBack = findViewById(R.id.textView_BackToLoginFromForgot);
        apiService = (ApiService) RetrofitClient.getApiService();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editEmail.setError("Enter a valid email");
                    return;
                }
                sendForgotRequest(email);
            }
        });

        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void sendForgotRequest(final String email) {
        progressDialog.show();
        ForgotPasswordRequest req = new ForgotPasswordRequest(email);
        // Gọi với action=forgot
        apiService.forgotPassword("forgot", req).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse body = response.body();
                    if (body.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this, "OTP sent to " + email, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ForgotPasswordActivity.this, OtpAuthActivity.class);
                        i.putExtra("email", email);
                        startActivity(i);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    ApiErrorUtils.GenericError err = ApiErrorUtils.parseError(response);
                    String msg = err != null && err.message != null ? err.message : "Failed to send OTP";
                    Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
