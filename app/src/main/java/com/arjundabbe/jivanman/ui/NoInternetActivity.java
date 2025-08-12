package com.arjundabbe.jivanman.ui;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.NetworkDetails;
import com.arjundabbe.jivanman.R;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_no_internet);

        Button btnRetry = findViewById(R.id.btnRetry);

        // 🔹 Touch animation effect
        btnRetry.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(50).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(50).start();
            }
            return false; // so clicks still work
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
