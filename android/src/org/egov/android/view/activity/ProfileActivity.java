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

package org.egov.android.view.activity;

import java.io.File;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.common.StorageManager;
import org.egov.android.listener.Event;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends BaseActivity {
    private String mobileNo = "";
    private String userName = "";
    private String mailId = "";
    private String gender = "";
    private String altContactNumber = "";
    private String dateOfBirth = "";
    private String panCardNumber = "";
    private String aadhaarCardNumber = "";

    /**
     * It is used to initialize an activity. An Activity is an application component that provides a
     * screen with which users can interact in order to do something, To initialize and set the
     * layout for the ProfileActivity.Set click listener to the edit icon. Here we have checked the
     * api level to set the layout. If api level is greater than 13, then call activity_profile
     * layout else call activity_lower_version_profile layout. activity_profile layout contains
     * EGovRoundedImageView component which is not supported in lower api levels.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
        if (apiLevel > 13) {
            setContentView(R.layout.activity_profile);
        } else {
            setContentView(R.layout.activity_lower_version_profile);
        }
        ((ImageView) findViewById(R.id.edit_icon)).setOnClickListener(this);
    }

    /**
     * Call the profile api when the activity is started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ApiController.getInstance().getProfile(this);
    }

    /**
     * Event triggered when clicking on the item having click listener. Clicking on edit icon
     * redirect to EditProfileActivity and pass the user informations through intent.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.edit_icon:
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("mailId", mailId);
                intent.putExtra("mobileNo", mobileNo);
                intent.putExtra("gender", gender);
                intent.putExtra("altContactNumber", altContactNumber);
                intent.putExtra("dateOfBirth", dateOfBirth);
                intent.putExtra("panCardNumber", panCardNumber);
                intent.putExtra("aadhaarCardNumber", aadhaarCardNumber);
                startActivity(intent);
                break;
        }
    }

    /**
     * The onResponse method will be invoked after the API call onResponse methods will contain the
     * response. If the response has status as 'success' then get the user informations from the
     * response and show it in layout. If the error is like 'Invalid access token' then redirect to
     * the login page.
     */
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();

        if (status.equalsIgnoreCase("success")) {
            try {
                JSONObject jo = new JSONObject(event.getData().getResponse().toString());
                String birth_date = "";
                userName = _getValue(jo, "name");
                mailId = _getValue(jo, "emailId");
                mobileNo = _getValue(jo, "mobileNumber");
                gender = (_getValue(jo, "gender") == "") ? "" : _getValue(jo, "gender").substring(
                        0, 1).toUpperCase()
                        + _getValue(jo, "gender").substring(1).toLowerCase();
                altContactNumber = _getValue(jo, "altContactNumber");
                birth_date = _getValue(jo, "dob");
                panCardNumber = _getValue(jo, "panCard");
                aadhaarCardNumber = _getValue(jo, "aadhaarCard");

                if (!birth_date.equals("")) {
                    String[] parts = birth_date.split("-");
                    dateOfBirth = parts[2] + "-" + parts[1] + "-" + parts[0];
                }
                ((TextView) findViewById(R.id.name)).setText(userName);
                ((TextView) findViewById(R.id.email)).setText(mailId);
                ((TextView) findViewById(R.id.mobile)).setText(mobileNo);
                ((TextView) findViewById(R.id.gender)).setText(gender);
                ((TextView) findViewById(R.id.alt_contact_num)).setText(altContactNumber);
                ((TextView) findViewById(R.id.dob)).setText(dateOfBirth);
                ((TextView) findViewById(R.id.pan)).setText(panCardNumber);
                ((TextView) findViewById(R.id.aadhaar)).setText(aadhaarCardNumber);

                StorageManager sm = new StorageManager();
                Object[] obj = sm.getStorageInfo();
                String profPath = obj[0].toString() + "/egovernments/profile";
                sm.mkdirs(profPath);
                File imgFile = new File(profPath + "/photo_" + mobileNo + ".jpg");
                if (imgFile.exists()) {
                    ImageView image = (ImageView) findViewById(R.id.profile_image);
                    image.setImageBitmap(_getBitmapImage(profPath + "/photo_" + mobileNo + ".jpg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (msg.matches(".*Invalid access token.*")) {
                showMessage("Session expired");
                startLoginActivity();
            } else {
                showMessage(msg);
            }
        }
    }

    /**
     * Function used to decode the file(for memory consumption) and return the bitmap to show it in
     * image view
     * 
     * @param path
     *            => image file path
     * @return bitmap
     */
    private Bitmap _getBitmapImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Function used to check whether the key value exists in the given json object. If the key
     * exist ,return the value from the json object else return empty string
     * 
     * @param jo
     *            => json object to check the key existence
     * @param key
     *            => name of the key to check
     * @return string
     */
    private String _getValue(JSONObject jo, String key) {
        String result = "";
        try {
            result = (jo.has(key)) ? jo.getString(key) : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
