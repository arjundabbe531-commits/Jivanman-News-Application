package com.arjundabbe.jivanman.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.ui.fragments.EpaperFragment;
import com.arjundabbe.jivanman.ui.fragments.HomeFragment;
import com.arjundabbe.jivanman.ui.fragments.SavedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    public boolean doubletap = false;
    DrawerLayout drawerLayout;
    ImageButton btnDrawerToggle;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);



        toolbar = findViewById(R.id.customToolbarHomeActivity);
        setSupportActionBar(toolbar);

        // Load default fragment
        loadFragment(new HomeFragment());

        // Bottom Navigation Setup
        BottomNavigationView bottomNav = findViewById(R.id.homeBottomNavView);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String message = "";

            int id = item.getItemId();
            if (id == R.id.bottom_Nav_Home) {
                selectedFragment = new HomeFragment();

            } else if (id == R.id.bottom_Nav_saved) {
                selectedFragment = new SavedFragment();

            } else if (id == R.id.bottom_Nav_Epaper) {
                selectedFragment = new EpaperFragment();

            } else if (id == R.id.bottom_Nav_Search) {

            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        btnDrawerToggle = findViewById(R.id.btnDrawerToggle);
        btnDrawerToggle.setOnClickListener(v -> drawerLayout.open());

        NavigationView navigationView = findViewById(R.id.NavigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.MenuItemHome) {
                Toast.makeText(this, "होम क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemMaharashtra) {
                Toast.makeText(this, "महाराष्ट्र क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemPolitics) {
                Toast.makeText(this, "सत्ताकारण क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemCity) {
                Toast.makeText(this, "शहर क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemCarrier) {
                Toast.makeText(this, "करीअर क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemEconomy) {
                Toast.makeText(this, "अर्थभान क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemSport) {
                Toast.makeText(this, "क्रीडा क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemFuture) {
                Toast.makeText(this, "राशीभविष्य क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemUpsc) {
                Toast.makeText(this, "UPSC क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemMpsc) {
                Toast.makeText(this, "MPSC क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemHealth) {
                Toast.makeText(this, "हेंल्थ क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemRecpi) {
                Toast.makeText(this, "रेसिपी क्लिक केले", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.MenuItemVicharmanch) {
                Toast.makeText(this, "विचारमंच क्लिक केले", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // First-time welcome dialog
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTimeHome = preferences.getBoolean("isFirstTimeHome", true);

        if (isFirstTimeHome) {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_welcome, null);

            AlertDialog welcomeDialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            dialogView.findViewById(R.id.btnWelcomeOk).setOnClickListener(v -> {
                welcomeDialog.dismiss();

                // Save flag to not show again
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isFirstTimeHome", false);
                editor.apply();
            });

            welcomeDialog.show();
        }

    }

    @Override
    public void onBackPressed() {
        if (doubletap) {
            finishAffinity();
        } else {
            Toast.makeText(this, "एकदा अजून Back दाबा App बंद करण्यासाठी", Toast.LENGTH_SHORT).show();
            doubletap = true;

            new Handler().postDelayed(() -> doubletap = false, 2000);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.top_menu_map) {
            startActivity(new Intent(this, MyLocationActivity.class));
            return true;
        } else if (id == R.id.top_menu_upload) {
            Toast.makeText(this, "बातमी पाठवा क्लिक केले", Toast.LENGTH_SHORT).show();
            // Optional: startActivity(new Intent(this, UploadActivity.class));
            return true;
        } else if (id == R.id.top_menu_idcard) {
            startActivity(new Intent(this, IdCardActivity.class));
            return true;
        } else if (id == R.id.top_menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
