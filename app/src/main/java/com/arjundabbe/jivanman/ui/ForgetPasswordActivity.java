package com.arjundabbe.jivanman.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnRecover, btnBackToLogin;
    TextView tvRecoveredPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        btnRecover = findViewById(R.id.btnRecover);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        tvRecoveredPassword = findViewById(R.id.tvRecoveredPassword);

        // Clear error on focus
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etEmail.setError(null);
            }
        });

        // Handle password recovery
        btnRecover.setOnClickListener(view -> {
            String enteredEmail = etEmail.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                etEmail.setError("कृपया वैध ईमेल भरा");
                return;
            }

            btnRecover.setEnabled(false); // Prevent double click

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String registeredEmail = preferences.getString("email", "");
            String savedPassword = preferences.getString("password", "");

            if (enteredEmail.equals(registeredEmail)) {
                String message = "✅ तुमचा पासवर्ड: " + savedPassword;
                tvRecoveredPassword.setText(message);
                Toast.makeText(this, "पासवर्ड यशस्वीपणे मिळवला", Toast.LENGTH_SHORT).show();
            } else {
                tvRecoveredPassword.setText("");
                Toast.makeText(this, "❌ हा ईमेल नोंदणीकृत नाही", Toast.LENGTH_SHORT).show();
            }

            btnRecover.setEnabled(true); // Always re-enable
        });

        // Back to login
        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }
}
