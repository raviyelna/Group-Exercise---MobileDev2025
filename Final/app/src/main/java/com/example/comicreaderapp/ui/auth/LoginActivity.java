package com.example.comicreaderapp.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.model.User;
import com.example.comicreaderapp.ui.account.AccountActivity;
import com.example.comicreaderapp.ui.account.SessionManager;
import com.example.comicreaderapp.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private AuthViewModel authViewModel;

    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        editEmail = findViewById(R.id.et_email);
        editPassword = findViewById(R.id.et_password);
        buttonLogin = findViewById(R.id.btn_login);

        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        // ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Loading
        authViewModel.loading.observe(this, isLoading -> {
            if (Boolean.TRUE.equals(isLoading)) progressDialog.show();
            else progressDialog.dismiss();
        });

        // Error
        authViewModel.error.observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Login result
        authViewModel.loginResult.observe(this, response -> {
            if (response == null) return;

            if (response.isSuccess()) {

                User user = extractUser(response);

                if (user == null) {
                    Toast.makeText(this, "Parse user failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                SessionManager session = new SessionManager(this);
                session.saveUser(user);

                startActivity(new Intent(this, AccountActivity.class));
                finish();

            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Login click
        buttonLogin.setOnClickListener(v -> {
            String email = editEmail.getText() != null
                    ? editEmail.getText().toString().trim()
                    : "";
            String password = editPassword.getText() != null
                    ? editPassword.getText().toString().trim()
                    : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.login(email, password);
        });
    }

    // ===== convert Object -> User =====
    private User extractUser(GenericResponse response) {
        if (response.getData() == null) return null;
        return gson.fromJson(
                gson.toJson(response.getData()),
                User.class
        );
    }
}
