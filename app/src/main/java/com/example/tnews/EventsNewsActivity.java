package com.example.tnews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.tnews.model.News;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventsNewsActivity extends BaseActivity {
    private static final String TAG = "EventsNewsActivity";
    private BottomNavigationView bottomNavigationView;
    private LinearLayout newsContainer;
    private FirebaseFirestore db;
    private List<News> eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_news);

        initializeViews();
        setupBottomNavigation();
        setupFirestore();
        fetchEventsNews();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_events);

        MaterialToolbar toolbar = findViewById(R.id.dropdown_anchor);
        setSupportActionBar(toolbar);

        // Find the LinearLayout inside ScrollView where cards will be added
        newsContainer = findViewById(R.id.news_container);
        if (newsContainer == null) {
            Log.e(TAG, "news_container not found in layout");
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_sports) {
                    startActivity(new Intent(EventsNewsActivity.this, SportsNewsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_academic) {
                    startActivity(new Intent(EventsNewsActivity.this, AcademicNewsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_events) {
                    return true;
                }

                return false;
            }
        });
    }

    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
        eventsList = new ArrayList<>();
    }

    private void fetchEventsNews() {
        db.collection("news")
                .whereEqualTo("newsType", "events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            News news = document.toObject(News.class);
                            eventsList.add(news);
                            Log.d(TAG, "News fetched: " + news.getTitle());
                        }
                        displayNews();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(EventsNewsActivity.this, "Failed to fetch news", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching events news", e);
                    Toast.makeText(EventsNewsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void displayNews() {
        if (newsContainer == null) {
            Log.e(TAG, "newsContainer is null, cannot display news");
            return;
        }
        newsContainer.removeAllViews();
        for (News news : eventsList) {
            createNewsCard(news);
        }
        if (eventsList.isEmpty()) {
            showNoNewsMessage();
        }
    }

    private void createNewsCard(News news) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(20));
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(dpToPx(4));
        cardView.setRadius(dpToPx(12)); // Rounded corners
        cardView.setCardBackgroundColor(Color.parseColor("#F9F9F9")); // Light gray background

        // Create the main LinearLayout for the card (horizontal orientation)
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);  // Horizontal layout for image and content
        mainLayout.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6)); // Padding around content

        // Create ImageView (Image will be on the left)
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                dpToPx(120), // Fixed width for the image
                dpToPx(120)  // Fixed height for the image
        );
        imageParams.setMargins(0, 0, dpToPx(12), 0); // Margin to the right of the image
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Load image using Picasso (you might need to add Picasso dependency)
        if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(news.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .transform(new com.squareup.picasso.Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(output);
                            Paint paint = new Paint();
                            paint.setAntiAlias(true);
                            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                            canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, source.getWidth() / 2, paint);
                            if (output != source) {
                                source.recycle();
                            }
                            return output;
                        }

                        @Override
                        public String key() {
                            return "circle";
                        }
                    })
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground); // Default image
        }

        // Create a vertical LinearLayout for content (title, description, etc.)
        LinearLayout contentLayout = new LinearLayout(this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // 1f weight to take remaining space
        contentLayout.setLayoutParams(contentParams);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setGravity(Gravity.START); // Align text to the start (left)

        // Create TextViews for title, content, description
        TextView titleTextView = createTextView(news.getTitle(), 18, true, 0xFF000000); // Bold, larger title
        TextView contentTextView = createTextView(news.getContent(), 14, false, 0xFF888888); // Smaller description

        // Truncate the description to show only 1-2 lines
        TextView descriptionTextView = new TextView(this);
        descriptionTextView.setText(news.getDescription());
        descriptionTextView.setTextSize(14);
        descriptionTextView.setTextColor(0xFF333333);
        descriptionTextView.setMaxLines(2); // Show only 1-2 lines
        descriptionTextView.setEllipsize(TextUtils.TruncateAt.END); // Truncate the rest of the text with ellipsis (...)

        // Create Read News button
        Button readButton = new Button(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, dpToPx(5), 0, 0);
        readButton.setLayoutParams(buttonParams);
        readButton.setText("Read More");
        readButton.setBackgroundColor(Color.TRANSPARENT);
        readButton.setTextColor(Color.parseColor("#004643"));

        // UPDATED: Add click listener for the button to open NewsDetailActivity
        readButton.setOnClickListener(v -> {
            // Create intent to open NewsDetailActivity
            Intent intent = new Intent(EventsNewsActivity.this, NewsDetailActivity.class);

            // Pass news data to the new activity
            intent.putExtra("news_title", news.getTitle());
            intent.putExtra("news_content", news.getContent());
            intent.putExtra("news_description", news.getDescription());
            intent.putExtra("news_date", news.getDate());
            intent.putExtra("news_image_url", news.getImageUrl());

            // Start the new activity
            startActivity(intent);
        });

        // Add views to the content layout
        contentLayout.addView(titleTextView);
        contentLayout.addView(contentTextView);
        contentLayout.addView(descriptionTextView);
        contentLayout.addView(readButton);

        // Add ImageView and content layout to the main layout (horizontal)
        mainLayout.addView(imageView);
        mainLayout.addView(contentLayout);

        // Add the main layout to the card
        cardView.addView(mainLayout);

        // Add the card to the news container (assumed to be a LinearLayout)
        newsContainer.addView(cardView);
    }


    private TextView createTextView(String text, int textSize, boolean bold, int color) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(4), 0, 0);
        textView.setLayoutParams(params);
        textView.setText(text != null ? text : "");
        textView.setTextSize(textSize);
        textView.setTextColor(color);
        if (bold) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        return textView;
    }

    private void showNoNewsMessage() {
        TextView noNewsText = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(32), 0, 0);
        noNewsText.setLayoutParams(params);
        noNewsText.setText("No Events news available at the moment.");
        noNewsText.setTextSize(16);
        noNewsText.setTextColor(0xFF666666);
        noNewsText.setGravity(android.view.Gravity.CENTER);
        newsContainer.addView(noNewsText);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
