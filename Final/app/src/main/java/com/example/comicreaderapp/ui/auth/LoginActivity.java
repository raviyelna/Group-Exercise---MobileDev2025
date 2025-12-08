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

import com.example.comicreaderapp.MainActivity;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ApiErrorUtils;
import com.example.comicreaderapp.api.ApiService;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.ForgotPasswordRequest;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.LoginRequest;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail;
    private Button buttonSend;
    private TextInputEditText editPassword;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private TextView textView_Register;
    private TextView textView_ForgotPassword;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editText_TextEmailAddress);
        buttonSend = findViewById(R.id.button_Login);
        editPassword = findViewById(R.id.editText_TextPassword);
        textView_Register = findViewById(R.id.textView_Register);
        textView_ForgotPassword = findViewById(R.id.textView_ForgotPassword);
        apiService = (ApiService) RetrofitClient.getApiService();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
                String password = editPassword.getText() != null ? editPassword.getText().toString().trim() : "";
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editEmail.setError("Enter a valid email");
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    editPassword.setError("Password must be at least 6 characters");
                    return;
                }
                sendLoginRequest(email, password);
            }
        });

        textView_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        textView_ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    private void sendLoginRequest(final String email, final String password) {
        progressDialog.show();
        LoginRequest req = new LoginRequest(email, password);
        // Gọi với action=login
        apiService.login("login", req).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse body = response.body();
                    if (body.isSuccess()) {
                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(LoginActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    ApiErrorUtils.GenericError err = ApiErrorUtils.parseError(response);
                    String msg = err != null && err.message != null ? err.message : "Failed to login";
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
