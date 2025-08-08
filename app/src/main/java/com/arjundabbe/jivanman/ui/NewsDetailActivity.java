package com.arjundabbe.jivanman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.models.SavedArticle;
import com.arjundabbe.jivanman.util.SavedPrefsManager;
import com.bumptech.glide.Glide;

public class NewsDetailActivity extends AppCompatActivity {

    // Declare UI components
    TextView titleText, descText, dateText;
    ImageView backArrow,newsImage, btnSave, btnWhatsApp, btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail); // Set the layout for this activity

        // Initialize views by their IDs from the XML layout
        backArrow = findViewById(R.id.backarrow);
        titleText = findViewById(R.id.detailTitle);
        descText = findViewById(R.id.detailDescription);
        dateText = findViewById(R.id.detailDate);
        newsImage = findViewById(R.id.detailImage);
        btnSave = findViewById(R.id.btnSave);
        btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnShare = findViewById(R.id.btnShare);

        // Get the data passed from previous activity through Intent
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String date = getIntent().getStringExtra("date");

        // Set the received data to views
        titleText.setText(title);
        descText.setText(desc);
        dateText.setText(date);

        // Load the image using Glide library from the URL
        Glide.with(this).load(imageUrl).into(newsImage);

        // Set button click logic
        setupButtonListeners(title, desc, imageUrl,date);

        backArrow.setOnClickListener(v -> finish());

    }


    // ========== Handles Click Events for Save, WhatsApp Share, and General Share ==========
    private void setupButtonListeners(String title, String description, String imageUrl, String date) {

    // ===== SAVE / UNSAVE BUTTON =====
        btnSave.setOnClickListener(v -> {
            boolean isSaved = (btnSave.getTag() != null && (boolean) btnSave.getTag());

            if (isSaved) {
                // Unsave logic (Not fully implemented yet)
                btnSave.setImageResource(R.drawable.baseline_bookmark_border_24); // Change to outline icon
                btnSave.setTag(false); // Mark as not saved
                Toast.makeText(this, "सेव्ह केलेल्या यादीतून काढले (अद्याप पूर्ण केलेले नाही)", Toast.LENGTH_SHORT).show();
            } else {
                // Save the article
                btnSave.setImageResource(R.drawable.baseline_bookmark_24); // Change to filled icon
                btnSave.setTag(true); // Mark as saved

                // Create a SavedArticle object and store it using SharedPreferences
                SavedArticle article = new SavedArticle(title, description, imageUrl, date);
                SavedPrefsManager.saveArticle(this, article);

                // Show confirmation
                Toast.makeText(this, "लेख सेव्ह केला", Toast.LENGTH_SHORT).show();
            }
        });

        // ===== SHARE VIA WHATSAPP ONLY =====
        btnWhatsApp.setOnClickListener(v -> {
            // Prepare Marathi share content
            String shareText = title + "\n\n" + description + "\n\nजास्त माहिती: www.jivanman.in";

            // Create intent to open WhatsApp
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            try {
                // Try to open WhatsApp
                startActivity(whatsappIntent);
            } catch (Exception e) {
                // If WhatsApp not installed
                Toast.makeText(this, "WhatsApp इन्स्टॉल केलेले नाही", Toast.LENGTH_SHORT).show();
            }
        });

        // ===== GENERAL SHARE BUTTON =====
        btnShare.setOnClickListener(v -> {
            // Prepare Marathi share content
            String shareText = title + "\n\n" + description + "\n\nजास्त माहिती: www.jivanman.in";

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            // Open share dialog
            startActivity(Intent.createChooser(shareIntent, "कशाद्वारे शेअर करायचे ते निवडा"));
        });
    }

}
