package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.database.DBHelper;

public class MyProfileActivity extends AppCompatActivity {

    // Views
    private ImageView ivprofileImage, btnBack;
    private TextView tvProfileName, tvProfileEmail, tvProfileRole, tvProfileMobile, tvProfileId, editProfile;

    @SuppressLint({"Range", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialize views
        ivprofileImage = findViewById(R.id.ivprofileImage);
        btnBack = findViewById(R.id.btn_back);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        tvProfileMobile = findViewById(R.id.tvProfileMobile);
        tvProfileId = findViewById(R.id.tvProfileId);
        editProfile = findViewById(R.id.editProfile);

        // Back button click - close activity
        btnBack.setOnClickListener(v -> finish());

        // Edit profile button click - open EditProfileActivity
        editProfile.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MyProfileActivity.this, EditProfileActivity.class));
        });

        // Load user data from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String email = prefs.getString("email", null);

        if (email != null) {
            DBHelper dbHelper = new DBHelper(this);
            Cursor cursor = dbHelper.getUserByEmail(email);

            if (cursor != null) {
                Log.d("MyProfile", "Cursor count: " + cursor.getCount());
            }

            // Set user details in views
            if (cursor != null && cursor.moveToFirst()) {
                tvProfileName.setText(cursor.getString(cursor.getColumnIndex("name")));
                tvProfileEmail.setText(cursor.getString(cursor.getColumnIndex("email")));
                tvProfileRole.setText(cursor.getString(cursor.getColumnIndex("role")));
                tvProfileMobile.setText("+91-" + cursor.getString(cursor.getColumnIndex("mobileno")));
                tvProfileId.setText("JIV" + cursor.getString(cursor.getColumnIndex("id"))); // optional
            }

            if (cursor != null) cursor.close();
        }

        // Load profile image from SharedPreferences (Base64 encoded)
        String encodedImage = prefs.getString("profile_image", null);
        if (encodedImage != null) {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap savedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivprofileImage.setImageBitmap(savedBitmap);
        }

        // Profile image appearance
        ivprofileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivprofileImage.setClipToOutline(true); // rounded/circular outline in XML
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();  // Reload fresh user data every time activity resumes
    }

    @SuppressLint("Range")
    private void loadUserData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String email = prefs.getString("email", null);

        if (email != null) {
            DBHelper dbHelper = new DBHelper(this);
            Cursor cursor = dbHelper.getUserByEmail(email);

            // Update views with latest user data from DB
            if (cursor != null && cursor.moveToFirst()) {
                tvProfileName.setText(cursor.getString(cursor.getColumnIndex("name")));
                tvProfileEmail.setText(cursor.getString(cursor.getColumnIndex("email")));
                tvProfileRole.setText(cursor.getString(cursor.getColumnIndex("role")));
                tvProfileMobile.setText("+91-" + cursor.getString(cursor.getColumnIndex("mobileno")));
                tvProfileId.setText("JIV" + cursor.getString(cursor.getColumnIndex("id")));
            }

            if (cursor != null) cursor.close();
        }

        // Reload profile image from SharedPreferences
        String encodedImage = prefs.getString("profile_image", null);
        if (encodedImage != null) {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap savedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivprofileImage.setImageBitmap(savedBitmap);
        }
    }
}
