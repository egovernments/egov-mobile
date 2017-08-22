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

package org.egov.employee.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.egov.employee.controls.PinView;
import org.egov.employee.controls.map.CustomMapFragment;
import org.egov.employee.controls.map.MapWrapperLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import offices.org.egov.egovemployees.R;

/**
 *  Grievance Location Picker Screen From Google Maps
 */

public class GrievanceLocPickerActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
        MapWrapperLayout.OnDragListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    public static final String DEFAULT_LOCATION_LAT = "defaultLat";
    public static final String DEFAULT_LOCATION_LNG = "defaultLng";
    public static final String SELECTED_LOCATION_LAT = "selectedLat";
    public static final String SELECTED_LOCATION_LNG = "selectedLng";
    public static final String SELECTED_LOCATION_ADDRESS = "selectedLocAddress";
    private static final String TAG = "GRIEVANCE_LOC_PICKER";
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    final private int REQUEST_CODE_ASK_PERMISSIONS_LOCATION = 123;
    PinView pinView;
    Handler addressUpdateHandler;
    GoogleMap googleMap;
    Geocoder geocoder;
    Runnable updateAddressRunnable;
    LocationManager locationManager;
    LatLng defaultLatLng = null, choosenLatLng = null;
    View mapView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Boolean isLocationChoosen = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableBackButton();

        setStatusBarTranslucent(true);

        locationManager = (LocationManager)
                getApplicationContext().getSystemService(Context.LOCATION_SERVICE);



        pinView=(PinView)findViewById(R.id.pinmap);

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
            isLocationChoosen = true;
            defaultLatLng=new LatLng(getIntent().getDoubleExtra(DEFAULT_LOCATION_LAT, 0),
                    getIntent().getDoubleExtra(DEFAULT_LOCATION_LNG, 0));
        }

        CustomMapFragment map = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        map.setOnDragListener(this);
        mapView = map.getView();
        geocoder = new Geocoder(this, Locale.getDefault());
        addressUpdateHandler=new Handler();
        updateAddressRunnable=null;

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
       /*
        * The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
        * set a filter returning only results with a precise address.
        */

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (googleMap != null) {
                    choosenLatLng = place.getLatLng();
                    setLocation(choosenLatLng);
                }
            }

            @Override
            public void onError(Status status) {

            }
        });

        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_pick)).setOnClickListener(this);


    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        View v = findViewById(R.id.titleView);
        if (v != null) {
            int paddingTop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? statusBarHeight(getResources()) : 0;
            v.setPadding(0, paddingTop, 0, 0);
        }

        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    int statusBarHeight(android.content.res.Resources res) {
        return (int) (24 * res.getDisplayMetrics().density);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_grievance_location;
    }

    private void buildAlertMessageNoGps() {
        if (!isFinishing()) {
            displayLocationSettingsRequest(GrievanceLocPickerActivity.this);
        }
    }

    public void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        startLocationListener();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(GrievanceLocPickerActivity.this, REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Disable map toolbar
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        // Set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnCameraIdleListener(this);
        LatLng latLng = new LatLng(preference.getActiveCityLat(), preference.getActiveCityLat());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom((defaultLatLng==null?latLng:defaultLatLng), 16);
        googleMap.moveCamera(cameraUpdate);

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 350, 30, 30);
        }

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
                    new SetAddressToPinViewFromCameraPosition().execute(cameraPosition);
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
        if(googleMap!=null && defaultLatLng==null)
        {
            if (choosenLatLng == null)
                choosenLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            setLocation(choosenLatLng);
        }
        removeLocationListener();
    }

    private void setLocation(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        googleMap.moveCamera(cameraUpdate);
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
                if (mGoogleApiClient.isConnected())
                    startLocationListener();
                else
                    mGoogleApiClient.connect();
        }
    }

    @Override
    public void onCameraIdle() {
        if(!pinView.isDragging()){
            if (isLocationChoosen)
                choosenLatLng = new LatLng(googleMap.getCameraPosition().target.latitude,
                        googleMap.getCameraPosition().target.longitude);
            else
                isLocationChoosen = true;

            updateAddress(googleMap.getCameraPosition());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            checkAndStartLocationManager();
        } else {
            if (checkLocationPermission()) {
                checkAndStartLocationManager();
            }
        }
    }

    public void checkAndStartLocationManager()
    {
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            startLocationListener();
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if(mGoogleApiClient.isConnected())
                        checkAndStartLocationManager();
                    else
                        mGoogleApiClient.connect();
                } else {
                    finish();
                }
                break;
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

        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_pick:
                if (googleMap != null) {
                    Intent output = getIntent();
                    output.putExtra(SELECTED_LOCATION_LAT, googleMap.getCameraPosition().target.latitude);
                    output.putExtra(SELECTED_LOCATION_LNG, googleMap.getCameraPosition().target.longitude);
                    if (pinView != null && !TextUtils.isEmpty(pinView.getAddressText())) {
                        output.putExtra(SELECTED_LOCATION_ADDRESS, pinView.getAddressText());
                    }
                    setResult(Activity.RESULT_OK, output);
                    finish();
                } else {
                    Toast.makeText(this, R.string.gmap_loading, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class SetAddressToPinViewFromCameraPosition extends AsyncTask<CameraPosition, String, String> {

            List<Address> addresses;
            CameraPosition cameraPosition;

            @Override
            protected String doInBackground(CameraPosition... params) {
            try {
                cameraPosition=params[0];
                addresses = geocoder.getFromLocation(params[0].target.latitude, params[0].target.longitude, 1);
                if (addresses != null && addresses.size() > 0 && pinView!=null) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = (TextUtils.isEmpty(addresses.get(0).getLocality())?"":addresses.get(0).getLocality());
                                    /*String country = addresses.get(0).getCountryName();
                                    String knownName = addresses.get(0).getFeatureName();*/
                    return address + (city.equals("null")?"":"," + city);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(pinView!=null && !pinView.isDragging()) {
                if (TextUtils.isEmpty(response))
                    new SetAddressToPinViewFromCameraPosition().execute(cameraPosition);
                else
                    pinView.setAddressText(response);
            }
        }
    }

}
