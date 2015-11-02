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
import java.util.ArrayList;

import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.IApiListener;
import org.egov.android.common.StorageManager;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.listener.Event;
import org.egov.android.listener.IActionListener;
import org.egov.android.model.Complaint;
import org.egov.android.service.GeoLocation;
import org.egov.android.view.adapter.ComplaintAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AllComplaintActivity extends Fragment implements IApiListener, OnItemClickListener,
        OnItemSelectedListener, IActionListener {

    private int page = 1;
    private boolean toAppend = false;
    private boolean isApiLoaded = false;
    private boolean isUserVisible = false;
    private int spinnerSelectedPosition = 0;
    private int apiLevel = 0;
    private ComplaintAdapter adapter = null;
    private ArrayList<Complaint> listItem = new ArrayList<Complaint>();
    private Spinner spinner = null;

    /**
     * When activity is created, the onActivityCreated() is called after the onCreateView() method .
     * Create an instance of GeoLocation to know whether the GPS/Location is on/off. Set the spinner
     * data as latest and near by. If the GPS/Location is in off mode then call _showSettingsAlert
     * function.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new GeoLocation(getActivity());
        String[] items = { "Latest", "Near By" };
        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_list_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(dataAdapter);

        if (!GeoLocation.getGpsStatus()) {
            _showSettingsAlert();
        }
    }

    /**
     * This is used to call the api respect to the visible fragment.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isUserVisible = isVisibleToUser;

        if (isVisibleToUser && listItem.size() == 0 && !isApiLoaded) {
            ApiController.getInstance().getLatestComplaints(this, 1);
        } else if (isVisibleToUser && listItem.size() > 0) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * To set the layout for the AllComplaintActivity. Here we have checked the api level to set the
     * layout. If api level greater than 13 then set activity_all_complaints layout else set
     * activity_lower_version_all_complaints layout. activity_all_complaints layout contains
     * EGovRoundedImageView component is the custom image view to show the image in a circle, which
     * is not supported in lower api levels.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
        if (apiLevel > 13) {
            return inflater.inflate(R.layout.activity_all_complaints, container, false);
        } else {
            return inflater.inflate(R.layout.activity_lower_version_all_complaints, container,
                    false);
        }
    }

    /**
     * Function called if the user didnt enable GPS/Location in their device. Give options to enable
     * GPS/Location and cancel the pop up.
     */
    public void _showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Settings");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
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
     * Function called after getting response from api call to display the list
     * 
     * @param isPagination
     *            => flag to inform the adapter to show load more button
     */
    private void _displayListView(boolean isPagination) {
        ListView list = (ListView) getActivity().findViewById(R.id.all_complaint_list);
        adapter = new ComplaintAdapter(getActivity(), listItem, isPagination, "allcomplaint",
                apiLevel, this);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Function called after getting success api response to download the images under the
     * complaints After downloading the images, the images will be updated in the list
     * 
     * @param path
     *            => complaint folder path
     * @param jsonObj
     *            => contains complaint information
     */
    private void _addDownloadJobs(String path, JSONObject jsonObj) {
        JSONObject jo = null;
        try {
            int totalFiles = jsonObj.getInt("supportDocsSize");
            if (totalFiles == 0) {
                jo = new JSONObject();
                jo.put("url",
                        AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                                + "/pgr/resources/images/complaintType/"
                                + jsonObj.getString("complaintTypeImage"));
                jo.put("type", "complaintType");
                jo.put("destPath", path + "/photo_complaint_type.jpg");
                SQLiteHelper.getInstance().execSQL(
                        "INSERT INTO tbl_jobs(data, status, type, triedCount) values ('"
                                + jo.toString() + "', 'waiting', 'download', 0)");
            } else {
                for (int i = 1; i <= totalFiles; i++) {
                    jo = new JSONObject();
                    jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                            + "/api/v1.0/complaint/" + jsonObj.getString("crn")
                            + "/downloadSupportDocument");
                    jo.put("fileNo", i);
                    jo.put("type", "complaint");
                    jo.put("destPath", path + "/photo_" + i + ".jpg");
                    SQLiteHelper.getInstance().execSQL(
                            "INSERT INTO tbl_jobs(data, status, type, triedCount) values ('"
                                    + jo.toString() + "', 'waiting', 'download', 0)");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function used to check whether the key value is existing in the given json object. If the key
     * exists return the value from the json object else return empty string
     * 
     * @param jo
     *            => json object where to check the key exist
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

    /**
     * The onResponse method will be invoked after the all complaint API call. onResponse methods
     * will contain the response.If the response has a status as 'success' then we have checked
     * whether the access token is valid or not.If the access token is invalid, redirect to login
     * page. If the access token is valid, the response contains the JSON object.
     * createdDate,complainantName,detail,crn,status values are retrieved from the response object
     * and store it to the variable then these values are set to the all complaint layout. call the
     * _addDownloadJobs method to display the complaint photo from the complaint photos directory on
     * the storage device. displays the All complaints list with the corresponding complaint image.
     * we have checked the pagination value.This value is retrieved from the api response if the
     * value is true then load more option will be displayed below the complaint list view.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        String status = event.getData().getApiStatus().getStatus();
        String pagination = event.getData().getApiStatus().isPagination();
        String msg = event.getData().getApiStatus().getMessage();
        final ListView listView = (ListView) getActivity().findViewById(R.id.all_complaint_list);
        if (!toAppend) {
            listItem = new ArrayList<Complaint>();
        }
        if (status.equalsIgnoreCase("success")) {
            try {
                if (listItem.size() > 5) {
                    listItem.remove(listItem.size() - 1);
                }
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                Complaint item = null;
                isApiLoaded = true;
                if (ja.length() > 0) {
                    ((TextView) getActivity().findViewById(R.id.all_errMsg))
                            .setVisibility(View.GONE);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        item = new Complaint();
                        String userName = _getValue(jo, "complainantName");
                        item.setCreatedDate(_getValue(jo, "createdDate"));
                        item.setCreatedBy((userName.equals("")) ? _getValue(jo, "lastModifiedBy")
                                : userName);
                        item.setDetails(_getValue(jo, "detail"));
                        item.setComplaintId(jo.getString("crn"));
                        item.setStatus(jo.getString("status"));
                        StorageManager sm = new StorageManager();
                        Object[] obj = sm.getStorageInfo();
                        String complaintFolderName = obj[0].toString()
                                + "/egovernments/complaints/" + jo.getString("crn");

                        File complaintFolder = new File(complaintFolderName);
                        if (!complaintFolder.exists()) {
                            if (jo.getInt("supportDocsSize") == 0) {
                                item.setImagePath(complaintFolderName + File.separator
                                        + "photo_complaint_type.jpg");
                            } else {
                                item.setImagePath(complaintFolderName + File.separator + "photo_"
                                        + jo.getInt("supportDocsSize") + ".jpg");
                            }
                            sm.mkdirs(complaintFolderName);
                            _addDownloadJobs(complaintFolderName, jo);
                        } else {
                            item.setImagePath(complaintFolderName + File.separator + "photo_"
                                    + complaintFolder.listFiles().length + ".jpg");
                        }
                        listItem.add(item);
                    }

                    if (listItem.size() > 5) {
                        listView.postDelayed(new Runnable() {
                            public void run() {
                                listView.setStackFromBottom(true);
                                listView.setSelection(listItem.size() - 8);
                            }
                        }, 0);
                    }

                    if (pagination.equals("true")) {
                        item = new Complaint();
                        listItem.add(item);
                    }
                    ServiceController.getInstance().startJobs();
                    _displayListView(pagination.equals("true"));
                } else if (listItem.size() == 0) {
                    ((TextView) getActivity().findViewById(R.id.all_errMsg))
                            .setVisibility(View.VISIBLE);
                    _displayListView(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (msg.matches(".*Invalid access token.*")) {
                _showMsg("Session expired");
                AndroidLibrary.getInstance().getSession().edit().putString("access_token", "")
                        .commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            } else {
                page = (page > 1) ? page - 1 : 1;
                _showMsg(msg);
            }
        }
    }

    /**
     * Function used to show toast
     * 
     * @param message
     *            => Message to be shown
     */
    private void _showMsg(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }

    /**
     * Event triggered when clicking on an item in listview. Clicking on list item redirects to
     * detail page
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listItem.size() > position) {
            Complaint complaint = listItem.get(position);
            Intent intent = new Intent(getActivity(), ComplaintDetailActivity.class);
            intent.putExtra("complaintId", complaint.getComplaintId());
            intent.putExtra("name", complaint.getCreatedBy());
            intent.putExtra("complaint_status", complaint.getStatus());
            startActivity(intent);
        }
    }

    /**
     * Event triggered when choosing an item from spinner component. If the position is 0, then call
     * latest complaint api else call near by complaint api
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
        spinnerSelectedPosition = position;
        if (!isUserVisible) {
            return;
        }
        if (position == 0) {
            page = 1;
            toAppend = false;
            ApiController.getInstance().getLatestComplaints(this, page);
        } else {
            double lat = GeoLocation.getLatitude();
            double lng = GeoLocation.getLongitude();
            int distance = 5000;
            toAppend = false;
            page = 1;
            ApiController.getInstance().getNearByComplaints(this, page, lat, lng, distance);
        }
        ((ListView) getActivity().findViewById(R.id.all_complaint_list)).setStackFromBottom(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    /**
     * Event triggered When clicking on load more in ComplaintAdapter to call api.
     */
    @Override
    public void actionPerformed(String tag, Object... value) {
        if (tag.equals("LOAD_MORE")) {
            toAppend = true;
            page = page + 1;
            if (spinnerSelectedPosition == 0) {
                ApiController.getInstance().getLatestComplaints(this, page);
            } else {
                double lat = GeoLocation.getLatitude();
                double lng = GeoLocation.getLongitude();
                int distance = 5000;
                ApiController.getInstance().getNearByComplaints(this, page, lat, lng, distance);
            }
        }
    }
}