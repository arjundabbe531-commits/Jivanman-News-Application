package com.arjundabbe.jivanman.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.arjundabbe.jivanman.NetworkChangeListener;
import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.ui.fragments.EpaperFragment;
import com.arjundabbe.jivanman.ui.fragments.HomeFragment;
import com.arjundabbe.jivanman.ui.fragments.NewsCategoryFragment;
import com.arjundabbe.jivanman.ui.fragments.SavedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * HomeActivity
 * -------------
 * Main activity of the Jivanman application.
 * Acts as a container for:
 * - Bottom Navigation
 * - Navigation Drawer
 * - Toolbar menu
 * - Fragment navigation
 * - Theme handling
 * - Network monitoring
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    // Used for double-back press exit
    private boolean doubletap = false;

    // UI components
    private DrawerLayout drawerLayout;
    private ImageButton btnDrawerToggle;
    private Toolbar toolbar;

    // Network connectivity listener
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ---------------------------------
        // Apply saved theme BEFORE layout
        // ---------------------------------
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Log.d(TAG, "onCreate: HomeActivity started");

        // ---------------------------------
        // Firebase Cloud Messaging
        // Subscribe all users to topic
        // ---------------------------------
        FirebaseMessaging.getInstance().subscribeToTopic("allUsers")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Successfully subscribed to allUsers topic");
                    } else {
                        Log.e("FCM", "Subscription failed");
                    }
                });

        // (Duplicate subscription retained as-is)
        FirebaseMessaging.getInstance().subscribeToTopic("allUsers");

        // ---------------------------------
        // Toolbar setup
        // ---------------------------------
        toolbar = findViewById(R.id.customToolbarHomeActivity);
        setSupportActionBar(toolbar);

        // ---------------------------------
        // Load default fragment (Home)
        // ---------------------------------
        loadFragment(new HomeFragment());

        // ---------------------------------
        // Bottom Navigation setup
        // ---------------------------------
        BottomNavigationView bottomNav = findViewById(R.id.homeBottomNavView);
        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.bottom_Nav_Home) {
                selectedFragment = new HomeFragment();

            } else if (id == R.id.bottom_Nav_saved) {
                selectedFragment = new SavedFragment();

            } else if (id == R.id.bottom_Nav_Epaper) {
                selectedFragment = new EpaperFragment();

            } else if (id == R.id.bottom_Nav_Profile) {
                // Profile opens as a new Activity
                startActivity(new Intent(HomeActivity.this, MyProfileActivity.class));
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // ---------------------------------
        // Navigation Drawer setup
        // ---------------------------------
        drawerLayout = findViewById(R.id.drawer_layout);
        btnDrawerToggle = findViewById(R.id.btnDrawerToggle);

        btnDrawerToggle.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            // Category-based navigation
            if (id == R.id.MenuItemHome) {
                loadFragment(new HomeFragment());

            } else if (id == R.id.MenuItemMaharashtra) {
                loadFragment(NewsCategoryFragment.newInstance("महाराष्ट्र"));

            } else if (id == R.id.MenuItemEconomy) {
                loadFragment(NewsCategoryFragment.newInstance("अर्थव्यवस्था"));

            } else if (id == R.id.MenuItemPolitics) {
                loadFragment(NewsCategoryFragment.newInstance("सत्ताकारण"));

            } else if (id == R.id.MenuItemSport) {
                loadFragment(NewsCategoryFragment.newInstance("क्रीडा"));

            } else if (id == R.id.MenuItemEntertainment) {
                loadFragment(NewsCategoryFragment.newInstance("मनोरंजन"));

            } else if (id == R.id.MenuItemAgriculture) {
                loadFragment(NewsCategoryFragment.newInstance("शेती"));

            } else if (id == R.id.MenuItemCommpititiveExam) {
                loadFragment(NewsCategoryFragment.newInstance("स्पर्धा परीक्षा"));

            } else if (id == R.id.MenuItemTechnology) {
                loadFragment(NewsCategoryFragment.newInstance("तंत्रज्ञान"));

            } else if (id == R.id.MenuItemEducation) {
                loadFragment(NewsCategoryFragment.newInstance("शिक्षण"));

            } else if (id == R.id.MenuItemWeather) {
                loadFragment(NewsCategoryFragment.newInstance("हवामान"));

            } else if (id == R.id.MenuItemHealth) {
                loadFragment(NewsCategoryFragment.newInstance("आरोग्य"));
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // ---------------------------------
        // First-time welcome dialog
        // ---------------------------------
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        boolean isFirstTimeHome =
                preferences.getBoolean("isFirstTimeHome", true);

        if (isFirstTimeHome) {

            View dialogView =
                    getLayoutInflater().inflate(R.layout.dialog_welcome, null);

            AlertDialog welcomeDialog =
                    new AlertDialog.Builder(this)
                            .setView(dialogView)
                            .setCancelable(false)
                            .create();

            dialogView.findViewById(R.id.btnWelcomeOk)
                    .setOnClickListener(v -> {
                        welcomeDialog.dismiss();
                        preferences.edit()
                                .putBoolean("isFirstTimeHome", false)
                                .apply();
                    });

            welcomeDialog.show();
        }
    }

    /**
     * Apply saved light/dark theme from SharedPreferences
     * This must be called before setContentView()
     */
    private void applySavedTheme() {
        SharedPreferences themePrefs =
                getSharedPreferences("app_settings", MODE_PRIVATE);

        boolean isDarkMode =
                themePrefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /**
     * Double back press to exit the app
     */
    @Override
    public void onBackPressed() {
        if (doubletap) {
            finishAffinity();
        } else {
            Toast.makeText(
                    this,
                    "एकदा अजून Back दाबा App बंद करण्यासाठी",
                    Toast.LENGTH_SHORT
            ).show();

            doubletap = true;
            new Handler().postDelayed(() -> doubletap = false, 2000);
        }
    }

    /**
     * Helper method to replace fragments inside HomeActivity
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    /**
     * Inflate top toolbar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    /**
     * Handle toolbar menu actions
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.top_menu_Search) {
            startActivity(new Intent(this, SearchActivity.class));

        } else if (id == R.id.top_menu_map) {
            startActivity(new Intent(this, MyLocationActivity.class));

        } else if (id == R.id.top_menu_upload) {
            // Upload feature placeholder (currently empty)

        } else if (id == R.id.top_menu_idcard) {
            startActivity(new Intent(this, IdCardActivity.class));

        } else if (id == R.id.top_menu_scantext) {
            startActivity(new Intent(this, TextScanActivity.class));

        } else if (id == R.id.top_menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Register network listener when activity becomes visible
     */
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(
                networkChangeListener,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );
    }

    /**
     * Unregister network listener to avoid memory leaks
     */
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeListener);
    }
}
