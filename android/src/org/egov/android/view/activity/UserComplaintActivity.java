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
import org.egov.android.view.adapter.ComplaintAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserComplaintActivity extends Fragment implements IApiListener, OnItemClickListener,
        IActionListener {

    private ArrayList<Complaint> listItem = new ArrayList<Complaint>();
    private ComplaintAdapter adapter;
    private boolean isApiLoaded = false;
    private int apiLevel = 0;
    private int page = 1;

    /**
     * The onActivityCreated() is called after the onCreateView() method when activity is created.
     * Get the api level from the session
     * api level denotes the api versions of the android device
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
    }

    /**
     * This is used to call the api respect to the visible fragment.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && listItem.size() == 0 && !isApiLoaded) {
            ApiController.getInstance().getUserComplaints(this, page);
        } else if (isVisibleToUser && listItem.size() != 0) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * To set the layout for the UserComplaintActivity.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_complaints, container, false);
    }

    /**
     * Function called after getting response from api call to display the list
     * 
     * @param isPagination
     *            => flag to inform the adapter to show load more button
     */
    private void _displayListView(boolean isPagination) {
        ListView list = (ListView) getActivity().findViewById(R.id.user_complaint_list);
        list.setOnItemClickListener(this);
        adapter = new ComplaintAdapter(getActivity(), listItem, isPagination, false, apiLevel, this);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Function called after getting success api response to download the images under the complaints.
     * After downloading the images, the images will be updated in list
     * 
     * @param path
     *            => complaint folder path
     * @param jsonObj
     *            => contain complaint information
     */
    private void _addDownloadJobs(String path, JSONObject jsonObj) {
        JSONObject jo = null;
        try {
            int totalFiles = jsonObj.getInt("supportDocsSize");
            for (int i = 1; i <= totalFiles; i++) {
                jo = new JSONObject();
                jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                        + "/api/v1.0/complaint/" + jsonObj.getString("crn")
                        + "/downloadSupportDocument");
                jo.put("fileNo", i);
                jo.put("type", "complaint");
                jo.put("destPath", path + "/photo_" + i + ".jpg");
                SQLiteHelper.getInstance().execSQL(
                        "INSERT INTO jobs(data, status, type, triedCount) values ('"
                                + jo.toString() + "', 'waiting', 'download', 0)");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function used to check whether the key value exist in the given json object.If the key
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
    /**
     * The onResponse method will be invoked after the user complaints API call .
     * onResponse methods will contain the response.
     * If the response has a status as 'success' then,
     * we have checked whether the access token is valid or not.
     * If the access token is invalid, redirect to login page.
     * If the access token is valid 
     * createdDate,complainantName,detail,crn,status values are retrieved from the response object
     * and store it to the variable then these values are set to the all complaint layout.
     * then call the _addDownloadJobs method to display the complaint photo 
     * from the complaint photos directory on the storage device.
     * displays the user complaints list with the corresponding complaint image.
     * we have checked the pagination value.This value is retrieved from the api response 
     * if the value is true then 
     * load more option will be displayed below the user complaint list view.
     * 
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        String status = event.getData().getApiStatus().getStatus();
        String pagination = event.getData().getApiStatus().isPagination();
        String msg = event.getData().getApiStatus().getMessage();
        if (page == 1) {
            listItem = new ArrayList<Complaint>();
        }

        if (status.equalsIgnoreCase("success")) {
            isApiLoaded = true;

            if (listItem.size() > 5) {
                listItem.remove(listItem.size() - 1);
            }

            try {
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                Complaint item = null;
                if (ja.length() > 0) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        item = new Complaint();
                        item.setCreatedDate(_getValue(jo, "createdDate"));
                        item.setDetails(_getValue(jo, "detail"));
                        item.setComplaintId(_getValue(jo, "crn"));
                        item.setStatus(jo.getString("status"));
                        StorageManager sm = new StorageManager();
                        Object[] obj = sm.getStorageInfo();
                        String complaintFolderName = obj[0].toString()
                                + "/egovernments/complaints/" + jo.getString("crn");
                        File complaintFolder = new File(complaintFolderName);
                        if (!complaintFolder.exists()) {
                            item.setImagePath(complaintFolderName + File.separator + "photo_"
                                    + jo.getInt("supportDocsSize") + ".jpg");
                            sm.mkdirs(complaintFolderName);
                            _addDownloadJobs(complaintFolderName, jo);
                        } else {
                            item.setImagePath(complaintFolderName + File.separator + "photo_"
                                    + complaintFolder.listFiles().length + ".jpg");
                        }
                        listItem.add(item);
                    }
                    if (pagination.equals("true")) {
                        item = new Complaint();
                        listItem.add(item);
                    }
                    ServiceController.getInstance().startJobs();
                    _displayListView(pagination.equals("true"));
                } else if (listItem.size() == 0) {
                    ((TextView) getActivity().findViewById(R.id.user_errMsg))
                            .setVisibility(View.VISIBLE);
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
     * Function used to show a message in toast.
     * 
     * @param message
     */
    private void _showMsg(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }

    /**
     * Event triggered when clicking on an item in listview. Clicking on list item redirect to detail page
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Complaint complaint = listItem.get(position);
        Intent intent = new Intent(getActivity(), ComplaintDetailActivity.class);
        intent.putExtra("complaintId", complaint.getComplaintId());
        intent.putExtra("name", complaint.getCreatedBy());
        intent.putExtra("complaint_status", complaint.getStatus());
        startActivity(intent);
    }

    /**
     * Event triggered when clicking on load more in ComplaintAdapter to call api.
     */
    @Override
    public void actionPerformed(String tag, Object... value) {
        if (tag.equals("LOAD_MORE")) {
            page = page + 1;
            ApiController.getInstance().getUserComplaints(this, page);
        }
    }
}
