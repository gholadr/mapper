package com.thinkful.mapper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private static final String TAG = "Mapper";
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int LOCATION_COARSE_REQUEST=37;
    private static final int LOCATION_FINE_REQUEST=LOCATION_COARSE_REQUEST+1;
    int permissionCoarseLocationCheck;
    int permissionFineLocationCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        permissionCoarseLocationCheck  = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionFineLocationCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCoarseLocationCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_COARSE_REQUEST);
            Log.d(TAG, "requesting coarse perms");
        }
        else{
            Log.d(TAG, "permissions already granted or not needed");
            enableSetLocation();
        }

/*        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng nyc = new LatLng(40.72493, -73.996599);
        mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(40.72493, -73.996599))
                        .title("Thinkful Headquarters")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.thinkful))
                        .snippet("On a mission to reinvent education")

                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        ).showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(40.72493, -73.996599), 12));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
            }
        }, 2000);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);*/
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case LOCATION_COARSE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location granted!");
                    if(permissionFineLocationCheck != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_FINE_REQUEST);
                        Log.d(TAG, "requesting fine perms");
                    }

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d(TAG, "request coarse location denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            case LOCATION_FINE_REQUEST:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "fine location granted & enabling Set Location!");
                    enableSetLocation();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d(TAG, "request fine location denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void enableSetLocation(){

        Toast.makeText(this, "Ok to proceed, click on the location icon", Toast.LENGTH_SHORT).show();
        try{
            mMap.setMyLocationEnabled(true);
        }
        catch(SecurityException e){
            Log.d(TAG,e.toString());
        }


    }
}
