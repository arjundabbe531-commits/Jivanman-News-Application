package com.arjundabbe.jivanman.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

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

public class MyLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMyLocationBinding binding;
    private LocationManager locationManager;
    private Marker currentLocationMarker;
    private Circle currentLocationCircle;

    public static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                updateLocationOnMap(latitude, longitude, location.getAccuracy());
            }
        };

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 1, listener); // 1 sec, 1 meter
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000, 1, listener); // fallback
        }
    }

    private void updateLocationOnMap(double latitude, double longitude, float accuracy) {
        LatLng myLocation = new LatLng(latitude, longitude);

        if (currentLocationMarker == null) {
            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .title("माझे स्थान"));
        } else {
            currentLocationMarker.setPosition(myLocation);
        }

        if (currentLocationCircle != null) {
            currentLocationCircle.remove();
        }

        currentLocationCircle = mMap.addCircle(new CircleOptions()
                .center(myLocation)
                .radius(accuracy)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(2f));

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && !addressList.isEmpty()) {
                String address = addressList.get(0).getAddressLine(0);
                currentLocationMarker.setTitle("माझे स्थान: " + address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Follow the marker with camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17), 1000, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable zoom and location controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Optional: Add fixed markers and polyline
        LatLng villageOffice = new LatLng(20.2950575, 76.0886663);
        LatLng college = new LatLng(19.613588, 75.789289);

        mMap.addMarker(new MarkerOptions().position(villageOffice).title("जीवनमान कार्यालय, भारज बु."));
        mMap.addMarker(new MarkerOptions().position(college).title("शासकीय तंत्रनिकेतन, अंबड"));

        mMap.addPolyline(new PolylineOptions()
                .add(villageOffice, college)
                .width(5f)
                .color(Color.BLUE));

        mMap.addCircle(new CircleOptions()
                .center(villageOffice)
                .fillColor(Color.TRANSPARENT)
                .strokeColor(Color.BLACK)
                .strokeWidth(1.5f)
                .radius(800));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(villageOffice, 16), 1000, null);

        // Map type buttons 
        findViewById(R.id.btnNormal).setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
        findViewById(R.id.btnSatellite).setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        findViewById(R.id.btnHybrid).setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));
        findViewById(R.id.btnTerrain).setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        findViewById(R.id.btnNone).setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_NONE));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }
}
