package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.arjundabbe.jivanman.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class IdCardActivity extends AppCompatActivity {
    ImageView ivQRCode,ivIdProfile;
    TextView tvMyCodeName, tvMobile, tvEmail, tvRole, tvJivanmanId;

    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_id_card);

        ivQRCode = findViewById(R.id.ivQRCode);
        tvMyCodeName = findViewById(R.id.tvMyCodeName);
        tvMobile = findViewById(R.id.tvMobile);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvJivanmanId = findViewById(R.id.tvJivanmanId);
        ivIdProfile = findViewById(R.id.ivIdProfile);

        // ✅ Corrected: initialize preferences before using
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // ✅ Load profile image
        String encodedImage = preferences.getString("profile_image", null);
        if (encodedImage != null) {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivIdProfile.setImageBitmap(bitmap);
        }

        // Load text data
        String name = preferences.getString("name", "Not Found");
        String mobile = preferences.getString("mobile", "");
        String email = preferences.getString("email", "");
        String role = preferences.getString("role", "");
        String jivanmanId = preferences.getString("jivanman_id", "");

        // Set UI
        tvMyCodeName.setText("~ "+name);
        tvMobile.setText("फोन : +91-" + mobile);
        tvEmail.setText("ईमेल : " + email);
        tvRole.setText(role);
        tvJivanmanId.setText("जीवनमान आयडी : " + jivanmanId);

        // Generate QR code
        String qrData =
                "Name: " + name + "\n" +
                        "Mobile: " + mobile + "\n" +
                        "Email: " + email + "\n" +
                        "Role: " + role + "\n" +
                        "Jivanman ID: " + jivanmanId;

        if (!qrData.isEmpty()) {

            try {
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400);
                ivQRCode.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "QR generation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "QR data not found.", Toast.LENGTH_SHORT).show();
        }
    }

}
