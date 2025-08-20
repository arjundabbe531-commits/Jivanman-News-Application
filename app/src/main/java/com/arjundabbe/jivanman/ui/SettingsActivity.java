package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.arjundabbe.jivanman.R;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {

    ImageView iconTheme, imageProfile;
    TextView text_profile_name, text_profile_role;
    Switch switchNotifications, switchTheme;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {




        // Load theme settings
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // View references
        text_profile_name = findViewById(R.id.text_profile_name);
        text_profile_role = findViewById(R.id.text_profile_role);
        switchNotifications = findViewById(R.id.switch_notifications);
        switchTheme = findViewById(R.id.switch_theme);
        iconTheme = findViewById(R.id.icon_theme);
        imageProfile = findViewById(R.id.image_profile);

        // Load name and role from shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("name", "अर्जुन डब्बे");
        String role = preferences.getString("role", "संपादक");
        text_profile_name.setText("~ " + name);
        text_profile_role.setText("भूमिका: " + role);

        // ✅ Load profile image from Base64 string
        String encodedImage = preferences.getString("profile_image", null);
        if (encodedImage != null) {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageProfile.setImageBitmap(bitmap);
        }

        // Adjust image appearance if needed
        imageProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageProfile.setClipToOutline(true); // Make sure you use a circular outline in XML

        // Theme toggle setup
        iconTheme.setImageResource(isDarkMode ? R.drawable.baseline_dark_mode_24 : R.drawable.baseline_light_mode_24);
        switchTheme.setChecked(isDarkMode);

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            iconTheme.setImageResource(isChecked ? R.drawable.baseline_dark_mode_24 : R.drawable.baseline_light_mode_24);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            Toast.makeText(this, isChecked ? "डार्क मोड चालू" : "लाइट मोड चालू", Toast.LENGTH_SHORT).show();
        });

        // Notifications switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Notifications " + (isChecked ? "चालू" : "बंद") + " केले", Toast.LENGTH_SHORT).show();
        });

        // Profile navigation
        findViewById(R.id.card_profile).setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, MyProfileActivity.class));
        });

        // QR Scanner
        findViewById(R.id.card_scanner).setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, MyQrActivity.class));
        });

        findViewById(R.id.card_aboutus).setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class));
        });

        findViewById(R.id.card_contactus).setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, ContactUsActivity.class));
        });

        // Logout logic
        findViewById(R.id.card_logout).setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLogin", false);
            editor.apply();

            Toast.makeText(SettingsActivity.this, "तुम्ही यशस्वीरित्या लॉगआउट झाला आहात", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }
}
