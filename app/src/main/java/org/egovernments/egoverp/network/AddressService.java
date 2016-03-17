/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.network;


import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import org.egovernments.egoverp.events.AddressReadyEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Service resolves lat/lng data to address in background when
 **/

public class AddressService extends IntentService {

    public static final String LAT = "LAT";
    public static final String LNG = "LNG";

    public static String addressResult = "";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AddressService() {
        super("Default");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        // Get the location passed to this service through an extra.
        Double lat = intent.getDoubleExtra(LAT, 0);
        Double lng = intent.getDoubleExtra(LNG, 0);


        List<Address> addresses = null;

        Geocoder geocoder = new Geocoder(getApplicationContext());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException | IllegalArgumentException ioException) {
            // Catch network or other I/O problems.
            ioException.printStackTrace();
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            Log.e("Address null", "");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            addressResult = TextUtils.join(System.getProperty("line.separator"), addressFragments);
            //Post event on success so that all relevant subscribers can react
            EventBus.getDefault().post(new AddressReadyEvent());
        }
    }
}
