package com.arjundabbe.jivanman.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.ui.PortraitCaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ScanCodeFragment extends Fragment {

    AppCompatButton btnTaptoScan;
    TextView tvData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_code, container, false);

        btnTaptoScan = view.findViewById(R.id.btnQRCodeScanner);
        tvData = view.findViewById(R.id.tvQRCodeScannerData);

        btnTaptoScan.setOnClickListener(v -> {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ScanCodeFragment.this);
            integrator.setOrientationLocked(true);
            integrator.setCaptureActivity(PortraitCaptureActivity.class);
            integrator.setPrompt("Scan QR Code");
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            tvData.setText(result.getContents());
        }
    }
}

