package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.NetworkDetails;
import com.arjundabbe.jivanman.R;

public class NoInternetActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_no_internet);

        Button btnRetry = findViewById(R.id.btnRetry);
        ImageView imgNoInternet = findViewById(R.id.imgNoInternet);
        TextView txtHeadline = findViewById(R.id.txtHeadline);
        TextView txtSubtext = findViewById(R.id.txtSubtext);

        boolean airplaneModeOn = getIntent().getBooleanExtra("airplaneMode", false);
        if (airplaneModeOn) {
            // Change image to airplane mode icon
            imgNoInternet.setImageResource(R.drawable.baseline_airplanemode_active_24);

            // Change headline text
            txtHeadline.setText("✈️ एअरप्लेन मोड चालू आहे");

            // Change subtext
            txtSubtext.setText("कृपया एअरप्लेन मोड बंद करा किंवा इंटरनेट कनेक्शन सुरू करा");
        } else {
            imgNoInternet.setImageResource(R.drawable.baseline_wifi_off_24);
            txtHeadline.setText("\uD83D\uDCE1 इंटरनेट कनेक्शन नाही");
            txtSubtext.setText("कृपया आपले कनेक्शन तपासा");

        }

        // 🔹 Touch animation effect
        btnRetry.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(50).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(50).start();
            }
            return false;
        });

        // 🔹 Retry click action
        btnRetry.setOnClickListener(v -> {
            if (NetworkDetails.isConnectedToInternet(this)) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkDetails.isConnectedToInternet(this)) {
            finish();
        }
    }
}
