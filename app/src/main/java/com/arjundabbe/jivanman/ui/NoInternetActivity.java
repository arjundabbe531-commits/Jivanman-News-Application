package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.arjundabbe.jivanman.NetworkDetails;
import com.arjundabbe.jivanman.R;

public class NoInternetActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before loading the layout
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_no_internet);

        // UI elements
        Button btnRetry = findViewById(R.id.btnRetry);
        ImageView imgNoInternet = findViewById(R.id.imgNoInternet);
        TextView txtHeadline = findViewById(R.id.txtHeadline);
        TextView txtSubtext = findViewById(R.id.txtSubtext);

        // Check if the device is in airplane mode
        boolean airplaneModeOn = getIntent().getBooleanExtra("airplaneMode", false);
        if (airplaneModeOn) {
            // Airplane mode visuals
            imgNoInternet.setImageResource(R.drawable.baseline_airplanemode_active_24);
            txtHeadline.setText("✈️ एअरप्लेन मोड चालू आहे");
            txtSubtext.setText("कृपया एअरप्लेन मोड बंद करा किंवा इंटरनेट कनेक्शन सुरू करा");
        } else {
            // No internet visuals
            imgNoInternet.setImageResource(R.drawable.baseline_wifi_off_24);
            txtHeadline.setText("\uD83D\uDCE1 इंटरनेट कनेक्शन नाही");
            txtSubtext.setText("कृपया आपले कनेक्शन तपासा");
        }

        // Touch animation for button press
        btnRetry.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(50).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(50).start();
            }
            return false;
        });

        // Retry button click: closes activity if internet is back
        btnRetry.setOnClickListener(v -> {
            if (NetworkDetails.isConnectedToInternet(this)) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Automatically close activity if internet is restored
        if (NetworkDetails.isConnectedToInternet(this)) {
            finish();
        }
    }

    /**
     * Apply saved theme (dark/light) from SharedPreferences
     */
    private void applySavedTheme() {
        SharedPreferences themePrefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean isDarkMode = themePrefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
