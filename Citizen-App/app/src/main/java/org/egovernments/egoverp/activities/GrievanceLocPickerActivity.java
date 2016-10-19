/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.PinView;
import org.egovernments.egoverp.helper.map.CustomMapFragment;
import org.egovernments.egoverp.helper.map.MapWrapperLayout;
import org.egovernments.egoverp.network.SessionManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by egov on 22/9/16.
 */

public class GrievanceLocPickerActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
        MapWrapperLayout.OnDragListener, GoogleMap.OnMyLocationButtonClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    SessionManager sessionManager;
    PinView pinView;

    Handler addressUpdateHandler;
    GoogleMap googleMap;
    Geocoder geocoder;
    Runnable updateAddressRunnable;

    LocationManager locationManager;

    final private int REQUEST_CODE_ASK_PERMISSIONS_LOCATION = 123;

    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public static final String DEFAULT_LOCATION_LAT="defaultLat";
    public static final String DEFAULT_LOCATION_LNG="defaultLng";

    public static final String SELECTED_LOCATION_LAT="selectedLat";
    public static final String SELECTED_LOCATION_LNG="selectedLng";
    public static final String SELECTED_LOCATION_ADDRESS="selectedLocAddress";

    LatLng defaultLatLng=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_location);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationManager = (LocationManager)
                getApplicationContext().getSystemService(Context.LOCATION_SERVICE);



        pinView=(PinView)findViewById(R.id.pinmap);
        sessionManager=new SessionManager(getApplicationContext());

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(POLLING_FREQ);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        mGoogleApiClient = new GoogleApiClient.Builder(GrievanceLocPickerActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(getIntent().getDoubleExtra(DEFAULT_LOCATION_LAT, 0d) != 0d)
        {
            defaultLatLng=new LatLng(getIntent().getDoubleExtra(DEFAULT_LOCATION_LAT, 0),
                    getIntent().getDoubleExtra(DEFAULT_LOCATION_LNG, 0));
        }

        CustomMapFragment map = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        map.setOnDragListener(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        addressUpdateHandler=new Handler();
        updateAddressRunnable=null;

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(settingsIntent, REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkLocationPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Disable map toolbar
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        // Set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnCameraIdleListener(this);
        LatLng latLng = new LatLng(sessionManager.getCityLatitude(), sessionManager.getCityLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom((defaultLatLng==null?latLng:defaultLatLng), 16);
        googleMap.moveCamera(cameraUpdate);
        this.googleMap=googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grievancelocpicker_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_pickloc:
                if(googleMap!=null) {
                    Intent output = getIntent();
                    output.putExtra(SELECTED_LOCATION_LAT, googleMap.getCameraPosition().target.latitude);
                    output.putExtra(SELECTED_LOCATION_LNG, googleMap.getCameraPosition().target.longitude);
                    if(pinView!=null && !TextUtils.isEmpty(pinView.getAddressText()))
                    {
                        output.putExtra(SELECTED_LOCATION_ADDRESS, pinView.getAddressText());
                    }
                    setResult(Activity.RESULT_OK, output);
                    finish();
                }
                else {
                    Toast.makeText(this, "Please wait, Google map still loading...", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateAddress(final CameraPosition cameraPosition)
    {

        removeUpdateAddressRunningTasks();
        updateAddressRunnable=new Runnable() {
            @Override
            public void run() {
                if(!pinView.isDragging()) {
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 1);
                        if (addresses != null && addresses.size() > 0 && pinView!=null) {
                            String address = addresses.get(0).getAddressLine(0);
                            String city = addresses.get(0).getLocality();
                            /*String country = addresses.get(0).getCountryName();
                            String knownName = addresses.get(0).getFeatureName();*/
                            pinView.setAddressText(address + (city.equals("null")?"":"," + city));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        addressUpdateHandler.postDelayed(updateAddressRunnable, 200);
    }

    @Override
    public void onDrag(MotionEvent motionEvent) {

        final int ACTION_DRAGGING=2;
        final int ACTION_DRAGGED=1;

        if(googleMap==null)
        {
            return;
        }

        switch (motionEvent.getAction()) {
            case ACTION_DRAGGING:
                pinView.setDragging(true);
                break;
            case ACTION_DRAGGED:
                pinView.setDragging(false);
                updateAddress(googleMap.getCameraPosition());
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        removeUpdateAddressRunningTasks();
        pinView.setAddressText("Getting address");
        return false;
    }

    void removeUpdateAddressRunningTasks()
    {
        if(updateAddressRunnable!=null) {
            addressUpdateHandler.removeCallbacks(updateAddressRunnable);
            updateAddressRunnable=null;
        }
    }

    private void startLocationListener() {
        try {
            if(servicesAvailable()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
                googleMap.setMyLocationEnabled(true);
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    //removing location listener
    public void removeLocationListener() {
        try {
            if(mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(googleMap!=null && defaultLatLng==null && !sessionManager.isDemoMode())
        {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
            googleMap.moveCamera(cameraUpdate);
        }
        removeLocationListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ASK_PERMISSIONS_LOCATION && resultCode==RESULT_OK)
        {
            startLocationListener();
        }
    }

    @Override
    public void onCameraIdle() {
        if(!pinView.isDragging()){
            updateAddress(googleMap.getCameraPosition());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (Build.VERSION.SDK_INT < 23) {
            if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                startLocationListener();
            }
        } else {

            if (checkLocationPermission()) {
                startLocationListener();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean servicesAvailable() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            GoogleApiAvailability.getInstance().getErrorDialog(this,resultCode, 0).show();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startLocationListener();
                } else {
                    Toast.makeText(GrievanceLocPickerActivity.this, "You're disabled location access!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
