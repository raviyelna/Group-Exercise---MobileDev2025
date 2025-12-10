package com.example.comicreaderapp.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.RegisterRequest;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editUsername;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private TextInputEditText editConfirmPassword;
    private Button buttonSend;
    private ProgressDialog progressDialog;
    private ApiService apiService;

    private TextView backToLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editUsername = findViewById(R.id.et_name);
        editEmail = findViewById(R.id.et_reg_email);
        editPassword = findViewById(R.id.et_reg_pass);
        editConfirmPassword = findViewById(R.id.et_reg_pass_confirm);
        buttonSend = findViewById(R.id.btn_create_account);
        backToLogin = findViewById(R.id.tv_have_account);

        apiService = RetrofitClient.getApiService();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Nút Register
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editUsername.getText() != null
                        ? editUsername.getText().toString().trim()
                        : "";
                String email = editEmail.getText() != null
                        ? editEmail.getText().toString().trim()
                        : "";
                String password = editPassword.getText() != null
                        ? editPassword.getText().toString().trim()
                        : "";
                String confirmPassword = editConfirmPassword.getText() != null
                        ? editConfirmPassword.getText().toString().trim()
                        : "";

                if (username.isEmpty()) {
                    editUsername.setError("Enter username");
                    return;
                }

                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editEmail.setError("Enter a valid email");
                    return;
                }

                if (password.isEmpty() || password.length() < 6) {
                    editPassword.setError("Password must be at least 6 characters");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    editConfirmPassword.setError("Password does not match");
                    return;
                }

                sendRegisterRequest(username, email, password);
            }
        });

        // Text "Back to login"
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void sendRegisterRequest(final String username, final String email, final String password) {
        progressDialog.show();
        RegisterRequest req = new RegisterRequest(username, email, password);

        // Gọi với action=register
        apiService.register("register", req).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse body = response.body();
                    if (body.isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_LONG).show();

                        // Sau khi đăng ký xong, chuyển sang LoginActivity
                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        // Hoặc có thể auto-login, tuỳ bạn
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, body.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    ApiErrorUtils.GenericError err = ApiErrorUtils.parseError(response);
                    String msg = err != null && err.message != null ? err.message : "Failed to register";
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("error", t.getMessage());
            }
        });
    }
}
