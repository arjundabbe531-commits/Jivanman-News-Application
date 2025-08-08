package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.database.DBHelper;

public class LoginActivity extends AppCompatActivity {

    ImageView ivLoginLogo, ivTogglePassword;
    EditText etLoginUsername, etLoginPassword;
    Button btnLoginLogin;
    TextView tvLoginRegisterNow, tvLoginForgetPassword;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLogin = preferences.getBoolean("isLogin", false);
        DBHelper dbHelper = new DBHelper(this);

        if (isLogin) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setTitle("Login Activity");

        ivLoginLogo = findViewById(R.id.ivLoginLogo);
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLoginLogin = findViewById(R.id.btnLoginLogin);
        tvLoginRegisterNow = findViewById(R.id.tvLoginRegisterNow);
        tvLoginForgetPassword = findViewById(R.id.tvLoginForgetPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Toggle Password Visibility
        ivTogglePassword.setOnClickListener(v -> {
            if (etLoginPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                etLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_closed_eyes);
            }
            etLoginPassword.setSelection(etLoginPassword.length());
        });

        // Forgot Password
        tvLoginForgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        });

        // Register
        tvLoginRegisterNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        // Login Logic
        btnLoginLogin.setOnClickListener(v -> {
            String loginInput = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();
            boolean isValid = true;

            // Username Validation
            if (loginInput.isEmpty()) {
                etLoginUsername.setError("कृपया ईमेल किंवा वापरकर्ता नाव भरा");
                isValid = false;
            }

            // Password Validation
            String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

            if (password.isEmpty()) {
                etLoginPassword.setError("कृपया पासवर्ड भरा");
                isValid = false;
            } else if (password.length() < 8) {
                etLoginPassword.setError("पासवर्ड किमान ८ अक्षरे असावा");
                isValid = false;
            } else if (!password.matches(passwordPattern)) {
                etLoginPassword.setError("पासवर्डमध्ये १ Capital, १ small व १ विशेष चिन्ह असणे आवश्यक आहे");
                isValid = false;
            }

            if (!isValid) return;

            @SuppressLint("Range") Cursor cursor = dbHelper.getUserByEmail(loginInput);

            if (cursor != null && cursor.moveToFirst()) {
                String dbPassword = cursor.getString(cursor.getColumnIndex("password"));

                if (password.equals(dbPassword)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLogin", true);
                    editor.putString("email", cursor.getString(cursor.getColumnIndex("email")));
                    editor.putString("name", cursor.getString(cursor.getColumnIndex("name")));
                    editor.putString("mobile", cursor.getString(cursor.getColumnIndex("mobileno")));
                    editor.putString("role", cursor.getColumnIndex("role") != -1
                            ? cursor.getString(cursor.getColumnIndex("role"))
                            : "user");
                    editor.putString("jivanman_id", "JIV" + System.currentTimeMillis());
                    editor.putString("myQRValue", "https://wa.me/91" + cursor.getString(cursor.getColumnIndex("mobileno")));
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "पुन्हा स्वागत आहे!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "चुकीचा पासवर्ड", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "वापरकर्ता सापडला नाही", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) cursor.close();
        });
    }
}
