package com.egov.android.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.egov.android.R;
import com.egov.android.controller.ApiController;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.listener.Event;
import com.egov.android.model.Complaint;
import com.egov.android.model.ComplaintListByType;
import com.egov.android.view.adapter.ComplaintListByTypeAdapter;

public class ComplaintListActivity extends BaseActivity implements OnGroupExpandListener {

    private int previousGroup = -1;
    private ExpandableListView expandableList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_list);

        expandableList = (ExpandableListView) findViewById(R.id.expandable_list);
        expandableList.setOnGroupExpandListener(this);
        
        ApiController.getInstance().getComplaint(this);
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        if (groupPosition != previousGroup) {
            expandableList.collapseGroup(previousGroup);
        }
        previousGroup = groupPosition;
    }

    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        if (!status.equalsIgnoreCase("success")) {
            return;
        }
        try {
            JSONArray ja = new JSONArray(event.getData().getResponse().toString());

            List<ComplaintListByType> listItem = new ArrayList<ComplaintListByType>();

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                ComplaintListByType ct = new ComplaintListByType();
                ct.setName(jo.getString("name"));
                ct.setImage(jo.getString("image"));
                JSONArray clist = new JSONArray(jo.getString("complaints"));

                List<Complaint> complaintList = new ArrayList<Complaint>();
                for (int j = 0; j < clist.length(); j++) {
                    JSONObject obj = clist.getJSONObject(j);
                    Complaint complaint = new Complaint();
                    complaint.setId(obj.getInt("id"));
                    complaint.setName(obj.getString("name"));
                    complaintList.add(complaint);
                }
                ct.setComplaintList(complaintList);
                listItem.add(ct);
            }
            _setAdapter(listItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void _setAdapter(List<ComplaintListByType> listItem) {
        ComplaintListByTypeAdapter adapter = new ComplaintListByTypeAdapter(this, listItem);
        expandableList.setAdapter(adapter);
    }
}
