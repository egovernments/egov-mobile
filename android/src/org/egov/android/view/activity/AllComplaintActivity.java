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
     * Function called if the user not enabled GPS/Location in their device
     * Give options to enable GPS/Location and cancel the pop up
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
     * Function called after got response from api call to display the list
     * @param isPagination => flag to inform the adapter to show load more button
     */
    private void _displayListView(boolean isPagination) {
        ListView list = (ListView) getActivity().findViewById(R.id.all_complaint_list);
        adapter = new ComplaintAdapter(getActivity(), listItem, isPagination, true, apiLevel, this);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 
     * @param path
     * @param jsonObj
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
                        item.setCreatedDate(_getValue(jo, "createdDate"));
                        item.setCreatedBy(_getValue(jo, "complainantName"));
                        item.setDetails(_getValue(jo, "detail"));
                        item.setComplaintId(jo.getString("crn"));
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
                    ((TextView) getActivity().findViewById(R.id.all_errMsg))
                            .setVisibility(View.VISIBLE);
                    _displayListView(false);
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
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

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