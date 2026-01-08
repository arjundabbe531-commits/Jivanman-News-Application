package com.arjundabbe.jivanman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.arjundabbe.jivanman.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    // ---------------- UI COMPONENTS ----------------
    private EditText etUsername, etMobile, etEmail, etPassword1, etPassword2;
    private Button btnRegister;
    private TextView tvLoginNow;

    // ---------------- FIREBASE ----------------
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply saved dark/light theme
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Bind UI components
        etUsername = findViewById(R.id.etRegistrationUsername);
        etMobile = findViewById(R.id.etRegistrationMobileNo);
        etEmail = findViewById(R.id.etRegistrationEmail);
        etPassword1 = findViewById(R.id.etRegistrationPassword1);
        etPassword2 = findViewById(R.id.etRegistrationPassword2);

        btnRegister = findViewById(R.id.btnRegistrationRegistration);
        tvLoginNow = findViewById(R.id.tvRegistrationLoginNow);

        // Navigate to LoginActivity if user clicks "Login Now"
        tvLoginNow.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        // Handle registration button click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    // ---------------- USER REGISTRATION ----------------
    private void registerUser() {

        // Get input values
        String name = etUsername.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword1.getText().toString().trim();
        String confirm = etPassword2.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            etUsername.setError("नाव आवश्यक आहे");
            return;
        }
        if (mobile.length() != 10) {
            etMobile.setError("वैध मोबाईल क्रमांक");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("वैध ईमेल");
            return;
        }
        if (pass.length() < 6) {
            etPassword1.setError("किमान 6 अक्षरे");
            return;
        }
        if (!pass.equals(confirm)) {
            etPassword2.setError("पासवर्ड जुळत नाही");
            return;
        }

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();

                    // Prepare user data for Firestore
                    Map<String, Object> user = new HashMap<>();
                    user.put("uid", uid);
                    user.put("name", name);
                    user.put("email", email);
                    user.put("mobile", mobile);
                    user.put("role", "reader");       // default role
                    user.put("verified", false);      // default verification
                    user.put("joinedAt", FieldValue.serverTimestamp());

                    // Save user in Firestore
                    firestore.collection("Users").document(uid)
                            .set(user)
                            .addOnSuccessListener(v -> {
                                Toast.makeText(this, "नोंदणी यशस्वी 🎉", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "नोंदणी अयशस्वी: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    // ---------------- THEME HELPER ----------------
    private void applySavedTheme() {
        boolean isDark = getSharedPreferences("app_settings", MODE_PRIVATE)
                .getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
