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

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.common.StorageManager;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.listener.Event;
import org.egov.android.model.Complaint;
import org.egov.android.view.adapter.ComplaintAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends BaseActivity implements OnItemClickListener,
        OnEditorActionListener {
    private ArrayList<Complaint> listItem = null;
    private ComplaintAdapter adapter;
    private int apiLevel = 0;

    /**
     * To set the layout for the SearchActivity .Set click listener to the search icon and editor
     * action listener to search EditText.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ((EditText) findViewById(R.id.search)).setOnEditorActionListener(this);
        ((ImageView) findViewById(R.id.search_icon)).setOnClickListener(this);

        apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
    }

    /**
     * Event triggered when clicking on the item having click listener.On Clicking search icon
     * _getSearchList() function get called.
     */
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search_icon:
                _getSearchList();
                break;
        }
    }

    /**
     * Function called after getting response from search complaint api to display the search list
     */
    private void _displayListView() {
        ListView list = (ListView) findViewById(R.id.search_list);
        adapter = new ComplaintAdapter(this, listItem, false, true, apiLevel, null);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

    /**
     * Function called after getting search complaint api response to download the image of the
     * complaint. After downloading the images, the images will be updated in the list
     * 
     * @param path
     *            => complaint folder path
     * @param crn
     *            => contain complaint crn number
     */
    private void _addDownloadJobs(String path, String crn) {
        JSONObject jo = null;
        try {
            jo = new JSONObject();
            jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                    + "/api/v1.0/complaint/" + crn + "/downloadSupportDocument");
            jo.put("type", "complaintSearch");
            jo.put("destPath", path + "/photo_" + crn + ".jpg");
            SQLiteHelper.getInstance().execSQL(
                    "INSERT INTO jobs(data, status, type, triedCount) values ('" + jo.toString()
                            + "', 'waiting', 'download', 0)");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search complaint api response handler. Here we have checked the invalid access token error to
     * redirect to login page.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        if (status.equalsIgnoreCase("success")) {
            try {
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                listItem = new ArrayList<Complaint>();
                Complaint item = null;
                if (ja.length() > 0) {
                    StorageManager sm = new StorageManager();
                    Object[] obj = sm.getStorageInfo();
                    ((TextView) findViewById(R.id.search_errMsg)).setVisibility(View.GONE);
                    for (int i = 0; i < ja.length(); i++) {
                        String complaintNo = "";
                        JSONObject data = ja.getJSONObject(i).getJSONObject("resource");
                        JSONObject searchObj = data.getJSONObject("searchable");
                        JSONObject commonObj = data.getJSONObject("common")
                                .getJSONObject("citizen");
                        JSONObject statusObj = data.getJSONObject("clauses")
                                .getJSONObject("status");

                        if (data.getJSONObject("clauses").has("crn")) {
                            complaintNo = data.getJSONObject("clauses").getString("crn");
                        } else {
                            complaintNo = searchObj.getString("crn");
                        }
                        item = new Complaint();
                        item.setCreatedDate(data.getJSONObject("common").getString("createdDate"));
                        item.setDetails(searchObj.getString("details"));
                        item.setComplaintId(complaintNo);
                        if (commonObj.has("name")) {
                            item.setCreatedBy(commonObj.getString("name"));
                        } else {
                            item.setCreatedBy("");
                        }
                        item.setStatus(statusObj.getString("name"));
                        String complaintFolderName = obj[0].toString()
                                + "/egovernments/search/complaints/" + complaintNo;
                        item.setImagePath(complaintFolderName + File.separator + "photo_"
                                + complaintNo + ".jpg");
                        sm.mkdirs(complaintFolderName);
                        _addDownloadJobs(complaintFolderName, complaintNo);
                        listItem.add(item);
                    }
                    ServiceController.getInstance().startJobs();
                } else {
                    ((TextView) findViewById(R.id.search_errMsg)).setVisibility(View.VISIBLE);
                }
                _displayListView();
                adapter.notifyDataSetChanged();
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
     * Event triggered when clicking on an item in listview. Clicking on list item redirect to detail page
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(this, ComplaintDetailActivity.class);
        intent.putExtra("complaintId", listItem.get(position).getComplaintId());
        intent.putExtra("name", listItem.get(position).getCreatedBy());
        intent.putExtra("complaint_status", listItem.get(position).getStatus());
        startActivity(intent);
    }

    /**
     * Event triggered when pressing enter/done key,call _getSearchList() function
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
            _getSearchList();
        }
        return false;
    }

    /**
     * Function called when searching the complaints. If the search text field is empty or less than 3
     * characters then show the error message. Otherwise call the search list api by search text.
     */
    private void _getSearchList() {
        String searchText = ((EditText) findViewById(R.id.search)).getText().toString().trim();

        if (searchText.equals("")) {
            showMessage(getMessage(R.string.search_empty));
        } else if (searchText.length() < 3) {
            showMessage(getMessage(R.string.search_length));
        } else {
            ApiController.getInstance().getSearchComplaints(this, searchText);
        }
    }
}
