/*package com.example.lifecircle;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lifecircle.databinding.ActivityMaps2Binding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}*/




package com.example.lifecircle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lifecircle.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LatLng myLocation;
    private static final int req_code = 1;
    private Marker me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // мапата е вчитана ама се уште не е поставена
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this); // повикува onMapLoaded кога поставувањето е завршено
    }


    @Override
    public void onMapLoaded() {

        //poshiroko gleda kamerata
        LatLngBounds bounds = new LatLngBounds(new LatLng(40,
                20), new LatLng(43, 23));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));


        //za povrzuvanje markeri kliknatite, so mojata lokacija
        mMap.setOnMarkerClickListener(this);

        //marker za moja lokacija
        myLocation = getMyLocation();

        if (myLocation == null) {
            Toast.makeText(this, "Не може да се пристапи до локација. Проверете Settings.", Toast.LENGTH_LONG).show();
        } else {
            me = mMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .draggable(true)
                    .title("ME!")
            );
            mMap.setOnMarkerDragListener(this);
        }

        /*
        *
        *  private static void addMarkerWithCameraZooming(Context ctx, GoogleMap googleMap, double latitude, double longitude, String title, boolean dragabble) {
            LatLng current_latlng = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(current_latlng)
                    .title(title)
                    .snippet(getLocality(current_latlng, ctx))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker))
                    .draggable(dragabble)
                              );
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).tilt(30).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

mGoogleMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker markerDragStart) {
                if (BuildConfig.DEBUG)
                Log.i("Marker drag", "start");
            }

            @Override
            public void onMarkerDragEnd(Marker markerDragEnd) {
                if (BuildConfig.DEBUG)
                    Log.i("Marker drag", "start");
            }

            @Override
            public void onMarkerDrag(Marker markerDrag) {
                if (BuildConfig.DEBUG)
                    Log.i("Marker drag", "start");
            }
        });
        * */


    }

    private LatLng getMyLocation() {

        // обид за добивање локација на еден од три начини: GPS, cell/wifi мрежа и пасивен режим
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, req_code);}

        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            /*List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);


                if (l == null) {
                continue;
            }
            if (loc == null || l.getAccuracy() < loc.getAccuracy()) {
                loc = l;
            }
            }*/
        if (loc == null) {
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc == null) {
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        if (loc == null) {
            return new LatLng(41.983, 21.467);
        } else {
            double myLat = loc.getLatitude();
            double myLng = loc.getLongitude();
            return new LatLng(myLat, myLng);
        }
    }
    public void onRequestPermissionsResult(int reqCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(reqCode, permissions, grantResults);
    }

    public boolean onMarkerClick(Marker marker) {
        if (myLocation != null) {
            LatLng markerLatLng = marker.getPosition();
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onMapClick(LatLng point) {
        mMap.clear();
        me = mMap.addMarker(new MarkerOptions()
                .position(point)
                .snippet("ME!"));
        Toast.makeText(getApplicationContext(), "Lat: "+me.getPosition().latitude + ", Long: "+me.getPosition().longitude, Toast.LENGTH_SHORT).show();
        mMap.setOnMapClickListener(this);

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mMap.clear();
        me = mMap.addMarker(new MarkerOptions()
                .position(marker.getPosition())
                .draggable(true)
                .snippet("ME!"));
        Toast.makeText(getApplicationContext(), "Lat: "+me.getPosition().latitude + ", Long: "+me.getPosition().longitude, Toast.LENGTH_SHORT).show();
        mMap.setOnMarkerDragListener(this);
    }
}