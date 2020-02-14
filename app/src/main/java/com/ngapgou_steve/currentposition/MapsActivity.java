package com.ngapgou_steve.currentposition;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Views
    private LinearLayout formView;
    private TextInputEditText nameView, latitudeView, longitudeView;
    private MaterialButton addToMap;
    private ImageView showForm;

    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final Float DEFAULT_ZOOM = 5f;

    public static final String TAG = "MapsActivity";
    private static final int ERROR_DIAOLG_REQUEST = 9001;

    // vars
    private boolean permissionLocationGranted = false;
    private boolean isMapReady = false;
    private String name;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        nameView = findViewById(R.id.name);
        formView = findViewById(R.id.form);
        showForm = findViewById(R.id.show_form);
        addToMap = findViewById(R.id.add_to_map);
        latitudeView = findViewById(R.id.latitude);
        longitudeView = findViewById(R.id.longitude);

        showForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForm.setVisibility(View.GONE);
                formView.setVisibility(View.VISIBLE);
            }
        });

        addToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                longitude = Double.parseDouble(longitudeView.getText().toString());
                latitude = Double.parseDouble(latitudeView.getText().toString());
                name = nameView.getText().toString();
                if (name.length() > 3){
                    if (longitude != 0){
                        if (latitude != 0){
                            try {
                                moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM, name+"; Lat:"+latitude+" Long:"+longitude);
                                longitudeView.setText("");
                                nameView.setText("");
                                latitudeView.setText("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            showForm.setVisibility(View.VISIBLE);
                            formView.setVisibility(View.GONE);
                        }else {
                            latitudeView.setError("Incorrect input");
                        }
                    }else {
                        longitudeView.setError("Incorrect input");
                    }
                }else {
                    nameView.setError("Incorrect input");
                }
            }
        });

        Log.e(TAG,"isServicesOK(): checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS){
            Log.e(TAG,"isServicesOK(): Google play services is working");
            getLocalPermission();
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.e(TAG,"isServicesOK(): an error occurred but we can fix it");
            Dialog dialog =GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIAOLG_REQUEST);
            dialog.show();
        }else {
            //Toast.makeText(context, "You can't make Maps request", Toast.LENGTH_SHORT).show();
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocalPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permissionLocationGranted = true;
                initMap();

               /* CountDownTimer timer = new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long l) {
                        if (isMapReady){
                            if (currentLocation != null){
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My Location; Long: "+currentLocation.getLongitude()+" Lat: "+currentLocation.getLatitude()+"");
                                isMapReady = false;
                            }
                        }
                    }

                    @Override
                    public void onFinish() {

                    }
                };
                timer.start();*/
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionLocationGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionLocationGranted = false;
                            return;
                        }
                    }
                    permissionLocationGranted = true;
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (permissionLocationGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            initMap();
            isMapReady = true;
        }else {
            Toast.makeText(getApplicationContext(), "Location Permission is not Granted", Toast.LENGTH_LONG).show();
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                         currentLocation = (Location) task.getResult();
                    } else {
                        Toast.makeText(getApplicationContext(), "unable get the location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng, Float zoom, String title) {
        //mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(markerOptions);
            //mMap.setMyLocationEnabled(false);
        }
    }
}
