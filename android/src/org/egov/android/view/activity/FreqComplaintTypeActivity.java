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

import java.util.ArrayList;

import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.ApiUrl;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.listener.Event;
import org.egov.android.model.ComplaintType;
import org.egov.android.view.adapter.ComplaintTypeAdapter;
import org.egov.android.view.adapter.GridViewAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class FreqComplaintTypeActivity extends BaseActivity implements OnItemClickListener,
        Runnable {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<ComplaintType> listItem = new ArrayList<ComplaintType>();

    /**
     * It is used to initialize an activity. An Activity is an application component that provides a
     * screen with which users can interact in order to do something, To initialize and set the
     * layout for the FreqComplaintTypeActivity. Set click listener to the all complaint type text .
     * Call the frequent complaint type api to load data in grid view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_category);
        ((RelativeLayout) findViewById(R.id.all_category)).setOnClickListener(this);
        ApiController.getInstance().getFreqComplaintTypes(this);
    }

    /**
     * Event triggered when clicking on the item having click listener. When clicking on all
     * complaint types text, go to AllComplaintTypeActivity.
     */
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.all_category:
                startActivity(new Intent(this, AllComplaintTypeActivity.class));
                break;
        }
    }

    /**
     * Function called after getting success api response to download the frequent complaint type
     * images. After downloading the images, the images will be updated in grid view
     * 
     * @param path
     *            => complaint type folder path
     * @param jsonObj
     *            => contain complaint type information
     */
    private void _addDownloadJobs(String path, JSONObject jsonObj) {
        try {
            JSONObject jo = new JSONObject();
            jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                    + "/pgr/resources/images/complaintType/" + jsonObj.getString("typeImage"));
            jo.put("type", "complaintType");
            jo.put("destPath", path + "/" + jsonObj.getString("typeImage"));
            SQLiteHelper.getInstance().execSQL(
                    "INSERT INTO tbl_jobs(data, status, type, triedCount) values ('" + jo.toString()
                            + "', 'waiting', 'download', 0)");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Event triggered when clicking on an item in listview. Clicking on list item, redirect to
     * create complaint page
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        ComplaintType ct = listItem.get(position);
        Intent intent = new Intent(this, CreateComplaintActivity.class);
        intent.putExtra("complaintTypeName", ct.getName());
        intent.putExtra("complaintTypeId", ct.getId());
        startActivity(intent);
    }

    /**
     * Function called after getting response from all complaint type api to display the complaint
     * types.
     */
    private void _displayListView() {
        ListView list = (ListView) findViewById(R.id.all_category_list);
        list.setVisibility(View.VISIBLE);
        ComplaintTypeAdapter adapter = new ComplaintTypeAdapter(this, listItem);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

    /**
     * The onResponse method will be invoked after the Frequent complaint type API call onResponse
     * methods will contain the response. If the response has status as 'success' then get the
     * complaint type from the response and show it in layout. If there is no frequent complaint
     * types then show all complaint types. If the error is like 'Invalid access token' then
     * redirect to the login page.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);

        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();

        if (status.equalsIgnoreCase("success")) {
            if (event.getData().getApiMethod().getApiUrl().getUrl()
                    .equals(ApiUrl.GET_FREQ_COMPLAINT_TYPES.getUrl())) {
                try {
                    JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                    ComplaintType cat = null;
                    JSONObject jo;

                    if (ja.length() == 0) {
                        ApiController.getInstance().getComplaintTypes(this);
                        return;
                    }
                    ((LinearLayout) findViewById(R.id.frequentTypes)).setVisibility(View.VISIBLE);
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        cat = new ComplaintType();
                        cat.setName(jo.getString("name"));
                        cat.setId(jo.getInt("id"));
                        listItem.add(cat);
                        
                        /*StorageManager sm = new StorageManager();
                        Object[] obj = sm.getStorageInfo();
                        String folderName = obj[0].toString() + "/egovernments/complaint_type";
                        cat.setImagePath(folderName + File.separator + jo.getString("typeImage"));
                        
                        sm.mkdirs(folderName);
                        if (!new File(folderName + File.separator + jo.getString("typeImage"))
                                .exists()) {
                            _addDownloadJobs(folderName, jo);
                        }*/
                    }

                    ServiceController.getInstance().startJobs();
                    gridView = (GridView) findViewById(R.id.gridView);
                    gridAdapter = new GridViewAdapter(this, listItem);
                    gridView.setAdapter(gridAdapter);
                    gridView.setOnItemClickListener(this);
                    new Handler().postDelayed(this, 10000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (event.getData().getApiMethod().getApiUrl().getUrl()
                    .equals(ApiUrl.GET_COMPLAINT_TYPES.getUrl())) {
                try {
                    JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                    listItem = new ArrayList<ComplaintType>();
                    ComplaintType item = null;
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        item = new ComplaintType();
                        item.setName(jo.getString("name"));
                        item.setId(jo.getInt("id"));
                        listItem.add(item);
                    }
                    _displayListView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
     * To refresh the frequent complaint type image after 10000 milliseconds from the response time.
     */
    @Override
    public void run() {
        if (listItem.size() > 0) {
            gridAdapter.notifyDataSetChanged();
        }
    }
}
