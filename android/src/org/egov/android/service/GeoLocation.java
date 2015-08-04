/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

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