package com.example.comicreaderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.comicreaderapp.R;
import com.example.comicreaderapp.manga_model.AllComic;
import com.example.comicreaderapp.manga_model.AllComicsAdapter;
import com.example.comicreaderapp.manga_model.NetworkSingleton;
import com.example.comicreaderapp.readUI.MangaDetailActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllComicsActivity extends AppCompatActivity {

    private static final String API_URL =
            "http://10.0.2.2/api/getData/request.php?r=all_manga";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comics);

        RecyclerView rv = findViewById(R.id.rv_recent_full);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<AllComic> list = new ArrayList<>();

        AllComicsAdapter adapter = new AllComicsAdapter(
                this,
                list,
                comic -> {
                    Intent i = new Intent(this, MangaDetailActivity.class);
                    i.putExtra("manga_id", comic.manga_id);
                    startActivity(i);
                }
        );

        rv.setAdapter(adapter);

        loadAllManga(list, adapter);
    }

    private void loadAllManga(List<AllComic> list, AllComicsAdapter adapter) {

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                response -> {
                    try {
                        JSONArray arr = response.getJSONArray("data");
                        list.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            AllComic c = new AllComic();
                            c.manga_id = o.getString("manga_id");
                            c.title = o.getString("title");
                            c.cover = o.getString("cover");

                            list.add(c);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Parse error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this,
                        "API error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show()
        );

        NetworkSingleton
                .getInstance(this)
                .getRequestQueue()
                .add(req);
    }

}
