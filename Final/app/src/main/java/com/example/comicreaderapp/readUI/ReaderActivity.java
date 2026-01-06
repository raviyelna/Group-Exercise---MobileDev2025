package com.example.comicreaderapp.readUI;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.api.LegacyApi;
import com.example.comicreaderapp.api.RetrofitClient;
import com.example.comicreaderapp.manga_model.NetworkSingleton; // NOTE: unused, kept for compatibility if needed

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "ReaderActivity";

    // UI Components
    private ImageButton btnBack, btnMore, btnPrev, btnNext, btnSettings;
    private TextView tvTitle, tvPageIndicator;
    private ImageView imgPage;
    private ScrollView scrollViewPage;

    // Data
    private String mangaId;
    private String chapterName;
    private List<String> imageUrls = new ArrayList<>();
    private int currentPage = 0;
    private int totalPages = 0;

    private LegacyApi legacyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide ActionBar if exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide system status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars());
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                );
            }
        } else {
            // Legacy support (Android 10 and below)
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        setContentView(R.layout.activity_reader);

        legacyApi = RetrofitClient.getInstance().create(LegacyApi.class);

        // Get data from intent
        mangaId = getIntent().getStringExtra("manga_id");
        chapterName = getIntent().getStringExtra("chapter_name");

        if (mangaId == null || chapterName == null) {
            Toast.makeText(this, "Missing manga or chapter info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        loadChapterImages();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_reader);
        btnMore = findViewById(R.id.btn_more_reader);
        btnPrev = findViewById(R.id.btn_prev_page);
        btnNext = findViewById(R.id.btn_next_page);
        btnSettings = findViewById(R.id.btn_reader_settings);
        tvTitle = findViewById(R.id.tv_reader_title);
        tvPageIndicator = findViewById(R.id.tv_page_indicator);
        imgPage = findViewById(R.id.img_page);
        scrollViewPage = findViewById(R.id.scroll_view_page);

        // Set chapter name as title
        tvTitle.setText(chapterName);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                displayPage();
                scrollViewPage.smoothScrollTo(0, 0);
            } else {
                Toast.makeText(this, "Already at first page", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                displayPage();
                scrollViewPage.smoothScrollTo(0, 0);
            } else {
                Toast.makeText(this, "Already at last page", Toast.LENGTH_SHORT).show();
            }
        });

        btnMore.setOnClickListener(v -> {
            Toast.makeText(this, "More options", Toast.LENGTH_SHORT).show();
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Reader settings", Toast.LENGTH_SHORT).show();
        });

        imgPage.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                displayPage();
                scrollViewPage.smoothScrollTo(0, 0);
            }
        });
    }

    private void loadChapterImages() {
        Map<String, String> params = new HashMap<>();
        params.put("manga_id", mangaId);
        params.put("chapter_name", chapterName);

        legacyApi.getData("read_images", params).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(ReaderActivity.this, "No images found for this chapter", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    String raw = response.body().string();
                    JSONObject root = new JSONObject(raw);

                    JSONArray imagesArray = root.optJSONArray("images");
                    totalPages = root.optInt("count", 0);

                    if (imagesArray != null && imagesArray.length() > 0) {
                        imageUrls.clear();

                        for (int i = 0; i < imagesArray.length(); i++) {
                            String imageUrl = imagesArray.getString(i);
                            imageUrls.add(imageUrl);
                        }

                        currentPage = 0;
                        displayPage();

                        Log.d(TAG, "Loaded " + imageUrls.size() + " images");

                    } else {
                        Toast.makeText(ReaderActivity.this, "No images found for this chapter", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (JSONException | IOException e) {
                    Log.e(TAG, "Error parsing chapter images", e);
                    Toast.makeText(ReaderActivity.this, "Error loading chapter images", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Network error loading images", t);
                Toast.makeText(ReaderActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayPage() {
        if (imageUrls.isEmpty() || currentPage < 0 || currentPage >= imageUrls.size()) {
            return;
        }

        tvPageIndicator.setText("Page " + (currentPage + 1) + " of " + totalPages);

        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);

        btnPrev.setAlpha(currentPage > 0 ? 1.0f : 0.5f);
        btnNext.setAlpha(currentPage < totalPages - 1 ? 1.0f : 0.5f);

        String imageUrl = imageUrls.get(currentPage);

        Log.d(TAG, "Loading page " + (currentPage + 1) + ": " + imageUrl);

        Glide.with(this)
                .load(imageUrl)
                .fitCenter()
                .placeholder(R.drawable.placeholder_cover)
                .error(R.drawable.placeholder_cover)
                .into(imgPage);

        scrollViewPage.post(() -> scrollViewPage.scrollTo(0, 0));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}