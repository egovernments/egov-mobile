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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && listItem.size() == 0 && !isApiLoaded) {
            ApiController.getInstance().getUserComplaints(this, page);
        } else if (isVisibleToUser && listItem.size() != 0) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_complaints, container, false);
    }

    private void _displayListView(boolean isPagination) {
        ListView list = (ListView) getActivity().findViewById(R.id.user_complaint_list);
        list.setOnItemClickListener(this);
        adapter = new ComplaintAdapter(getActivity(), listItem, isPagination, false, apiLevel, this);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

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

    private String _getValue(JSONObject jo, String key) {
        String result = "";
        try {
            result = (jo.has(key)) ? jo.getString(key) : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

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
                showMsg("Session expired");
                AndroidLibrary.getInstance().getSession().edit().putString("access_token", "")
                        .commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            } else {
                page = (page > 1) ? page - 1 : 1;
                showMsg(msg);
            }
        }
    }

    private void showMsg(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Complaint complaint = listItem.get(position);
        Intent intent = new Intent(getActivity(), ComplaintDetailActivity.class);
        intent.putExtra("complaintId", complaint.getComplaintId());
        intent.putExtra("name", complaint.getCreatedBy());
        intent.putExtra("complaint_status", complaint.getStatus());
        startActivity(intent);
    }

    @Override
    public void actionPerformed(String tag, Object... value) {
        if (tag.equals("LOAD_MORE")) {
            page = page + 1;
            ApiController.getInstance().getUserComplaints(this, page);
        }
    }
}
