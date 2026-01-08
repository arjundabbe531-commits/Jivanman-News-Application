package com.arjundabbe.jivanman.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import com.arjundabbe.jivanman.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * LoginActivity handles both email/password login and Google Sign-In.
 * Also manages user creation/fetch in Firestore and local session storage.
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText etLoginUsername, etLoginPassword;
    private MaterialButton btnLoginLogin;
    private SignInButton btnGoogleSignIn;
    private TextView tvLoginRegisterNow, tvLoginForgetPassword;

    // Firebase & Google Sign-In
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;

    private static final int RC_SIGN_IN = 999;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applySavedTheme(); // Apply saved dark/light theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLoginLogin = findViewById(R.id.btnLoginLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        tvLoginRegisterNow = findViewById(R.id.tvLoginRegisterNow);
        tvLoginForgetPassword = findViewById(R.id.tvLoginForgetPassword);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("कृपया थोडा थांबा...");
        progressDialog.setCancelable(false);

        // 🔁 Auto-login if user already signed in
        if (firebaseAuth.getCurrentUser() != null) {
            progressDialog.show();
            fetchOrCreateUser(firebaseAuth.getCurrentUser().getUid(),
                    firebaseAuth.getCurrentUser().getEmail());
        }

        // Navigation to registration and forget password screens
        tvLoginRegisterNow.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class)));

        tvLoginForgetPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgetPasswordActivity.class)));

        // Email login button
        btnLoginLogin.setOnClickListener(v -> userLogin());

        // Initialize Google Sign-In
        initGoogleSignIn();
        styleGoogleButton(btnGoogleSignIn);

        // Google Sign-In button click
        btnGoogleSignIn.setOnClickListener(v ->
                googleSignInClient.signOut().addOnCompleteListener(task ->
                        startActivityForResult(
                                googleSignInClient.getSignInIntent(),
                                RC_SIGN_IN
                        )));
    }

    // ================= EMAIL LOGIN =================
    private void userLogin() {
        String email = etLoginUsername.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        // Validate email & password
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginUsername.setError("कृपया वैध ईमेल भरा");
            return;
        }

        if (password.isEmpty()) {
            etLoginPassword.setError("पासवर्ड आवश्यक आहे");
            return;
        }

        progressDialog.show();

        // Firebase email login
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result ->
                        fetchOrCreateUser(
                                result.getUser().getUid(),
                                result.getUser().getEmail()
                        ))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this,
                            "लॉगिन अयशस्वी: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // ================= GOOGLE LOGIN =================
    private void initGoogleSignIn() {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                                .getResult(Exception.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (Exception e) {
                Toast.makeText(this,
                        "Google Login Failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        // Firebase sign-in with Google
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(result ->
                        fetchOrCreateUser(
                                result.getUser().getUid(),
                                result.getUser().getEmail()
                        ))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this,
                            "Authentication Failed",
                            Toast.LENGTH_SHORT).show();
                });
    }

    // ================= USER FETCH / CREATE =================
    private void fetchOrCreateUser(String uid, String email) {

        firestore.collection("Users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        createNewUser(uid, email);
                        return;
                    }
                    proceedLogin(doc);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this,
                            "Profile fetch error",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void createNewUser(String uid, String email) {

        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("email", email);
        user.put("role", "reader");
        user.put("verified", false);
        user.put("joinedAt", FieldValue.serverTimestamp());

        // Create user in Firestore
        firestore.collection("Users").document(uid)
                .set(user)
                .addOnSuccessListener(unused ->
                        firestore.collection("Users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(this::proceedLogin));
    }

    private void proceedLogin(DocumentSnapshot doc) {

        progressDialog.dismiss();
        saveToLocal(doc);

        String role = doc.getString("role");

        // For now, all users go to HomeActivity
        startActivity(new Intent(this,
                "admin".equalsIgnoreCase(role)
                        ? HomeActivity.class
                        : HomeActivity.class));

        finish();
    }

    // Save login info locally
    private void saveToLocal(DocumentSnapshot doc) {
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();

        editor.putBoolean("isLogin", true);
        editor.putString("name", doc.getString("name"));
        editor.putString("email", doc.getString("email"));
        editor.putString("role", doc.getString("role"));
        editor.apply();
    }

    // ================= UI HELPERS =================
    private void styleGoogleButton(SignInButton btn) {
        btn.setSize(SignInButton.SIZE_WIDE);
        for (int i = 0; i < btn.getChildCount(); i++) {
            View v = btn.getChildAt(i);
            if (v instanceof TextView) {
                TextView t = (TextView) v;
                t.setText("Google सह लॉगिन करा");
                t.setTextColor(Color.BLACK);
                t.setTypeface(
                        ResourcesCompat.getFont(this, R.font.mukta_extralight),
                        Typeface.NORMAL
                );
            }
        }
    }

    private void applySavedTheme() {
        boolean isDark =
                getSharedPreferences("app_settings", MODE_PRIVATE)
                        .getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
