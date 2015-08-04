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

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.IApiListener;
import org.egov.android.listener.Event;
import org.egov.android.model.ComplaintType;
import org.egov.android.view.adapter.ComplaintTypeAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AllComplaintTypeActivity extends BaseActivity implements IApiListener,
        OnItemClickListener {

    private ArrayList<ComplaintType> listItem;

    /**
     * To set the layout for the AllComplaintTypeActivity and call all complaint type api to show the
     * list
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);

        ApiController.getInstance().getComplaintTypes(this);
    }

    /**
     * Function called after got response from all complaint type api to display the list
     */
    private void _displayListView() {
        ListView list = (ListView) findViewById(R.id.all_category_list);
        ComplaintTypeAdapter adapter = new ComplaintTypeAdapter(this, listItem);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

    /**
     * All complaint type api response handler. Here we have checked the invalid access token error to redirect to login
     * page
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        if (status.equalsIgnoreCase("success")) {
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
     * Event triggered when click on an item in listview. Click on list item redirect to create complaint page
     */
    @Override
    public void onItemClick(AdapterView<?> adapterview, View view, int position, long arg3) {
        ComplaintType ct = listItem.get(position);
        Intent intent = new Intent(this, CreateComplaintActivity.class);
        intent.putExtra("complaintTypeName", ct.getName());
        intent.putExtra("complaintTypeId", ct.getId());
        startActivity(intent);
    }
}
