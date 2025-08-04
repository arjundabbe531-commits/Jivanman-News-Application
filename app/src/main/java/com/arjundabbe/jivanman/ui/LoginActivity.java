package com.arjundabbe.jivanman.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arjundabbe.jivanman.R;

public class LoginActivity extends AppCompatActivity {

    ImageView ivLoginLogo,ivTogglePassword;
    EditText etLoginUsername, etLoginPassword;
    Button btnLoginLogin;
    TextView tvLoginRegisterNow,tvLoginForgetPassword;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isLogin = preferences.getBoolean("isLogin", false);

        if (isLogin) {
            // If already logged in, skip login page
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // Prevent going back to login
            return;
        }

        setTitle("Login Activity");
        //objectName = findViewById(R.id.idname);
        ivLoginLogo = findViewById(R.id.ivLoginLogo);
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLoginLogin = findViewById(R.id.btnLoginLogin);
        tvLoginRegisterNow = findViewById(R.id.tvLoginRegisterNow);
        tvLoginForgetPassword = findViewById(R.id.tvLoginForgetPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);


        ivTogglePassword.setOnClickListener(v -> {
            if (etLoginPassword.getTransformationMethod()instanceof PasswordTransformationMethod) {
                etLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            }else {
                etLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_closed_eyes);
            }
            etLoginPassword.setSelection(etLoginPassword.length());
        });

        tvLoginForgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        });

        tvLoginRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });




        btnLoginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etLoginUsername.getText().toString().trim();
                boolean isValid = true;

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etLoginUsername.setError("कृपया वैध ईमेल भरा");

                    etLoginUsername.setVisibility(View.VISIBLE);
                    isValid = false;
                }


                String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
                String password = etLoginPassword.getText().toString().trim();
                //check password
                if (password.isEmpty()) {

                    etLoginPassword.setError("कृपया पासवर्ड भरा");
                    etLoginPassword.setVisibility(View.VISIBLE);
                    isValid = false;

                } else if (password.length() < 8) {

                    etLoginPassword.setError("पासवर्ड किमान ८ अक्षरे असावा");
                    etLoginPassword.setVisibility(View.VISIBLE);
                    isValid = false;

                }else if(!password.matches(passwordPattern)) {
                    etLoginPassword.setError("पासवर्डमध्ये १ Capital, १ small व १ विशेष चिन्ह असणे आवश्यक आहे");
                    isValid = false;
                }

                if (isValid) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

                    String registeredEmail = preferences.getString("email", "");
                    String registeredPassword = preferences.getString("password", ""); // You need to store this during registration

                    String enteredEmail = etLoginUsername.getText().toString().trim();
                    String enteredPassword = etLoginPassword.getText().toString().trim();

                    if (enteredEmail.equals(registeredEmail) && enteredPassword.equals(registeredPassword)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin", true);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "पुन्हा स्वागत आहे!", Toast.LENGTH_SHORT).show();

                        // Navigate to home
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "चुकीचा ईमेल किंवा पासवर्ड", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}

