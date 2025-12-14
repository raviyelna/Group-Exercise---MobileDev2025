package com.example.comicreaderapp.readUI;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.manga_model.NetworkSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "ReaderActivity";
    private static final String BASE_URL = "http://10.0.2.2/api/getData/request.php";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

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
            // TODO: Implement more options menu
            Toast.makeText(this, "More options", Toast.LENGTH_SHORT).show();
        });

        btnSettings.setOnClickListener(v -> {
            // TODO: Implement reader settings
            Toast.makeText(this, "Reader settings", Toast.LENGTH_SHORT).show();
        });

        // Add tap zones for easier navigation
        imgPage.setOnClickListener(v -> {
            // Center tap can toggle UI visibility or do nothing
            // For now, we'll use it to go to next page
            if (currentPage < totalPages - 1) {
                currentPage++;
                displayPage();
                scrollViewPage.smoothScrollTo(0, 0);
            }
        });
    }

    private void loadChapterImages() {
        String url = BASE_URL + "?r=read_images&manga_id=" + mangaId + "&chapter_name=" + chapterName;

        Log.d(TAG, "Loading images from: " + url);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);

                        // Get the images array
                        JSONArray imagesArray = root.optJSONArray("images");
                        totalPages = root.optInt("count", 0);

                        if (imagesArray != null && imagesArray.length() > 0) {
                            imageUrls.clear();

                            for (int i = 0; i < imagesArray.length(); i++) {
                                String imageUrl = imagesArray.getString(i);
                                imageUrls.add(imageUrl);
                            }

                            // Display first page
                            currentPage = 0;
                            displayPage();

                            Log.d(TAG, "Loaded " + imageUrls.size() + " images");

                        } else {
                            Toast.makeText(this, "No images found for this chapter", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing chapter images", e);
                        Toast.makeText(this, "Error loading chapter images", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error loading images", error);
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    finish();
                }
        );

        NetworkSingleton.getInstance(this)
                .getRequestQueue()
                .add(request);
    }

    private void displayPage() {
        if (imageUrls.isEmpty() || currentPage < 0 || currentPage >= imageUrls.size()) {
            return;
        }

        // Update page indicator
        tvPageIndicator.setText("Page " + (currentPage + 1) + " of " + totalPages);

        // Update button states
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);

        // Set alpha to show disabled state
        btnPrev.setAlpha(currentPage > 0 ? 1.0f : 0.5f);
        btnNext.setAlpha(currentPage < totalPages - 1 ? 1.0f : 0.5f);

        // Load image with Glide
        String imageUrl = imageUrls.get(currentPage);

        Log.d(TAG, "Loading page " + (currentPage + 1) + ": " + imageUrl);

        Glide.with(this)
                .load(imageUrl)
                .fitCenter()
                .placeholder(R.drawable.placeholder_cover)
                .error(R.drawable.placeholder_cover)
                .into(imgPage);

        // Scroll to top when changing pages
        scrollViewPage.post(() -> scrollViewPage.scrollTo(0, 0));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}