package com.egov.android.view.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.egov.android.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnClickListener {

    private GoogleMap googleMap;
    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ((Button) findViewById(R.id.get_location)).setOnClickListener(this);
        initilizeMap();
    }

    private void initilizeMap() {
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();

        if (googleMap == null) {
            Toast.makeText(getApplicationContext(), "Sorry! unable to create maps",
                    Toast.LENGTH_SHORT).show();
        }
        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("latitude" + latLng.latitude + ":" + latLng.longitude);
                markerOptions.position(latLng);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.addMarker(markerOptions);
            }
        });
    }

    private void getLocationName() {
        try {
            List<Address> addresses;
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            System.out.println("calling this=========latitude========" + latitude);
            System.out.println("calling this=========longitude========" + longitude);

            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            System.out.println("calling this=========cityName========" + cityName);
            System.out.println("calling this========stateName=========" + stateName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_location:
                getLocationName();
                break;
        }
    }
}
