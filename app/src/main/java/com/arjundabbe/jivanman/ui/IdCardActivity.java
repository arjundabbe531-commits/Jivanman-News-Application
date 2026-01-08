package com.arjundabbe.jivanman.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.arjundabbe.jivanman.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class IdCardActivity extends AppCompatActivity {

    ImageView ivQRCode, ivIdProfile;
    TextView tvMyCodeName, tvMobile, tvEmail, tvRole, tvJivanmanId;
    LinearLayout idCardLayout;

    ImageView btnDownload, btnShare;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final int STORAGE_PERMISSION_CODE = 2001;
    private static final String TAG = "IdCardActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        applySavedTheme();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_id_card);

        // ID Card Views
        ivQRCode = findViewById(R.id.ivQRCode);
        tvMyCodeName = findViewById(R.id.tvMyCodeName);
        tvMobile = findViewById(R.id.tvMobile);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvJivanmanId = findViewById(R.id.tvJivanmanId);
        ivIdProfile = findViewById(R.id.ivIdProfile);
        idCardLayout = findViewById(R.id.idCardLayout);

        // Buttons
        btnDownload = findViewById(R.id.btnDownload);
        btnShare = findViewById(R.id.btnShare);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadProfileFromFirestore();  // Existing logic unchanged

        // Download & Share functionality
        btnDownload.setOnClickListener(v -> saveIdCardImage());
        btnShare.setOnClickListener(v -> shareIdCardImage());
    }

    // ----------------------------
    // DOWNLOAD & SHARE FUNCTIONS
    // ----------------------------
    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // ----------------------------
// CIRCULAR BITMAP FUNCTION
// ----------------------------
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);

        return output;
    }

    // ----------------------------
// GET BITMAP FROM VIEW WITH CIRCULAR PROFILE
// ----------------------------
    private Bitmap getBitmapFromViewWithCircularProfile(View view, ImageView profileImageView) {
        // 1. Get circular profile bitmap
        Bitmap profileBitmap = null;
        if (profileImageView.getDrawable() instanceof BitmapDrawable) {
            profileBitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
        }

        if (profileBitmap != null) {
            profileBitmap = getCircularBitmap(profileBitmap);
        }

        // 2. Temporarily hide the profile ImageView so the rectangle doesn't get drawn
        profileImageView.setVisibility(View.INVISIBLE);

        // 3. Draw the rest of the view
        Bitmap fullBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fullBitmap);
        view.draw(canvas);

        // 4. Overlay the circular profile bitmap at the correct position
        if (profileBitmap != null) {
            int left = profileImageView.getLeft();
            int top = profileImageView.getTop();
            int width = profileImageView.getWidth();
            int height = profileImageView.getHeight();

            Bitmap resizedCircular = Bitmap.createScaledBitmap(profileBitmap, width, height, true);
            canvas.drawBitmap(resizedCircular, left, top, null);
        }

        // 5. Make the ImageView visible again
        profileImageView.setVisibility(View.VISIBLE);

        return fullBitmap;
    }

    // ----------------------------
// SAVE ID CARD TO GALLERY
// ----------------------------
    private void saveIdCardImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE
                );
                return;
            }
        }

        Bitmap bitmap = getBitmapFromViewWithCircularProfile(idCardLayout, ivIdProfile);

        // Save in Pictures/Jivanman folder
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Jivanman");
        if (!directory.exists()) directory.mkdirs();

        File file = new File(directory, "Jivanman_ID_" + System.currentTimeMillis() + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(this, "ID कार्ड सेव्ह केले! (Gallery मध्ये)", Toast.LENGTH_LONG).show();

            // Optional: Notify MediaScanner to show image in gallery
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);

        } catch (IOException e) {
            Toast.makeText(this, "सेव्ह करताना त्रुटी आली!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error saving ID card: ", e);
        }
    }

    // ----------------------------
// SHARE ID CARD IMAGE
// ----------------------------
    private void shareIdCardImage() {
        Bitmap bitmap = getBitmapFromViewWithCircularProfile(idCardLayout, ivIdProfile);

        File cachePath = new File(getCacheDir(), "images");
        cachePath.mkdirs();
        File file = new File(cachePath, "id_card_share.jpg");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            Toast.makeText(this, "शेअर करताना त्रुटी आली!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "ID कार्ड शेअर करा"));
    }


    // ----------------------------
    // EXISTING LOGIC UNCHANGED
    // ----------------------------

    private void loadProfileFromFirestore() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "वापरकर्ता लॉगिन नाही!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(this, "प्रोफाइल मिळाले नाही!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String name = doc.getString("name");
                    String mobile = doc.getString("mobileno");
                    String email = doc.getString("email");
                    String role = doc.getString("role");
                    String jivanmanId = doc.getString("jivanman_id");
                    String profileImageUrl = doc.getString("profile_image_url");

                    tvMyCodeName.setText("~ " + (name != null ? name : ""));
                    tvMobile.setText("फोन : +91-" + (mobile != null ? mobile : "XXXXXXXXXX"));
                    tvEmail.setText("ईमेल : " + (email != null ? email : ""));
                    tvRole.setText(convertRole(role));
                    tvJivanmanId.setText("जिवनमान आयडी : " + (jivanmanId != null ? jivanmanId : ""));

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get()
                                .load(profileImageUrl.replace("http://","https://"))
                                .placeholder(R.drawable.round_person_24)
                                .error(R.drawable.round_person_24)
                                .into(ivIdProfile);
                    } else {
                        ivIdProfile.setImageResource(R.drawable.round_person_24);
                    }

                    generateQR(name, mobile, email, role, jivanmanId);

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Profile fetch error", e);
                    Toast.makeText(this, "प्रोफाइल मिळवताना त्रुटी आली!", Toast.LENGTH_SHORT).show();
                });
    }

    private void generateQR(String name, String mobile, String email, String role, String jivanmanId) {
        String qrData =
                "Name: " + name + "\n" +
                        "Mobile: " + mobile + "\n" +
                        "Email: " + email + "\n" +
                        "Role: " + role + "\n" +
                        "Jivanman ID: " + jivanmanId;

        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400);
            ivQRCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "QR कोड तयार करण्यात अडचण आली!", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertRole(String role) {
        if (role == null) return "वाचक";

        switch (role.toLowerCase()) {
            case "admin": return "प्रशासक";
            case "reporter": return "पत्रकार";
            case "reader": return "वाचक";
            default: return role;
        }
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
