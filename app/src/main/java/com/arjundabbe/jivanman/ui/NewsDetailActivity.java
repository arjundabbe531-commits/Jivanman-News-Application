package com.arjundabbe.jivanman.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.arjundabbe.jivanman.NetworkChangeListener;
import com.arjundabbe.jivanman.R;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NewsDetailActivity
 * ------------------
 * Displays full news article details.
 * Handles save/bookmark, share, analytics tracking,
 * read-time tracking, and theme management.
 */
public class NewsDetailActivity extends AppCompatActivity {

    // UI Components
    private TextView titleText, descText, dateText, writtenBy;
    private ImageView backArrow, newsImage, btnSave, btnWhatsApp, btnShare;

    // Network listener
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAnalytics analytics;

    // Data variables
    private String uid;
    private String newsId;
    private List<String> categoryList;
    private String reporter;
    private String title;

    // Read time tracking
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply saved theme before UI inflation
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Firebase initialization
        db = FirebaseFirestore.getInstance();
        analytics = FirebaseAnalytics.getInstance(this);
        uid = FirebaseAuth.getInstance().getUid();

        // Start read timer
        startTime = System.currentTimeMillis();

        // View binding
        backArrow = findViewById(R.id.backarrow);
        titleText = findViewById(R.id.detailTitle);
        descText = findViewById(R.id.detailDescription);
        dateText = findViewById(R.id.detailDate);
        newsImage = findViewById(R.id.detailImage);
        btnSave = findViewById(R.id.btnSave);
        btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnShare = findViewById(R.id.btnShare);
        writtenBy = findViewById(R.id.writternby);

        // Get data from intent
        title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String date = getIntent().getStringExtra("date");
        newsId = getIntent().getStringExtra("newsId");
        reporter = getIntent().getStringExtra("reporter");
        categoryList = getIntent().getStringArrayListExtra("category");

        // Safety check
        if (newsId == null) {
            Toast.makeText(this, "News ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (categoryList == null) categoryList = new ArrayList<>();

        // Populate UI
        titleText.setText(title);
        descText.setText(description);
        dateText.setText(date != null ? date : "Date not available");
        writtenBy.setText(
                reporter != null && !reporter.isEmpty()
                        ? "लेखक: " + reporter
                        : "लेखक: जिवनमन टीम"
        );

        // Load image
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(newsImage);

        // Back navigation
        backArrow.setOnClickListener(v -> finish());

        // Setup save & share features
        setupSaveButton(title, description, imageUrl, date);
        setupShareButtons(title, description);

        // Analytics events
        logNewsOpened();
        logCategoryRead();
    }

    // ---------------- ANALYTICS ----------------

    private void logNewsOpened() {
        Bundle bundle = new Bundle();
        bundle.putString("news_id", newsId);
        bundle.putString("title", title);

        if (!categoryList.isEmpty()) {
            bundle.putString("category", categoryList.get(0));
        }

        analytics.logEvent("news_opened", bundle);
    }

    private void logCategoryRead() {
        if (!categoryList.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString("category", categoryList.get(0));
            analytics.logEvent("category_read", bundle);
        }
    }

    private void logReadTime() {
        long duration = (System.currentTimeMillis() - startTime) / 1000;

        Bundle bundle = new Bundle();
        bundle.putString("news_id", newsId);
        bundle.putLong("seconds", duration);

        analytics.logEvent("news_read_time", bundle);
    }

    // ---------------- SAVE / BOOKMARK ----------------

    private void setupSaveButton(String title, String description,
                                 String imageUrl, String date) {

        DocumentReference newsRef = db.collection("users")
                .document(uid)
                .collection("savedNews")
                .document(newsId);

        // Check saved state
        newsRef.get().addOnSuccessListener(doc -> {
            boolean isSaved = doc.exists();
            btnSave.setTag(isSaved);
            btnSave.setImageResource(
                    isSaved
                            ? R.drawable.baseline_bookmark_24
                            : R.drawable.baseline_bookmark_border_24
            );
        });

        btnSave.setOnClickListener(v -> {
            boolean isSaved = btnSave.getTag() != null && (boolean) btnSave.getTag();

            if (isSaved) {
                // Remove bookmark
                newsRef.delete().addOnSuccessListener(aVoid -> {
                    btnSave.setImageResource(R.drawable.baseline_bookmark_border_24);
                    btnSave.setTag(false);
                    Toast.makeText(this,
                            "सेव्ह केलेल्या यादीतून काढले",
                            Toast.LENGTH_SHORT).show();
                });

            } else {
                // Save bookmark
                Map<String, Object> news = new HashMap<>();
                news.put("newsId", newsId);
                news.put("title", title);
                news.put("description", description);
                news.put("imageUrl", imageUrl);
                news.put("category", categoryList);
                news.put("timestamp", System.currentTimeMillis());
                news.put("reporter",
                        reporter != null ? reporter : "जिवनमान टीम");

                newsRef.set(news).addOnSuccessListener(aVoid -> {
                    btnSave.setImageResource(R.drawable.baseline_bookmark_24);
                    btnSave.setTag(true);
                    Toast.makeText(this,
                            "लेख सेव्ह केला",
                            Toast.LENGTH_SHORT).show();

                    analytics.logEvent("news_bookmarked", null);
                });
            }
        });
    }

    // ---------------- SHARE ----------------

    private void setupShareButtons(String title, String description) {

        btnWhatsApp.setOnClickListener(v -> {
            analytics.logEvent("news_shared_whatsapp", null);

            String shareText =
                    title + "\n\n" + description +
                            "\n\nजास्त माहिती: www.jivanman.in";

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.setPackage("com.whatsapp");
            i.putExtra(Intent.EXTRA_TEXT, shareText);

            try {
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this,
                        "WhatsApp इन्स्टॉल केलेले नाही",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnShare.setOnClickListener(v -> {
            analytics.logEvent("news_shared", null);

            String shareText =
                    title + "\n\n" + description +
                            "\n\nजास्त माहिती: www.jivanman.in";

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(i,
                    "कशाद्वारे शेअर करायचे ते निवडा"));
        });
    }

    // ---------------- LIFECYCLE ----------------

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(networkChangeListener,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Track total read time
        logReadTime();
    }

    // ---------------- THEME ----------------

    private void applySavedTheme() {
        SharedPreferences prefs =
                getSharedPreferences("app_settings", MODE_PRIVATE);

        AppCompatDelegate.setDefaultNightMode(
                prefs.getBoolean("dark_mode", false)
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
