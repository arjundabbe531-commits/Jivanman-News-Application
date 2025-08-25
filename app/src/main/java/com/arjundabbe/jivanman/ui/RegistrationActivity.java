package com.arjundabbe.jivanman.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.database.DBHelper;

public class RegistrationActivity extends AppCompatActivity {

    ImageView ivToggleRegistrationPassword1, ivToggleRegistrationPassword2;
    EditText etRegistrationUsername, etRegistrationMobileNo, etRegistrationEmail, etRegistrationPassword1, etRegistrationPassword2;
    Button btnRegistrationRegistration;
    TextView tvRegistrationLoginNow;
    RadioGroup rgRoles;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("नोंदणी");
        DBHelper dbHelper = new DBHelper(this);

        if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {
                    Manifest.permission.SEND_SMS},111);
        }

        etRegistrationUsername = findViewById(R.id.etRegistrationUsername);
        etRegistrationEmail = findViewById(R.id.etRegistrationEmail);
        etRegistrationPassword1 = findViewById(R.id.etRegistrationPassword1);
        etRegistrationPassword2 = findViewById(R.id.etRegistrationPassword2);
        tvRegistrationLoginNow = findViewById(R.id.tvRegistrationLoginNow);
        btnRegistrationRegistration = findViewById(R.id.btnRegistrationRegistration);
        etRegistrationMobileNo = findViewById(R.id.etRegistrationMobileNo);
        ivToggleRegistrationPassword1 = findViewById(R.id.ivToggleRegistrationPassword1);
        ivToggleRegistrationPassword2 = findViewById(R.id.ivToggleRegistrationPassword2);
        rgRoles = findViewById(R.id.rgRoles);

        tvRegistrationLoginNow.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        ivToggleRegistrationPassword1.setOnClickListener(v -> {
            if (etRegistrationPassword1.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etRegistrationPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleRegistrationPassword1.setImageResource(R.drawable.ic_eye_open);
            } else {
                etRegistrationPassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleRegistrationPassword1.setImageResource(R.drawable.ic_closed_eyes);
            }
            etRegistrationPassword1.setSelection(etRegistrationPassword1.length());
        });

        ivToggleRegistrationPassword2.setOnClickListener(v -> {
            if (etRegistrationPassword2.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etRegistrationPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleRegistrationPassword2.setImageResource(R.drawable.ic_eye_open);
            } else {
                etRegistrationPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleRegistrationPassword2.setImageResource(R.drawable.ic_closed_eyes);
            }
            etRegistrationPassword2.setSelection(etRegistrationPassword2.length());
        });

        btnRegistrationRegistration.setOnClickListener(v -> {
            String email = etRegistrationEmail.getText().toString().trim();
            String password = etRegistrationPassword1.getText().toString().trim();
            String username = etRegistrationUsername.getText().toString().trim();
            String mobile = etRegistrationMobileNo.getText().toString().trim();
            boolean isValid = true;

            // Validate mobile number
            if (mobile.isEmpty()) {
                etRegistrationMobileNo.setError("कृपया मोबाईल क्रमांक भरा");
                isValid = false;
            } else if (mobile.length() != 10 || !mobile.matches("[0-9]+")) {
                etRegistrationMobileNo.setError("कृपया वैध १० अंकी मोबाईल क्रमांक भरा");
                isValid = false;
            }

            // Validate username
            if (username.isEmpty() || username.length() < 3) {
                etRegistrationUsername.setError("कृपया पूर्ण नाव भरा");
                isValid = false;
            }

            // Validate email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etRegistrationEmail.setError("कृपया वैध ईमेल भरा");
                etRegistrationEmail.setVisibility(View.VISIBLE);
                isValid = false;
            }

            // Validate password
            String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";


            if (password.isEmpty()) {
                etRegistrationPassword1.setError("कृपया पासवर्ड भरा");
                etRegistrationPassword1.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (password.length() < 8) {
                etRegistrationPassword1.setError("पासवर्ड किमान ८ अक्षरे असावा");
                etRegistrationPassword1.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (!password.matches(passwordPattern)) {
                etRegistrationPassword1.setError("पासवर्डमध्ये १ Capital, १ small व १ विशेष चिन्ह असणे आवश्यक आहे");
                isValid = false;
            }

            // Confirm password match
            String confirmPassword = etRegistrationPassword2.getText().toString().trim();
            if (!password.equals(confirmPassword)) {
                etRegistrationPassword2.setError("पासवर्ड जुळत नाहीत");
                isValid = false;
            }

            // Get selected role
            int selectedRoleId = rgRoles.getCheckedRadioButtonId();
            String role = "";
            if (selectedRoleId == -1) {
                Toast.makeText(RegistrationActivity.this, "कृपया भूमिका निवडा (वाचक/पत्रकार)", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                RadioButton selectedButton = findViewById(selectedRoleId);
                role = selectedButton.getText().toString();
            }

            if (isValid) {
                boolean isInserted = dbHelper.registerUser(username, mobile, email, username, password,role); // using fullname for username for now
                if (isInserted) {
                    Toast.makeText(RegistrationActivity.this, "नोंदणी यशस्वी", Toast.LENGTH_SHORT).show();


                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegistrationActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();

                    // Simulate unique Jivanman ID using timestamp
                    String jivanmanId = "JIV" + System.currentTimeMillis();

                    editor.putString("username",username);
                    editor.putString("mobile", mobile);
                    editor.putString("name",username);
                    editor.putString("email", email);
                    editor.putString("role", role);
                    editor.putString("jivanman_id", jivanmanId);
                    editor.putString("myQRValue", "https://wa.me/91" + mobile);
                    editor.putBoolean("isLogin", true);
                    editor.putString("password", password);
                    editor.apply();

                    try {
                        String strSmsMobileNo = etRegistrationMobileNo.getText().toString().trim();
                        String strWelcomeMessage = username + ", तुमचे Jivanman मध्ये खूप हार्दिक स्वागत आहे!";

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(strSmsMobileNo, null, strWelcomeMessage, null, null);

                        Toast.makeText(RegistrationActivity.this, "SMS यशस्वीरित्या पाठवले", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegistrationActivity.this, "SMS पाठवण्यात अयशस्वी", Toast.LENGTH_SHORT).show();
                    }

                    // Navigate to Login Activity
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(RegistrationActivity.this, "हा मोबाईल, ईमेल किंवा वापरकर्ता नाव आधीच नोंदणीकृत आहे", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}