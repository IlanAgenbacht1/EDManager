package com.example.edmanager.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.location.Geocoder;
import android.location.Address;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.edmanager.R;
import com.example.edmanager.utility.Constant;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;
    private Geocoder geocoder;
    private Button saveButton;
    private LatLng selectedLocation;
    private Address address;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        searchView = findViewById(R.id.searchView);
        saveButton = findViewById(R.id.save_button);

        geocoder = new Geocoder(this, Locale.getDefault());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null && !location.equals("")) {

                    try {

                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    if (addressList != null && !addressList.isEmpty()) {

                        address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        selectedLocation = latLng;
                        mMap.clear(); // Clear previous markers
                        mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        saveButton.setVisibility(View.VISIBLE); // Show save button
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (address != null) {

                    Constant.address = address;

                    finish();
                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mMap.clear(); // Clear previous markers
                selectedLocation = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

                try {

                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                    if (addresses != null && !addresses.isEmpty()) {

                        address = addresses.get(0);
                        String addressText = address.getAddressLine(0);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(addressText));
                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }

                saveButton.setVisibility(View.VISIBLE); // Show the save button when a pin is placed
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }


    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location lastKnownLocation = task.getResult();
                            LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        } else {
                            Toast.makeText(GoogleLocation.this, "Unable to get current location",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}