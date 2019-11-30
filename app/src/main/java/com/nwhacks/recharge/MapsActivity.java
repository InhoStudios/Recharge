package com.nwhacks.recharge;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    // button declaring
    private Button usersBtn;
    private Button settingsBtn;
    float zoomLvl = 16f;
    double longitude, latitude;
    double endLong, endLat;

    Location curLoc;
    FusedLocationProviderClient flpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // create objects
        usersBtn = findViewById(R.id.usersBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        // button click listener
        usersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent usersIntent = new Intent(getApplicationContext(), UsersActivity.class);

                startActivity(usersIntent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);

                startActivity(settingsIntent);
            }
        });

        flpClient = LocationServices.getFusedLocationProviderClient(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        latitude = 49.2667984;
        longitude = -123.2530708;
        endLat = 49.2638279;
        endLong = -123.254772;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        LatLng sydney2 = new LatLng(endLat, endLong);

        float results[] = new float[10];
        Location.distanceBetween(latitude, longitude, endLat, endLong, results);

        String dist = "Distance: " + (int) results[0] + "m";

        Marker m1 = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
        Marker m2 = mMap.addMarker(new MarkerOptions().position(sydney2).title(dist));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLvl));

        mMap.setOnMarkerClickListener(this);
    }

    private void fetchLastLocation() {

        Task<Location> task = flpClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            public void onSuccess(Location location){
                if(location != null){
                    curLoc = location;
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        fetchLastLocation();
        return false;
    }
}
