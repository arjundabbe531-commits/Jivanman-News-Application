package com.arjundabbe.jivanman.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.arjundabbe.jivanman.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {

    private static final String TAG = "MyProfileActivity";

    // -------------------------------
    // Views
    // -------------------------------
    private ImageView ivProfileImage, btnBack;
    private TextView tvProfileName, tvProfileEmail, tvProfileRole, tvProfileMobile, tvProfileId, editProfile;

    // -------------------------------
    // Firebase
    // -------------------------------
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply saved theme before loading layout
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialize views
        ivProfileImage = findViewById(R.id.ivprofileImage);
        btnBack = findViewById(R.id.btn_back);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        tvProfileMobile = findViewById(R.id.tvProfileMobile);
        tvProfileId = findViewById(R.id.tvProfileId);
        editProfile = findViewById(R.id.editProfile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Back button closes activity
        btnBack.setOnClickListener(v -> finish());

        // Edit profile navigates to EditProfileActivity
        editProfile.setOnClickListener(v -> startActivity(
                new android.content.Intent(MyProfileActivity.this, EditProfileActivity.class)
        ));

        // Load user profile data from Firestore
        loadUserProfile();

        // Load theme preference from Firestore to sync across devices
        loadUserTheme();
    }

    /** Apply saved dark/light theme from SharedPreferences */
    private void applySavedTheme() {
        SharedPreferences themePrefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean isDarkMode = themePrefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /** Load user profile information from Firestore */
    private void loadUserProfile() {
        if (auth.getCurrentUser() == null) {
            Log.w(TAG, "No authenticated user found");
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.w(TAG, "User document does not exist for UID: " + uid);
                        return;
                    }

                    // Fetch fields
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String role = documentSnapshot.getString("role");
                    String mobile = documentSnapshot.getString("mobileno");
                    String jivanmanId = documentSnapshot.getString("jivanman_id");
                    String profileImageUrl = documentSnapshot.getString("profile_image_url");

                    // Update UI with defaults for null/empty values
                    tvProfileName.setText(name != null && !name.isEmpty() ? name : "~ नाव उपलब्ध नाही");
                    tvProfileEmail.setText(email != null && !email.isEmpty() ? email : "~ ईमेल उपलब्ध नाही");
                    tvProfileRole.setText(role != null && !role.isEmpty() ? convertRole(role) : "वाचक/पत्रकार");
                    tvProfileMobile.setText(mobile != null && !mobile.isEmpty() ? "+91-" + mobile : "+91-XXXXXXXXXX");
                    tvProfileId.setText(jivanmanId != null && !jivanmanId.isEmpty() ? jivanmanId : "JIVXXXXXXXX");

                    // Load profile image with Picasso, fallback to default
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get()
                                .load(profileImageUrl.replace("http://", "https://"))
                                .placeholder(R.drawable.round_person_24)
                                .error(R.drawable.round_person_24)
                                .into(ivProfileImage);
                    } else {
                        ivProfileImage.setImageResource(R.drawable.round_person_24);
                    }

                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user profile", e));
    }

    /** Load dark/light theme from Firestore */
    private void loadUserTheme() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Users").document(uid);

        userRef.get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("preferences")) {
                Map<String, Object> prefs = (Map<String, Object>) doc.get("preferences");
                boolean darkMode = prefs.containsKey("darkMode") && (Boolean) prefs.get("darkMode");

                AppCompatDelegate.setDefaultNightMode(
                        darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                );
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to load theme preference", e));
    }

    /** Converts Firebase role string to Marathi label */
    private String convertRole(String role) {
        switch (role.toLowerCase()) {
            case "admin": return "प्रशासक";
            case "reporter": return "पत्रकार";
            case "reader": return "वाचक";
            default: return role;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile after editing
        loadUserProfile();
    }
}
