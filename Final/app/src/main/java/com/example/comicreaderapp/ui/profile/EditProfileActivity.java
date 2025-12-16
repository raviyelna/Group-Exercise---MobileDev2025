package com.example.comicreaderapp.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.ApiService;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.model.GenericResponse;
import com.example.comicreaderapp.ui.account.SessionManager;
import com.google.android.material.button.MaterialButton;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private EditText edtUsername;
    private MaterialButton btnSave;

    private EditText Linkprofile;
    private SessionManager session;
    private Uri selectedImageUri;

    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgAvatar = findViewById(R.id.img_avatar_edit);
        edtUsername = findViewById(R.id.edt_username);
        btnSave = findViewById(R.id.btn_save);
        Linkprofile = findViewById(R.id.LinkProfilePicture);
        session = new SessionManager(this);
        api = RetrofitClient.getApiService();

        // Load current data
        edtUsername.setText(session.getUsername());
        if (session.getAvatar() != null) {
            Glide.with(this)
                    .load(session.getAvatar())
                    .circleCrop()
                    .into(imgAvatar);
        }



        // Pick image
        imgAvatar.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnSave.setOnClickListener(v -> uploadProfile());
    }

    // ===== Image Picker =====
    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedImageUri = uri;
                            imgAvatar.setImageURI(uri);
                        }
                    }
            );

    // ===== Upload Profile =====
    private void uploadProfile() {
        String newUsername = edtUsername.getText().toString().trim();
        String linkAvatar  = Linkprofile.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username required", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody usernameBody =
                RequestBody.create(MediaType.parse("text/plain"), newUsername);

        RequestBody linkBody = null;
        MultipartBody.Part imagePart = null;

        // ===== Case 1: User nhập link avatar, KHÔNG chọn ảnh =====
        if (!linkAvatar.isEmpty() && selectedImageUri == null) {
            linkBody = RequestBody.create(
                    MediaType.parse("text/plain"),
                    linkAvatar
            );
        }

        // ===== Case 2: User chọn ảnh =====
        else if (selectedImageUri != null) {
            byte[] imageBytes = FileUtils.readBytes(this, selectedImageUri);

            if (imageBytes == null) {
                Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
                return;
            }

            imagePart = MultipartBody.Part.createFormData(
                    "avatar",
                    "avatar.jpg",
                    RequestBody.create(
                            MediaType.parse("image/*"),
                            imageBytes
                    )
            );
        }

        api.updateProfile("profile_update", usernameBody, linkBody, imagePart)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call,
                                           Response<GenericResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            session.saveUserName(newUsername);

                            if (response.body().getData() != null) {
                                session.saveAvatar(response.body().getData().toString());
                            }

                            Toast.makeText(EditProfileActivity.this,
                                    "Profile updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditProfileActivity.this,
                                    "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }



                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
