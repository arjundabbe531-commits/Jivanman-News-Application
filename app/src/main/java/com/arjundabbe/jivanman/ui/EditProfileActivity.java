package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.database.DBHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail, editMobile;
    private ImageView btnBack, btn_camera, ivprofileImage;
    private Button btnSave;
    private TextView tvDeleteAccount;
    private Bitmap bitmap;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final int GALLERY_REQUEST_CODE = 999;
    private final int CAMERA_REQUEST_CODE = 998;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editMobile = findViewById(R.id.edit_mobile);
        btnBack = findViewById(R.id.btn_back);
        btn_camera = findViewById(R.id.btn_camera);
        btnSave = findViewById(R.id.btnSave);
        tvDeleteAccount = findViewById(R.id.tvDeleteAccount);
        ivprofileImage = findViewById(R.id.ivprofileImage);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        // Load existing profile data

        editName.setText(sharedPreferences.getString("name", ""));
        editEmail.setText(sharedPreferences.getString("email", ""));
        editMobile.setText(sharedPreferences.getString("mobile", ""));

        String encodedImage = sharedPreferences.getString("profile_image", null);
        if (encodedImage != null) {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap savedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivprofileImage.setImageBitmap(savedBitmap);
        }

        btnBack.setOnClickListener(v -> finish());
        btn_camera.setOnClickListener(v -> openImageOptionBottomSheet());

        btnSave.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newMobile = editMobile.getText().toString().trim();
            String username = sharedPreferences.getString("username", "");

            Log.d("EditProfile", "Username from SharedPrefs: " + username);
            Log.d("EditProfile", "Name: " + newName);
            Log.d("EditProfile", "Email: " + newEmail);
            Log.d("EditProfile", "Mobile: " + newMobile);

            if (newName.isEmpty() || newEmail.isEmpty() || newMobile.isEmpty()) {
                Toast.makeText(this, "सर्व फील्ड्स भरावीत", Toast.LENGTH_SHORT).show();
            } else if (username.isEmpty()) {
                Toast.makeText(this, "वापरकर्ता नाव सापडले नाही, कृपया पुन्हा लॉगिन करा", Toast.LENGTH_LONG).show();
            } else {
                DBHelper dbHelper = new DBHelper(EditProfileActivity.this);
                boolean updated = dbHelper.updateUser(username, newName, newEmail, newMobile);

                if (updated) {
                    editor.putString("name", newName);
                    editor.putString("email", newEmail);
                    editor.putString("mobile", newMobile);
                    editor.apply();

                    Toast.makeText(this, "माहिती यशस्वीरित्या अपडेट झाली", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "अपडेट अयशस्वी", Toast.LENGTH_SHORT).show();
                }
            }

        });



        tvDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void openImageOptionBottomSheet() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_image_picker, null);

        ImageView closeBtn = view.findViewById(R.id.cross_btn);
        CardView optionCamera = view.findViewById(R.id.option_camera);
        CardView optionGallery = view.findViewById(R.id.option_gallery);

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
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "प्रोफाईल फोटो निवडा"), GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "कॅमेरा उपलब्ध नाही", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
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
            } else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    ivprofileImage.setImageBitmap(bitmap);
                    saveImageToSharedPreferences(bitmap);
                    Toast.makeText(this, "प्रोफाईल फोटो अपडेट झाला", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveImageToSharedPreferences(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        byte[] imageBytes = byteStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        editor.putString("profile_image", encodedImage);
        editor.apply();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("खाते हटवा")
                .setMessage("तुम्हाला खातं कायमचं हटवायचं आहे का?")
                .setPositiveButton("हो", (dialog, which) -> {
                    String username = sharedPreferences.getString("username", null);
                    if (username != null) {
                        DBHelper dbHelper = new DBHelper(EditProfileActivity.this);
                        boolean deleted = dbHelper.deleteUser(username);

                        if (deleted) {
                            editor.clear();
                            editor.apply();
                            Toast.makeText(this, "खाते यशस्वीरित्या हटवले गेले", Toast.LENGTH_SHORT).show();

                            // Go to login or splash screen
                            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "खाते हटवता आले नाही", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("रद्द करा", null)
                .show();
    }
}
