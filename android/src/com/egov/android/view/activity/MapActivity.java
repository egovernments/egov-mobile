package com.egov.android.view.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.egov.android.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends BaseFragmentActivity implements OnClickListener {

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
            Toast.makeText(getApplicationContext(), R.string.map_load_failure, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        LatLng coordinate = new LatLng(20.593684, 78.962880);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 4);
        googleMap.animateCamera(yourLocation);

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
            if (latitude == 0 && longitude == 0) {
                showMsg(_setMessage(R.string.location_empty));
                return;
            }
            List<Address> addresses;
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                String cityName = addresses.get(0).getAddressLine(0);
                Intent intent = new Intent();
                intent.putExtra("city_name", cityName);
                setResult(2, intent);
                finish();
            } else {
                showMsg(_setMessage(R.string.unknown_location));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String _setMessage(int id) {
        return getResources().getString(id);
    }

    private void showMsg(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.get_location:
                getLocationName();
                break;
        }
    }
}
