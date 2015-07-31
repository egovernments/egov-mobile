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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);

        ApiController.getInstance().getComplaintTypes(this);
    }

    private void _displayListView() {
        ListView list = (ListView) findViewById(R.id.all_category_list);
        ComplaintTypeAdapter adapter = new ComplaintTypeAdapter(this, listItem);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

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

    @Override
    public void onItemClick(AdapterView<?> adapterview, View view, int position, long arg3) {
        ComplaintType ct = listItem.get(position);
        Intent intent = new Intent(this, CreateComplaintActivity.class);
        intent.putExtra("complaintTypeName", ct.getName());
        intent.putExtra("complaintTypeId", ct.getId());
        startActivity(intent);
    }
}
