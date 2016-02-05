package com.thinkful.mapper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG =  "Mapper";
    private static final int LOCATION_REQUEST=0;
    int permissionCoarseLocationCheck;
    int permissionFineLocationCheck;
    private Location mLastKnownLocation;
    private boolean mShowMe = false;
    protected Switch mSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        checkPermissions();
        mSwitch = (Switch)findViewById(R.id.switch1);
        mSwitch.setVisibility(View.INVISIBLE);
        mSwitch.setChecked(false);
        //attach a listener to check for changes in state

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "Tracking is on!", Toast.LENGTH_SHORT).show();
                    mShowMe = true;
                    checkPermissions();
                    showLocation();
                   }
                else{
                    Toast.makeText(getApplicationContext(), "Tracking is off!", Toast.LENGTH_SHORT).show();
                    mShowMe = false;
                    removeLocation();
                }
            }
        });
    }



    public void checkPermissions(){
        permissionCoarseLocationCheck  = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionFineLocationCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCoarseLocationCheck != PackageManager.PERMISSION_GRANTED || permissionFineLocationCheck != PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            Log.d(TAG, "requesting  location perms");
        }
        else{
            Log.d(TAG, "permissions already granted or not needed");
        }
    }
    public void showToggle(){
        mSwitch.setVisibility(View.VISIBLE);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_REQUEST) {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, " location granted!");
                    enableSetLocation();
                } else {
                    Log.d(TAG, "request location denied");
                }
        }
    }


    public void enableSetLocation(){
        try{
            mMap.setMyLocationEnabled(true);
        }
        catch(SecurityException e){
            Log.d(TAG,e.toString());
        }
        showToggle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void removeLocation(){
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
        mMap.clear();
    }

    public void showLocation(){
        if(!mShowMe) return;
        Location mCurrentLocation;
        try{
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        catch(SecurityException e){
            Log.d(TAG, e.toString());
            return;
        }
        //if current location null, exit. should happen very infrequently.
        if(mCurrentLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 19));
        }
        if(mLastKnownLocation != null) {
            // Add a marker for last known location and move the camera
            //LatLng lastKnownLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .add(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
            );
        }
        mLastKnownLocation = mCurrentLocation;

    }
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //this.set
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch(SecurityException e){
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        showLocation();
        Log.i("Where am I?", "Latitude: " + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }
    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();
        if (mGoogleApiClient.isConnected()) {
            //setUpMapIfNeeded();    // <-from previous tutorial
            startLocationUpdates();
        }
    }
}
