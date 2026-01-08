package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.arjundabbe.jivanman.R;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    // -------------------------------
    // Views
    // -------------------------------
    private TextInputEditText editName, editEmail, editMobile;
    private ImageView btnBack, btnCamera, ivProfileImage;
    private Button btnSave;
    private TextView tvDeleteAccount;

    // -------------------------------
    // Firebase
    // -------------------------------
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Bitmap for selected image
    private Bitmap selectedBitmap = null;

    // Request codes
    private final int GALLERY_REQUEST_CODE = 999;
    private final int CAMERA_REQUEST_CODE = 998;

    // URI for camera capture
    private Uri cameraImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply saved theme before loading layout
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editMobile = findViewById(R.id.editMobile);
        btnBack = findViewById(R.id.btn_back);
        btnCamera = findViewById(R.id.btnCamera);
        ivProfileImage = findViewById(R.id.ivprofileImage);
        btnSave = findViewById(R.id.btnSave);
        tvDeleteAccount = findViewById(R.id.tvDeleteAccount);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Load current user data
        loadUserProfile();

        // -------------------------------
        // Button listeners
        // -------------------------------
        btnBack.setOnClickListener(v -> finish());
        btnCamera.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveProfile());
        tvDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    /** Apply saved dark/light theme from SharedPreferences */
    private void applySavedTheme() {
        SharedPreferences themePrefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean isDarkMode = themePrefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /** Load user profile from Firestore */
    private void loadUserProfile() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        db.collection("Users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // Populate fields
                        editName.setText(doc.getString("name"));
                        editEmail.setText(doc.getString("email"));
                        editMobile.setText(doc.getString("mobileno"));

                        String imageUrl = doc.getString("profile_image_url");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Load profile image using Picasso
                            Picasso.get()
                                    .load(imageUrl.replace("http://", "https://"))
                                    .placeholder(R.drawable.round_person_24)
                                    .error(R.drawable.round_person_24)
                                    .into(ivProfileImage);
                            Log.d(TAG, "Loaded profile image: " + imageUrl);
                        } else {
                            ivProfileImage.setImageResource(R.drawable.round_person_24);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Profile load failed", e));
    }

    /** Open bottom sheet to select image from gallery, camera, or delete */
    private void openImagePicker() {
        View view = getLayoutInflater().inflate(R.layout.bottom_image_picker, null);

        ImageView closeBtn = view.findViewById(R.id.cancel_btn);
        CardView optionCamera = view.findViewById(R.id.option_camera);
        CardView optionGallery = view.findViewById(R.id.option_gallery);
        ImageView deleteBtn = view.findViewById(R.id.delete_btn);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        closeBtn.setOnClickListener(v -> dialog.dismiss());
        optionCamera.setOnClickListener(v -> {
            dialog.dismiss();
            openCamera();
        });
        optionGallery.setOnClickListener(v -> {
            dialog.dismiss();
            openGallery();
        });
        deleteBtn.setOnClickListener(v -> {
            dialog.dismiss();
            deleteProfileImage();
        });
    }

    /** Open gallery for image selection */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "प्रतिमा निवडा"), GALLERY_REQUEST_CODE);
    }

    /** Open camera to capture a new image */
    private void openCamera() {
        try {
            File imageFile = new File(getExternalFilesDir("Pictures"),
                    "profile_" + System.currentTimeMillis() + ".jpg");
            cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "कॅमेरा उघडण्यात अयशस्वी: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /** Handle result from gallery or camera */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == GALLERY_REQUEST_CODE && data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } else if (requestCode == CAMERA_REQUEST_CODE && cameraImageUri != null) {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), cameraImageUri);
                }

                if (selectedBitmap != null) {
                    ivProfileImage.setImageBitmap(selectedBitmap);
                    Log.d(TAG, "Profile image selected successfully.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "प्रतिमा निवड अयशस्वी", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Save profile updates to Firestore and upload image if changed */
    private void saveProfile() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "सर्व फील्ड्स भरा", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedBitmap != null) {
            uploadImageToCloudinary(selectedBitmap, uid, name, email, mobile);
        } else {
            updateUserInFirestore(uid, name, email, mobile, null);
        }
    }

    /** Delete profile image */
    private void deleteProfileImage() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        // Reset UI
        ivProfileImage.setImageResource(R.drawable.round_person_24);
        selectedBitmap = null;

        // Remove URL from Firestore
        db.collection("Users").document(uid)
                .update("profile_image_url", null)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "प्रोफाइल फोटो हटवला", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting profile image", e));
    }

    /** Upload image to Cloudinary */
    private void uploadImageToCloudinary(Bitmap bitmap, String uid, String name, String email, String mobile) {
        Toast.makeText(this, "प्रतिमा अपलोड होत आहे...", Toast.LENGTH_SHORT).show();

        try {
            File tempFile = new File(getCacheDir(), "profile_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            MediaManager.get().upload(tempFile.getAbsolutePath())
                    .option("upload_preset", "unsigned_jivanman")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) { Log.d(TAG, "Upload started: " + requestId); }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) { }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String imageUrl = resultData.get("url").toString().replace("http://", "https://");
                            updateUserInFirestore(uid, name, email, mobile, imageUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Upload failed: " + error.getDescription());
                            Toast.makeText(EditProfileActivity.this, "प्रतिमा अपलोड अयशस्वी: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) { }
                    }).dispatch();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "प्रतिमा अपलोड अयशस्वी: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /** Update Firestore document */
    private void updateUserInFirestore(String uid, String name, String email, String mobile, @Nullable String profileImageUrl) {
        DocumentReference userRef = db.collection("Users").document(uid);

        Map<String, Object> updateMap = profileImageUrl != null
                ? Map.of("name", name, "email", email, "mobileno", mobile, "profile_image_url", profileImageUrl)
                : Map.of("name", name, "email", email, "mobileno", mobile);

        userRef.update(updateMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "प्रोफाइल यशस्वीरित्या अपडेट झाली!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error updating profile: ", e));
    }

    /** Show delete account confirmation dialog */
    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("खाते हटवा")
                .setMessage("तुम्हाला खातं कायमचं हटवायचं आहे का?")
                .setPositiveButton("हो", (dialog, which) -> deleteAccount())
                .setNegativeButton("रद्द करा", null)
                .show();
    }

    /** Delete user account from Firebase */
    private void deleteAccount() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("Users").document(uid).delete()
                .addOnSuccessListener(aVoid -> {
                    auth.getCurrentUser().delete();
                    Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Account delete failed: ", e));
    }
}
