package org.egov.android.service;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class GeoLocation implements LocationListener {

    public static String Tag = "Geolocation";
    private static LocationManager locationManager;
    private static Context context;
    private String provider;
    private static boolean gpsStatus = false;
    private Location location;
    private static double latitude;
    private static double longitude;

    public GeoLocation(Context ctx) {
        context = ctx;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsStatus = false;
            return;
        } else {
            gpsStatus = true;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 5000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    /**
     * To update the latitude and longitude values
     * 
     * @param location
     */
    private void updateLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            latitude = 0;
            longitude = 0;
        }
    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static boolean getGpsStatus() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsStatus = false;

        } else {
            gpsStatus = true;
        }
        return gpsStatus;
    }

    @Override
    public void onProviderDisabled(String provider) {
        updateLocation(null);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                if (location == null || location.getProvider().equals(provider)) {
                    location = null;
                }
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                if (location == null || location.getProvider().equals(provider)) {
                }
                break;
            case LocationProvider.AVAILABLE:

                break;
        }
    }
}