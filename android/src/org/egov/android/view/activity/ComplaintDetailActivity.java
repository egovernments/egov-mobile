package org.egov.android.view.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.common.StorageManager;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.listener.Event;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ComplaintDetailActivity extends BaseActivity {

    private String id = "";
    private Spinner statusSpinner;
    private LinearLayout imageCntainer = null;
    private String complaintTypeName = "";
    private String complaintStatus = "";
    private String createdDate = "";
    private String lastModifiedDate = "";
    private String complaintFolderName = "";
    private boolean isComplaintDetail = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

        imageCntainer = (LinearLayout) findViewById(R.id.complaint_image_container);
        String[] items = { "Select", "Withdrawn" };

        int apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
        if (apiLevel > 13) {
            statusSpinner = (Spinner) findViewById(R.id.status_spinner);
        } else {
            statusSpinner = (Spinner) findViewById(R.id.status_normal_spinner);
        }
        statusSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_list_item);
        statusSpinner.setAdapter(dataAdapter);

        ((Button) findViewById(R.id.status_summary)).setOnClickListener(this);
        ((Button) findViewById(R.id.complaint_changeStatus)).setOnClickListener(this);

        id = getIntent().getExtras().getString("complaintId");
        String name = getIntent().getExtras().getString("name");
        String status = getIntent().getExtras().getString("complaint_status");

        if (name.equals("") && !status.equalsIgnoreCase("withdrawn")) {
            ((LinearLayout) findViewById(R.id.action_container)).setVisibility(View.VISIBLE);
        }

        StorageManager sm = new StorageManager();
        Object[] obj = sm.getStorageInfo();
        complaintFolderName = obj[0].toString() + "/egovernments/complaints/" + id;
        _showComplaintImages();
        isComplaintDetail = true;
        ApiController.getInstance().getComplaintDetail(this, id);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.status_summary:
                Intent intent = new Intent(this, StatusSummaryActivity.class);
                intent.putExtra("complaintId", id);
                intent.putExtra("complaintTypeName", complaintTypeName);
                intent.putExtra("status", complaintStatus);
                intent.putExtra("created_date", createdDate);
                intent.putExtra("lastModifiedDate", lastModifiedDate);
                startActivity(intent);
                break;
            case R.id.complaint_changeStatus:
                _changeStatus();
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    private void _changeStatus() {
        int pos = statusSpinner.getSelectedItemPosition();
        if (pos == 0) {
            showMessage("Please select a status");
        } else {
            String status = statusSpinner.getSelectedItem().toString();
            String message = ((EditText) findViewById(R.id.message)).getText().toString();
            ApiController.getInstance().complaintChangeStatus(this, id, status.toUpperCase(),
                    message);
        }
    }

    private void _showComplaintImages() {
        File folder = new File(complaintFolderName);
        if (!folder.exists()) {
            return;
        }
        File[] listOfFiles = folder.listFiles();
        ImageView image;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                Log.d("EGOV_JOB", "File path" + complaintFolderName + File.separator
                        + listOfFiles[i].getName());
                image = new ImageView(this);
                Bitmap bmp = _getBitmapImage(complaintFolderName + File.separator
                        + listOfFiles[i].getName());
                image.setImageBitmap(bmp);
                LinearLayout.LayoutParams inner_container_params = new LinearLayout.LayoutParams(
                        dpToPix(80), dpToPix(80));

                image.setLayoutParams(inner_container_params);
                image.setPadding(0, 0, 5, 0);
                imageCntainer.addView(image);
            }
        }
    }

    private Bitmap _getBitmapImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(path, options);
    }

    private int dpToPix(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources()
                .getDisplayMetrics());
    }

    private void _addDownloadJobs(int totalFiles) {
        JSONObject jo = null;
        try {
            for (int i = 1; i <= totalFiles; i++) {
                jo = new JSONObject();
                jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                        + "/api/v1.0/complaint/" + id + "/downloadSupportDocument");
                jo.put("fileNo", i);
                jo.put("type", "complaint");
                jo.put("destPath", complaintFolderName + "/photo_" + i + ".jpg");
                SQLiteHelper.getInstance().execSQL(
                        "INSERT INTO jobs(data, status, type, triedCount) values ('"
                                + jo.toString() + "', 'waiting', 'download', 0)");
            }
            ServiceController.getInstance().startJobs();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        if (status.equalsIgnoreCase("success")) {
            if (isComplaintDetail) {
                try {
                    isComplaintDetail = false;
                    JSONObject jo = new JSONObject(event.getData().getResponse().toString());

                    complaintStatus = _getValue(jo, "status");
                    createdDate = _getValue(jo, "createdDate");
                    lastModifiedDate = _getValue(jo, "lastModifiedDate");
                    complaintTypeName = _getValue(jo, "complaintTypeName");

                    ((TextView) findViewById(R.id.crn)).setText(_getValue(jo, "crn"));
                    ((TextView) findViewById(R.id.complaintType)).setText(complaintTypeName);
                    ((TextView) findViewById(R.id.details)).setText(_getValue(jo, "detail"));
                    ((TextView) findViewById(R.id.landmarkDetails)).setText(_getValue(jo,
                            "landmarkDetails"));
                    ((TextView) findViewById(R.id.complaintStatus)).setText(complaintStatus
                            .toLowerCase());

                    if (jo.has("locationName")) {
                        ((TextView) findViewById(R.id.location)).setText(_getValue(jo,
                                "locationName"));
                    } else {
                        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                        List<Address> addresses;
                        try {
                            addresses = geocoder.getFromLocation(jo.getDouble("lat"),
                                    jo.getDouble("lng"), 1);
                            if (addresses.size() > 0) {
                                String cityName = addresses.get(0).getAddressLine(0);
                                ((TextView) findViewById(R.id.location)).setText(cityName);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    File complaintFolder = new File(complaintFolderName);
                    if (!complaintFolder.exists()) {
                        new StorageManager().mkdirs(complaintFolderName);
                        _addDownloadJobs(jo.getInt("supportDocsSize"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showMessage(msg);
                Intent intent = new Intent(this, ComplaintActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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

    private String _getValue(JSONObject jo, String key) {
        String result = "";
        try {
            result = (jo.has(key)) ? jo.getString(key) : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
