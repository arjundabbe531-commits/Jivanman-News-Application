package com.arjundabbe.jivanman.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.arjundabbe.jivanman.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MyProfileActivity extends AppCompatActivity {

    private ImageView ivprofileImage, btnBack, btn_camera;
    private TextView tvName, tvRole, tvMobile, tvId;
    private Bitmap bitmap;
    private SharedPreferences prefs;

    // Request codes for gallery and camera actions
    private final int GALLERY_REQUEST_CODE = 999;
    private final int CAMERA_REQUEST_CODE = 998;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialize views
        ivprofileImage = findViewById(R.id.ivprofileImage);
        btnBack = findViewById(R.id.btn_back);
        btn_camera = findViewById(R.id.btn_camera);
        tvName = findViewById(R.id.profile_name);
        tvRole = findViewById(R.id.profile_role);
        tvMobile = findViewById(R.id.profile_mobile);
        tvId = findViewById(R.id.profile_id);

        // Get SharedPreferences instance to retrieve user data
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Load and set user information
        tvName.setText(prefs.getString("name", "~ नाव"));
        tvRole.setText(prefs.getString("role", "वाचक/पत्रकार"));
        tvMobile.setText("+91 " + prefs.getString("mobile", "XXXXXXXXXX"));
        tvId.setText(prefs.getString("jivanman_id", "JIVXXXXXXXXXXX"));

        // Load saved profile image from SharedPreferences
        String encodedImage = prefs.getString("profile_image", null);
        if (encodedImage != null) {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap savedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivprofileImage.setImageBitmap(savedBitmap);
        }

        // Handle back button click
        btnBack.setOnClickListener(v -> finish());

        // Open bottom sheet when camera icon is clicked
        btn_camera.setOnClickListener(v -> openImageOptionBottomSheet());
    }

    // Function to show bottom sheet dialog with image options
    private void openImageOptionBottomSheet() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_image_picker, null);

        // Get references to views inside the bottom sheet layout
        ImageView closeBtn = view.findViewById(R.id.cross_btn);
        CardView optionCamera = view.findViewById(R.id.option_camera);
        CardView optionGallery = view.findViewById(R.id.option_gallery);

        // Create and show the bottom sheet dialog
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        dialog.show();

        // Dismiss bottom sheet when close button is clicked
        closeBtn.setOnClickListener(v -> dialog.dismiss());

        // Launch camera when camera option is clicked
        optionCamera.setOnClickListener(v -> {
            dialog.dismiss();
            openCamera();
        });

        // Launch gallery when gallery option is clicked
        optionGallery.setOnClickListener(v -> {
            dialog.dismiss();
            openGallery();
        });
    }

    // Open gallery to select image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "प्रोफाईल फोटो निवडा"), GALLERY_REQUEST_CODE);
    }

    // Open camera to take a photo
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Check if there's a camera app available
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "कॅमेरा उपलब्ध नाही", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result from gallery or camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Image selected from gallery
            if (requestCode == GALLERY_REQUEST_CODE && data != null && data.getData() != null) {
                Uri imagePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    ivprofileImage.setImageBitmap(bitmap);
                    saveImageToSharedPreferences(bitmap);
                    Toast.makeText(this, "प्रोफाईल फोटो अपडेट झाला", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "फोटो लोड करण्यात त्रुटी आली", Toast.LENGTH_SHORT).show();
                }
            }
            // Image captured from camera
            else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    ivprofileImage.setImageBitmap(bitmap);
                    saveImageToSharedPreferences(bitmap);
                    Toast.makeText(this, "प्रोफाईल फोटो अपडेट झाला", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Save the selected/captured image to SharedPreferences as Base64 string
    private void saveImageToSharedPreferences(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        byte[] imageBytes = byteStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("profile_image", encodedImage);
        editor.apply();
    }
}
