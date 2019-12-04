package com.nwhacks.recharge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.NotificationChannel.DEFAULT_CHANNEL_ID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    // button declaring
    private Button usersBtn;
    private Button settingsBtn;
    private Button focusBtn;
    float zoomLvl = 16f;
    double longitude, latitude;
    double endLong, endLat;
    String isCurrentLoc = "Location: Default";
    Polyline polyline;
    Marker curMarker;

    int PERMISSION_ID = 44;
    Location curLoc;
    FusedLocationProviderClient flpClient;

    Marker m1;
    boolean reset = false;

    FirebaseFirestore fbFs;

    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable(){
        @Override
        public void run(){
            handler.postDelayed(periodicUpdate, 1000 - SystemClock.elapsedRealtime()%1000);

            getLastLocation();
            LatLng myPos = new LatLng(latitude, longitude);
            m1.setPosition(myPos);
            m1.setTitle(isCurrentLoc);
            if(curMarker != null && curMarker != m1) plotRoute(curMarker);
            if(mMap != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, zoomLvl));
            }
        }
    };

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
        focusBtn = findViewById(R.id.centreButton);

        // button click listener
        usersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent usersIntent = new Intent(getApplicationContext(), UsersActivity.class);

                startActivity(usersIntent);
            }
        });

        focusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getLastLocation();
                if(mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomLvl));
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
        getLastLocation();
        System.out.println("*************** this is the device name!!" + getDeviceName());

        fbFs = FirebaseFirestore.getInstance();

    }

    private void sendToFirebase(FirebaseFirestore db, HashMap data, String col){
        db.collection(col).add(data)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                flpClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                requestNewLocationData();
                                if (location == null) {
                                    isCurrentLoc = "Location: Default";
                                } else {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    isCurrentLoc = "Location: Current";
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        flpClient = LocationServices.getFusedLocationProviderClient(this);
        flpClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            curLoc = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
            handler.post(periodicUpdate);
        }
        reset = false;
        if(mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomLvl));
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
        reset = false;
        mMap = googleMap;
        latitude = 49.2667984;
        longitude = -123.2530708;
        endLat = 49.2638279;
        endLong = -123.254772;
        double lat3 = 49.2629248;
        double lng3 = -123.2479948;
        getLastLocation();
        // Add a marker in Sydney and move the camera
        LatLng myPos = new LatLng(latitude, longitude);
        LatLng ESB = new LatLng(endLat, endLong);
        LatLng LSI = new LatLng(lat3, lng3);

        float results[] = new float[10];
        Location.distanceBetween(latitude, longitude, endLat, endLong, results);

        float results2[] = new float[10];
        Location.distanceBetween(latitude, longitude, lat3, lng3, results2);

        String dist = "John Smith\nCharger: USB Type-C\nDistance: " + (int) results[0] + "m";
        String dist2 = "Gerald Driscoll\nCharger: USB Micro-B\nDistance: " + (int) results2[0] + "m";

        m1 = mMap.addMarker(new MarkerOptions().position(myPos).title(isCurrentLoc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.addMarker(new MarkerOptions().position(ESB).title(dist));
        mMap.addMarker(new MarkerOptions().position(LSI).title(dist2));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, zoomLvl));

        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        handler.post(periodicUpdate);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    private void generateMarkers(){

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker != m1) {
            plotRoute(marker);
            curMarker = marker;
        }
        Toast toast = Toast.makeText(getApplicationContext(),marker.getTitle(),Toast.LENGTH_LONG);
        toast.show();
        System.out.println("Marker Clicked");

        HashMap<String, Object> key = new HashMap<>();
        HashMap<String, Object> data = new HashMap<>();
        data.put("sent", "yes");
        key.put("t2", data);
        if(fbFs != null)
            sendToFirebase(fbFs, key, "test");

        return false;
    }

    public void sendBatLowNoti() {

    }

    public void sendReqNoti() {

        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("TESTING NOTIFICATION TITLE")
                .setContentText("testing notification content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(2);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(builder.hashCode(), builder.build());

    }

    public void plotRoute(Marker mTo){
        if(polyline != null) polyline.remove();
        polyline = mMap.addPolyline(new PolylineOptions()
                .add(m1.getPosition(), mTo.getPosition())
                .width(15)
                .color(Color.YELLOW)
        );
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
