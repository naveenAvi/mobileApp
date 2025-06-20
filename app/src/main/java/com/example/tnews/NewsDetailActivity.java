package com.example.tnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView newsImageView;
    private TextView newsTitleTextView;
    private TextView newsDateTextView;
    private TextView newsContentTextView;
    private TextView newsDescriptionTextView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initializeViews();
        setupToolbar();
        loadNewsData();
    }

    private void initializeViews() {
        newsImageView = findViewById(R.id.news_detail_image);
        newsTitleTextView = findViewById(R.id.news_detail_title);
        newsDateTextView = findViewById(R.id.news_detail_date);
        newsContentTextView = findViewById(R.id.news_detail_content);
        newsDescriptionTextView = findViewById(R.id.news_detail_description);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("News Detail");
        }
    }

    private void loadNewsData() {
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("news_title");
            String content = intent.getStringExtra("news_content");
            String description = intent.getStringExtra("news_description");
            String date = intent.getStringExtra("news_date");
            String imageUrl = intent.getStringExtra("news_image_url");

            // Set data to views
            newsTitleTextView.setText(title != null ? title : "No Title");
            newsContentTextView.setText(content != null ? content : "No Content");
            newsDescriptionTextView.setText(description != null ? description : "No Description");
            newsDateTextView.setText(date != null ? date : "No Date");

            // Load image
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(newsImageView);
            } else {
                newsImageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}