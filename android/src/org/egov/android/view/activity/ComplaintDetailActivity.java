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

import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.api.ApiResponse;
import org.egov.android.common.StorageManager;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.listener.Event;
import org.egov.android.service.GeoLocation;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ComplaintDetailActivity extends BaseActivity {

    private String id = "";
    private Spinner statusSpinner;
    private LinearLayout imageCntainer = null;
    private String complaintTypeName = "";
    private String complaintStatus = "";
    private String createdDate = "";
    private String lastModifiedDate = "";
    private String complaintFolderName = "";
    private boolean isComplaintDetail = true;

    /**
     * It is used to initialize an activity. An Activity is an application component that provides a
     * screen with which users can interact in order to do something, To initialize the
     * ComplaintDetailActivity. Set click listener to the status summary button and change status
     * button. call the complaint detail api
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

        imageCntainer = (LinearLayout) findViewById(R.id.complaint_image_container);
        String[] items = { "Select", "Withdrawn" };

        int apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
        if (apiLevel > 13) {
            statusSpinner = (Spinner) findViewById(R.id.status_spinner);
        } else {
            statusSpinner = (Spinner) findViewById(R.id.status_normal_spinner);
        }
        statusSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_list_item);
        statusSpinner.setAdapter(dataAdapter);

        ((Button) findViewById(R.id.status_summary)).setOnClickListener(this);
        ((Button) findViewById(R.id.complaint_changeStatus)).setOnClickListener(this);

        id = getIntent().getExtras().getString("complaintId");
        String name = getIntent().getExtras().getString("name");
        String status = getIntent().getExtras().getString("complaint_status");

        if (name.equals("") && !status.equalsIgnoreCase("withdrawn")) {
            ((LinearLayout) findViewById(R.id.action_container)).setVisibility(View.VISIBLE);
        }

        StorageManager sm = new StorageManager();
        Object[] obj = sm.getStorageInfo();
        complaintFolderName = obj[0].toString() + "/egovernments/complaints/" + id;
        _showComplaintImages();
        isComplaintDetail = true;
        new GeoLocation(this);
        if (!GeoLocation.getGpsStatus()) {
            _showSettingsAlert();
        }
        ApiController.getInstance().getComplaintDetail(this, id);
    }

    /**
     * Function called if the user didn't enable GPS/Location in their device. Give options to enable
     * GPS/Location and cancel the pop up.
     */
    public void _showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Settings");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    /**
     * Event triggered when clicking on the item having click listener. When clicking on status
     * summary, the complaintId,complaintTypeName,status,created_date,lastModifiedDate values are
     * stored in an Intent of StatusSummaryActivity. When clicking on change status, the
     * _changeStatus() will be called
     */

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.status_summary:
                Intent intent = new Intent(this, StatusSummaryActivity.class);
                intent.putExtra("complaintId", id);
                intent.putExtra("complaintTypeName", complaintTypeName);
                intent.putExtra("status", complaintStatus);
                intent.putExtra("created_date", createdDate);
                intent.putExtra("lastModifiedDate", lastModifiedDate);
                startActivity(intent);
                break;
            case R.id.complaint_changeStatus:
                _changeStatus();
                break;
        }
    }

    /**
     * Function called when clicking on change status call the complaintChangeStatus api to change
     * the complaint status
     */
    @SuppressLint("DefaultLocale")
    private void _changeStatus() {
        int pos = statusSpinner.getSelectedItemPosition();
        if (pos == 0) {
            showMessage("Please select a status");
        } else {
            String status = statusSpinner.getSelectedItem().toString();
            String message = ((EditText) findViewById(R.id.message)).getText().toString();
            ApiController.getInstance().complaintChangeStatus(this, id, status.toUpperCase(),
                    message);
        }
    }

    /**
     * Function called when the activity was created to show the images attached with the complaints
     * Get the files from the complaint folder path and show that images in horizontal scroll view
     */
    private void _showComplaintImages() {
        File folder = new File(complaintFolderName);
        if (!folder.exists()) {
            return;
        }
        File[] listOfFiles = folder.listFiles();
        ImageView image;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                Log.d("EGOV_JOB", "File path" + complaintFolderName + File.separator
                        + listOfFiles[i].getName());
                image = new ImageView(this);
                Bitmap bmp = _getBitmapImage(complaintFolderName + File.separator
                        + listOfFiles[i].getName());
                image.setImageBitmap(bmp);
                LinearLayout.LayoutParams inner_container_params = new LinearLayout.LayoutParams(
                        _dpToPix(80), _dpToPix(80));

                image.setLayoutParams(inner_container_params);
                image.setPadding(0, 0, 5, 0);
                imageCntainer.addView(image);
            }
        }
    }

    /**
     * Function used to decode the file and return the bitmap to show it in image view
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
     * Function used to convert dp unit to pixel unit
     * 
     * @param value
     *            => dp value
     * @return pixel value
     */
    private int _dpToPix(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources()
                .getDisplayMetrics());
    }

    /**
     * Function called after getting success api response to download the images under the
     * complaints After downloading the images, the images will be updated in detail screen
     * 
     * @param totalFiles
     *            => total files attached in a complaint
     */
    private void _addDownloadJobs(int totalFiles) {
        JSONObject jo = null;
        try {
            for (int i = 1; i <= totalFiles; i++) {
                jo = new JSONObject();
                jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                        + "/api/v1.0/complaint/" + id + "/downloadSupportDocument");
                jo.put("fileNo", i);
                jo.put("type", "complaint");
                jo.put("destPath", complaintFolderName + "/photo_" + i + ".jpg");
                SQLiteHelper.getInstance().execSQL(
                        "INSERT INTO tbl_jobs(data, status, type, triedCount) values ('"
                                + jo.toString() + "', 'waiting', 'download', 0)");
            }
            ServiceController.getInstance().startJobs();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * onResponse methods will contain the response If the response has a status as 'success' then
     * We have checked whether the access token is valid or not. If the access token is invalid,
     * redirect to login page. If access token is valid the retrieve the
     * crn,complaintType,details,landmarkDetails,complaintStatus values from the JSON Object and
     * store it to the variables, then display the
     * crn,complaintType,details,landmarkDetails,complaintStatus values to the corresponding UI
     * field of complaint detail page ui. location name coordinate is retrieved from the api
     * response and Geocoder is used to transforming a (latitude, longitude) coordinate into a
     * location name and location name is displayed to the corresponding UI.
     * 
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        if (status.equalsIgnoreCase("success")) {
            if (isComplaintDetail) {
                try {
                    isComplaintDetail = false;
                    JSONObject jo = new JSONObject(event.getData().getResponse().toString());

                    complaintStatus = _getValue(jo, "status");
                    createdDate = _getValue(jo, "createdDate");
                    lastModifiedDate = _getValue(jo, "lastModifiedDate");
                    complaintTypeName = _getValue(jo, "complaintTypeName");

                    ((TextView) findViewById(R.id.crn)).setText(_getValue(jo, "crn"));
                    ((TextView) findViewById(R.id.complaintType)).setText(complaintTypeName);
                    ((TextView) findViewById(R.id.details)).setText(_getValue(jo, "detail"));
                    ((TextView) findViewById(R.id.landmarkDetails)).setText(_getValue(jo,
                            "landmarkDetails"));
                    ((TextView) findViewById(R.id.complaintStatus)).setText(complaintStatus
                            .toLowerCase());

                    if (jo.has("locationName")) {
                        ((TextView) findViewById(R.id.location)).setText(_getValue(jo,
                                "locationName"));
                    } else {
                        String cityName = GeoLocation.getCurrentLocation(jo.getDouble("lat"),
                                jo.getDouble("lng"));
                        ((TextView) findViewById(R.id.location)).setText(cityName);
                    }
                    File complaintFolder = new File(complaintFolderName);
                    if (!complaintFolder.exists()) {
                        new StorageManager().mkdirs(complaintFolderName);
                        _addDownloadJobs(jo.getInt("supportDocsSize"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showMessage(msg);
                Intent intent = new Intent(this, ComplaintActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
     * Function used to check whether the key value is existing in the given json object. If the key
     * exists return the value from the json object else return empty string
     * 
     * @param jo
     *            => json object to check the key existance
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
