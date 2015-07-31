package com.egov.android.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.egov.android.R;
import com.egov.android.controller.ApiController;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.listener.Event;
import com.egov.android.model.Complaint;
import com.egov.android.view.adapter.ComplaintTypeAdapter;

public class ComplaintTypeListActivity extends BaseActivity implements OnItemClickListener {

    private ComplaintTypeAdapter<Complaint> adapter = null;
    private List<Complaint> listItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_type);

        //ApiController.getInstance().getComplaintByType(this);
    }

    private void _setAdapter() {
        adapter = new ComplaintTypeAdapter<Complaint>(this, listItem);
        ListView listView = (ListView) findViewById(R.id.complaint_type_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
        Intent intent = new Intent(this, ComplaintListActivity.class);
        intent.putExtra("name", listItem.get(position).getName());
        startActivity(intent);
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        if (status.equalsIgnoreCase("success")) {
            Log.d(TAG, event.getData().getResponse().toString());
            try {
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                listItem = new ArrayList<Complaint>();
                Complaint item = null;
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    item = new Complaint();
                    item.setName(jo.getString("name"));
                   // item.setImage(jo.getString("image"));
                    listItem.add(item);
                }
                _setAdapter();
            } catch (JSONException e) {

                e.printStackTrace();
            }

        }

    }
}
