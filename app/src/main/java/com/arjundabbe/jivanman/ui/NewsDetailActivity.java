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

    TextView titleText, descText, dateText;
    ImageView newsImage,btnSave, btnWhatsApp, btnShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Initialize views
        titleText = findViewById(R.id.detailTitle);
        descText = findViewById(R.id.detailDescription);
        dateText = findViewById(R.id.detailDate);
        newsImage = findViewById(R.id.detailImage);
        btnSave = findViewById(R.id.btnSave);
        btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnShare = findViewById(R.id.btnShare);

        // Get data from Intent
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String date = getIntent().getStringExtra("date");

        // Set data
        titleText.setText(title);
        descText.setText(desc);
        dateText.setText(date);
        Glide.with(this).load(imageUrl).into(newsImage);

        // Button Click Listeners
        setupButtonListeners(title, desc, imageUrl);

    }
    private void setupButtonListeners(String title, String description, String imageUrl) {
        // Save logic (simple toggle)
        btnSave.setOnClickListener(v -> {
            boolean isSaved = (btnSave.getTag() != null && (boolean) btnSave.getTag());
            if (isSaved) {
                btnSave.setImageResource(R.drawable.baseline_bookmark_border_24);
                btnSave.setTag(false);
                Toast.makeText(this, "Removed from saved (not implemented)", Toast.LENGTH_SHORT).show();
            } else {
                btnSave.setImageResource(R.drawable.baseline_bookmark_24);
                btnSave.setTag(true);

                // Save article
                SavedArticle article = new SavedArticle(title, description, imageUrl);
                SavedPrefsManager.saveArticle(this, article);
                Toast.makeText(this, "Article Saved", Toast.LENGTH_SHORT).show();
            }
        });


        // Share via WhatsApp
        btnWhatsApp.setOnClickListener(v -> {
            String shareText = title + "\n\n" + description + "\n\nजास्त माहिती: www.jivanman.in";
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            try {
                startActivity(whatsappIntent);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            }
        });

        // General share
        btnShare.setOnClickListener(v -> {
            String shareText = title + "\n\n" + description + "\n\nजास्त माहिती: www.jivanman.in";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

}
