package com.arjundabbe.jivanman.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.R;

/**
 * SplashActivity
 * ---------------
 * This is the launcher activity of the Jivanman application.
 * It displays the splash screen with animations and decides
 * whether to navigate the user to Home or Login screen
 * based on login status stored in SharedPreferences.
 */
public class SplashActivity extends AppCompatActivity {

    // SharedPreferences to store and retrieve login state
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set splash screen layout
        setContentView(R.layout.activity_splash);

        // Initialize splash screen TextViews
        TextView appName = findViewById(R.id.appName);
        TextView subtitle = findViewById(R.id.subtitle);

        // Load animations from anim resource folder
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);

        // Combine multiple animations into one AnimationSet
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(scaleIn);

        // Start animation on app name and subtitle
        appName.startAnimation(animationSet);
        subtitle.startAnimation(animationSet);

        // Initialize default shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Make the splash screen fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        // Delay execution for 2.5 seconds (2500 milliseconds)
        new Handler().postDelayed(() -> {

            // Check if user is already logged in
            boolean isLogin = preferences.getBoolean("isLogin", false);

            // Navigate user based on login status
            if (isLogin) {
                // User already logged in → go to Home screen
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                // User not logged in → go to Login screen
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            // Close splash activity so user cannot return to it
            finish();

        }, 2500);
    }
}
