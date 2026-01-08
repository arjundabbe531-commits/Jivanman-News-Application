package com.arjundabbe.jivanman.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.arjundabbe.jivanman.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    // -------------------------------
    // Views
    // -------------------------------
    private LinearLayout cardAddNews, cardManageNews, cardReporters, cardAnalytics;
    private ImageView ivLogout;
    private TextView tvAdminEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply saved dark/light theme before layout
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize views
        cardAddNews = findViewById(R.id.cardAddNews);
        cardManageNews = findViewById(R.id.cardManageNews);
        cardReporters = findViewById(R.id.cardReporters);
        cardAnalytics = findViewById(R.id.cardAnalytics);
        ivLogout = findViewById(R.id.ivLogout);
        tvAdminEmail = findViewById(R.id.tvAdminEmail);

        // Display current admin email
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tvAdminEmail.setText(
                    FirebaseAuth.getInstance().getCurrentUser().getEmail()
            );
        }

        // -------------------------------
        // Card navigation actions
        // -------------------------------
        cardAddNews.setOnClickListener(v ->
                startActivity(new Intent(this, AddNewsActivity.class)));

        cardManageNews.setOnClickListener(v ->
                startActivity(new Intent(this, ManageNewsActivity.class)));

        cardReporters.setOnClickListener(v ->
                startActivity(new Intent(this, ReportersActivity.class)));

        cardAnalytics.setOnClickListener(v ->
                startActivity(new Intent(this, AnalyticsActivity.class)));

        // Logout action
        ivLogout.setOnClickListener(v -> showLogoutDialog());
    }

    /** Apply saved theme from SharedPreferences */
    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean dark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /** Show logout confirmation dialog */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("लॉगआउट")
                .setMessage("आपण लॉगआउट करू इच्छिता का?")
                .setPositiveButton("हो", (d, w) -> logoutAdmin())
                .setNegativeButton("रद्द करा", null)
                .show();
    }

    /** Sign out admin and clear shared preferences */
    private void logoutAdmin() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /** Override back press to move app to background */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
