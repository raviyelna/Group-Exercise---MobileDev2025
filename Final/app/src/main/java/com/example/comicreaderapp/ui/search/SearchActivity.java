package com.example.comicreaderapp.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;
import com.example.comicreaderapp.manga_model.FeaturedAdapter;
import com.example.comicreaderapp.manga_model.Manga;
import com.example.comicreaderapp.readUI.MangaDetailActivity;
import com.example.comicreaderapp.utils.AppDataCache;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView rvResult;
    private TextView tvEmpty;

    private final ArrayList<Manga> resultList = new ArrayList<>();
    private FeaturedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.et_search);
        rvResult = findViewById(R.id.rv_search_result);
        tvEmpty = findViewById(R.id.empty_search);

        adapter = new FeaturedAdapter(this, resultList, manga ->
                startActivity(
                        new android.content.Intent(this, MangaDetailActivity.class)
                                .putExtra("manga_id", manga.manga_id)
                )
        );

        rvResult.setLayoutManager(new LinearLayoutManager(this));
        rvResult.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOffline(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterOffline(String keyword) {
        resultList.clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            adapter.notifyDataSetChanged();
            tvEmpty.setVisibility(View.GONE);
            return;
        }

        String key = keyword.toLowerCase().trim();

        for (Manga m : AppDataCache.SEARCH_SOURCE) {
            if (m.title != null && m.title.toLowerCase().contains(key)) {
                resultList.add(m);
            }
        }

        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(resultList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
