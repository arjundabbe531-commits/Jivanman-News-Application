package com.arjundabbe.jivanman.ui.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.arjundabbe.jivanman.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MyCodeFragment extends Fragment {

    ImageView ivQRCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_code, container, false);

        ivQRCode = view.findViewById(R.id.ivQRCode);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String name = preferences.getString("name", "Not Found");
        String mobile = preferences.getString("mobile", "");
        String email = preferences.getString("email", "");
        String role = preferences.getString("role", "");
        String jivanmanId = preferences.getString("jivanman_id", "");

        // QR data in Marathi
        String qrData =
                        name + "\n" +
                        mobile + "\n" +
                        email + "\n" +
                        role + "\n" +
                        jivanmanId;

        if (!qrData.isEmpty()) {
            try {
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400);
                ivQRCode.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(getContext(), "QR generation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "QR data not found.", Toast.LENGTH_SHORT).show();
        }





        return view;
    }
}

