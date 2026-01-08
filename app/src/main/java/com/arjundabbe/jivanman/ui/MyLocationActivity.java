package com.arjundabbe.jivanman.ui;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.databinding.ActivityMyLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * MyLocationActivity
 * ------------------
 * Displays user's live location on Google Map with:
 * - Marker + accuracy circle
 * - Reverse geocoded address
 * - Camera follow
 * - Map type controls
 * - Predefined reference markers & polyline
 */
public class MyLocationActivity extends FragmentActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMyLocationBinding binding;

    private LocationManager locationManager;
    private Marker currentLocationMarker;
    private Circle currentLocationCircle;

    public static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply saved Light/Dark theme BEFORE UI loads
        applySavedTheme();

        super.onCreate(savedInstanceState);
        binding = ActivityMyLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Location manager for GPS / Network updates
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Runtime permission check
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION
            );
        } else {
            startLocationUpdates();
        }
    }

    /**
     * Start listening for location updates
     */
    private void startLocationUpdates() {

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationOnMap(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAccuracy()
                );
            }
        };

        // Prefer GPS, fallback to Network provider
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,        // 1 second
                    1,           // 1 meter
                    listener
            );
        } else if (locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    listener
            );
        }
    }

    /**
     * Update marker, accuracy circle & camera position
     */
    private void updateLocationOnMap(
            double latitude,
            double longitude,
            float accuracy
    ) {

        LatLng myLocation = new LatLng(latitude, longitude);

        // Marker update
        if (currentLocationMarker == null) {
            currentLocationMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(myLocation)
                            .title("माझे स्थान")
            );
        } else {
            currentLocationMarker.setPosition(myLocation);
        }

        // Accuracy circle
        if (currentLocationCircle != null) {
            currentLocationCircle.remove();
        }

        currentLocationCircle = mMap.addCircle(
                new CircleOptions()
                        .center(myLocation)
                        .radius(accuracy)
                        .strokeColor(Color.BLUE)
                        .fillColor(0x220000FF)
                        .strokeWidth(2f)
        );

        // Reverse geocoding (LatLng → Address)
        try {
            Geocoder geocoder =
                    new Geocoder(this, Locale.getDefault());
            List<Address> addresses =
                    geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                currentLocationMarker.setTitle(
                        "माझे स्थान: " +
                                addresses.get(0).getAddressLine(0)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Smooth camera follow
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(myLocation, 17),
                1000,
                null
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Map UI controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Reference locations
        LatLng office = new LatLng(20.2950575, 76.0886663);
        LatLng college = new LatLng(19.613588, 75.789289);

        mMap.addMarker(new MarkerOptions()
                .position(office)
                .title("जीवनमान कार्यालय, भारज बु."));

        mMap.addMarker(new MarkerOptions()
                .position(college)
                .title("शासकीय तंत्रनिकेतन, अंबड"));

        // Route line
        mMap.addPolyline(
                new PolylineOptions()
                        .add(office, college)
                        .width(5f)
                        .color(Color.BLUE)
        );

        // Radius circle
        mMap.addCircle(
                new CircleOptions()
                        .center(office)
                        .radius(800)
                        .strokeColor(Color.BLACK)
                        .strokeWidth(1.5f)
                        .fillColor(Color.TRANSPARENT)
        );

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(office, 16),
                1000,
                null
        );

        // Map type controls
        findViewById(R.id.btnNormal)
                .setOnClickListener(v ->
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));

        findViewById(R.id.btnSatellite)
                .setOnClickListener(v ->
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));

        findViewById(R.id.btnHybrid)
                .setOnClickListener(v ->
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));

        findViewById(R.id.btnTerrain)
                .setOnClickListener(v ->
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));

        findViewById(R.id.btnNone)
                .setOnClickListener(v ->
                        mMap.setMapType(GoogleMap.MAP_TYPE_NONE));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    /**
     * Apply saved Light/Dark theme
     */
    private void applySavedTheme() {
        SharedPreferences prefs =
                getSharedPreferences("app_settings", MODE_PRIVATE);

        boolean dark =
                prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                dark
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
