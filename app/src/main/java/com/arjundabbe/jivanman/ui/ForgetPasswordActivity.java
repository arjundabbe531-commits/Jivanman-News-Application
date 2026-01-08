package com.arjundabbe.jivanman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgetPasswordOTP";

    // ---------------- UI ----------------
    private EditText etMobile;
    private Button btnSendOtp;

    // ---------------- Firebase ----------------
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Bind UI components
        etMobile = findViewById(R.id.etMobile);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        // Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        // Handle OTP request button click
        btnSendOtp.setOnClickListener(v -> sendOtp());
    }

    // ---------------- SEND OTP ----------------
    private void sendOtp() {
        String mobile = etMobile.getText().toString().trim();

        // Validate mobile number (10 digits)
        if (!mobile.matches("[0-9]{10}")) {
            etMobile.setError("वैध 10 अंकी मोबाइल नंबर भरा");
            return;
        }

        btnSendOtp.setEnabled(false);

        // Configure phone authentication
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + mobile)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();

        Log.d(TAG, "Sending OTP to +91" + mobile);
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // ---------------- CALLBACKS ----------------
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                // Auto verification (instant verification)
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    Log.d(TAG, "Auto verification completed");
                    signInWithCredential(credential);
                }

                // Verification failed
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    btnSendOtp.setEnabled(true);
                    Log.e(TAG, "OTP Failed", e);
                    Toast.makeText(
                            ForgetPasswordActivity.this,
                            "OTP Failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }

                // OTP sent successfully
                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    Log.d(TAG, "OTP Sent successfully");

                    // Open OTP verification activity
                    Intent intent = new Intent(ForgetPasswordActivity.this, OtpVerifyActivity.class);
                    intent.putExtra("verificationId", verificationId);
                    startActivity(intent);
                }
            };

    // ---------------- SIGN IN WITH OTP ----------------
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Log.d(TAG, "OTP verified, UID=" + uid);

                    // Open ResetPasswordActivity
                    Intent intent = new Intent(this, ResetPasswordActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Credential sign-in failed", e);
                    Toast.makeText(this, "OTP चुकीचा आहे", Toast.LENGTH_SHORT).show();
                });
    }
}
