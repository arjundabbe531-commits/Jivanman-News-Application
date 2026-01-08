package com.arjundabbe.jivanman.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.arjundabbe.jivanman.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * SettingsActivity:
 * - Shows user profile info (name, role, profile image)
 * - Allows theme switching (light/dark)
 * - Provides navigation to Profile, About Us, Admin/Reporter dashboards
 * - Handles user logout
 */
public class SettingsActivity extends AppCompatActivity {

    // Views
    private ImageView iconTheme, imageProfile;
    private TextView text_profile_name, text_profile_role;
    private SwitchCompat switchNotifications, switchTheme;
    private View cardAdminDashboard, cardReporterDashboard;

    // Firebase instances
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private ProgressDialog progressDialog;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply saved theme before setting content view
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        text_profile_name = findViewById(R.id.text_profile_name);
        text_profile_role = findViewById(R.id.text_profile_role);
        switchNotifications = findViewById(R.id.switch_notifications);
        switchTheme = findViewById(R.id.switch_theme);
        iconTheme = findViewById(R.id.icon_theme);
        imageProfile = findViewById(R.id.image_profile);
        cardAdminDashboard = findViewById(R.id.card_admin_dashboard);
        cardReporterDashboard = findViewById(R.id.card_reporter_dashboard);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("कृपया थोडा थांबा...");
        progressDialog.setCancelable(false);

        // Load user profile information
        loadUserProfile();

        // Setup theme switcher state
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        switchTheme.setChecked(isDark);
        iconTheme.setImageResource(isDark ? R.drawable.baseline_dark_mode_24 : R.drawable.baseline_light_mode_24);

        // Theme toggle listener
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            iconTheme.setImageResource(isChecked ? R.drawable.baseline_dark_mode_24 : R.drawable.baseline_light_mode_24);

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Update user's theme preference in Firestore
            if (auth.getCurrentUser() != null) {
                db.collection("Users")
                        .document(auth.getCurrentUser().getUid())
                        .update("preferences.darkMode", isChecked);
            }
        });

        // Navigation to other sections
        findViewById(R.id.card_profile).setOnClickListener(v ->
                startActivity(new Intent(this, MyProfileActivity.class)));

        findViewById(R.id.card_aboutus).setOnClickListener(v ->
                startActivity(new Intent(this, AboutUsActivity.class)));

        // Initialize Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build());

        // Logout
        findViewById(R.id.card_logout).setOnClickListener(v -> showLogoutDialog());
    }

    /**
     * Apply saved theme from SharedPreferences
     */
    private void applySavedTheme() {
        boolean isDark = getSharedPreferences("app_settings", MODE_PRIVATE)
                .getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /**
     * Load user profile info from Firestore
     * - Display name, role, profile image
     * - Show/hide Admin or Reporter dashboard cards based on role
     */
    private void loadUserProfile() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("Users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String name = doc.getString("name");
                    String role = doc.getString("role");
                    String profileImageUrl = doc.getString("profile_image_url");

                    // Set user name and role
                    text_profile_name.setText(name != null ? "~ " + name : "~ अर्जुन डब्बे");
                    text_profile_role.setText("भूमिका: " + convertRole(role));

                    // Load profile image using Glide
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(imageProfile);
                    } else {
                        imageProfile.setImageResource(R.drawable.round_person_24);
                    }

                    // Hide both dashboards initially
                    cardAdminDashboard.setVisibility(View.GONE);
                    cardReporterDashboard.setVisibility(View.GONE);

                    // Show dashboards based on role
                    if ("admin".equalsIgnoreCase(role)) {
                        cardAdminDashboard.setVisibility(View.VISIBLE);
                        cardAdminDashboard.setOnClickListener(v ->
                                startActivity(new Intent(this, AdminDashboardActivity.class)));
                    } else if ("reporter".equalsIgnoreCase(role)) {
                        cardReporterDashboard.setVisibility(View.VISIBLE);
                        cardReporterDashboard.setOnClickListener(v ->
                                startActivity(new Intent(this, ReportersActivity.class)));
                    }
                });
    }

    /**
     * Show logout confirmation dialog
     */
    private void showLogoutDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        // Cancel button
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        // Confirm logout button
        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Convert role string from backend to user-readable Marathi string
     */
    private String convertRole(String role) {
        if (role == null) return "वाचक";
        switch (role.toLowerCase()) {
            case "admin": return "प्रशासक";
            case "reporter": return "पत्रकार";
            default: return "वाचक";
        }
    }
}
