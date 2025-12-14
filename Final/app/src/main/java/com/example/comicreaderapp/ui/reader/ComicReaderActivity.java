package com.example.comicreaderapp.ui.reader;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.comicreaderapp.R;

public class ComicReaderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_reader); // Ensure layout exists
        
        String chapterName = getIntent().getStringExtra("chapter_name");
        
        // Basic placeholder
        TextView tv = findViewById(R.id.tv_reader_content);
        if(tv != null && chapterName != null) {
            tv.setText("Reading: " + chapterName);
        }
    }
}
