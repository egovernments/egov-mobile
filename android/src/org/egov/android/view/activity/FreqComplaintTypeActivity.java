package org.egov.android.view.activity;

import java.io.File;
import java.util.ArrayList;

import org.egov.android.R;
import org.egov.android.api.ApiUrl;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.common.StorageManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_category);
        ((RelativeLayout) findViewById(R.id.all_category)).setOnClickListener(this);
        ApiController.getInstance().getFreqComplaintTypes(this);
    }

    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.all_category:
                startActivity(new Intent(this, AllComplaintTypeActivity.class));
                break;
        }
    }

    private void _addDownloadJobs(String path, JSONObject jsonObj) {
        try {
            JSONObject jo = new JSONObject();
            jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                    + "/pgr/resources/images/complaintType/" + jsonObj.getString("typeImage"));
            jo.put("type", "complaintType");
            jo.put("destPath", path + "/" + jsonObj.getString("typeImage"));
            SQLiteHelper.getInstance().execSQL(
                    "INSERT INTO jobs(data, status, type, triedCount) values ('" + jo.toString()
                            + "', 'waiting', 'download', 0)");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        ComplaintType ct = listItem.get(position);
        Intent intent = new Intent(this, CreateComplaintActivity.class);
        intent.putExtra("complaintTypeName", ct.getName());
        intent.putExtra("complaintTypeId", ct.getId());
        startActivity(intent);
    }

    private void _displayListView() {
        ListView list = (ListView) findViewById(R.id.all_category_list);
        list.setVisibility(View.VISIBLE);
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
                        StorageManager sm = new StorageManager();
                        Object[] obj = sm.getStorageInfo();
                        String folderName = obj[0].toString() + "/egovernments/complaint_type";
                        cat.setImagePath(folderName + File.separator + jo.getString("typeImage"));
                        listItem.add(cat);
                        sm.mkdirs(folderName);
                        if (!new File(folderName + File.separator + jo.getString("typeImage"))
                                .exists()) {
                            _addDownloadJobs(folderName, jo);
                        }
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

    @Override
    public void run() {
        if (listItem.size() > 0) {
            gridAdapter.notifyDataSetChanged();
        }
    }
}
