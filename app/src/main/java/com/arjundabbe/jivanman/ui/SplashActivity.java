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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView appName = findViewById(R.id.appName);
        TextView subtitle = findViewById(R.id.subtitle);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);

        // Combine animations
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(scaleIn);

        // Apply animations
        appName.startAnimation(animationSet);
        subtitle.startAnimation(animationSet);

        // Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Fullscreen setup
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        // Firebase token silently
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            editor.putString("token", token).apply(); // Save token
                        }
                    }
                });

        // Redirect after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLogin = preferences.getBoolean("isLogin", false);
                Intent intent = isLogin
                        ? new Intent(SplashActivity.this, HomeActivity.class)
                        : new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
