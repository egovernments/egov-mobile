package com.egov.android.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.egov.android.R;
import com.egov.android.api.ApiUrl;
import com.egov.android.controller.ApiController;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.api.IApiUrl;
import com.egov.android.library.listener.Event;

public class ComplaintActivity extends BaseActivity {

    List<String> list = null;
    AutoCompleteTextView autocomplete = null;
    private boolean toastShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        ((ImageView) findViewById(R.id.complaint_location_icon)).setOnClickListener(this);
        ((Button) findViewById(R.id.complaint_doComplaint)).setOnClickListener(this);

        ApiController.getInstance().getComplaintType(this);

        autocomplete = (AutoCompleteTextView) findViewById(R.id.complaint_type);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.complaint_doComplaint:
                addComplaint();
                break;
            case R.id.complaint_location_icon:
                startActivity(new Intent(this, MapActivity.class));
                break;
        }
    }

    private void setSpinnerData() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, list);
        autocomplete.setThreshold(1);
        autocomplete.setAdapter(adapter);
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        IApiUrl url = event.getData().getApiMethod().getApiUrl();
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        if (url.getUrl().equals(ApiUrl.ADD_COMPLAINT.getUrl())) {
            showMsg(msg);
            if (status.equalsIgnoreCase("success")) {
                startActivity(new Intent(this, ComplaintListActivity.class));
            }
        } else if (url.getUrl().equals(ApiUrl.COMPLAINT_TYPE.getUrl())) {
            if (status.equalsIgnoreCase("success")) {
                Log.d(TAG, event.getData().getResponse().toString());
                try {
                    JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                    list = new ArrayList<String>();
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        list.add(jo.getString("name"));
                    }
                    setSpinnerData();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    private void addComplaint() {
        _clearStatus(new int[] { R.id.complaint_location_status, R.id.complaint_phone_status,
                R.id.complaint_email_status, R.id.complaint_type_status,
                R.id.complaint_details_status, R.id.complaint_landmark_status, });

        EditText location = (EditText) findViewById(R.id.complaint_location);
        EditText phone = (EditText) findViewById(R.id.complaint_phone);
        EditText email = (EditText) findViewById(R.id.complaint_email);
        EditText detail = (EditText) findViewById(R.id.complaint_details);
        EditText landmark = (EditText) findViewById(R.id.complaint_landmark);

        if (isEmpty(location.getText().toString())) {
            _changeStatus("error", R.id.complaint_location_status,
                    "Please select a location from the map");
        }
        if (isEmpty(phone.getText().toString())) {
            _changeStatus("error", R.id.complaint_phone_status, "Please enter phone number");
        }
        if (isEmpty(email.getText().toString())) {
            _changeStatus("error", R.id.complaint_email_status, "Please enter email");
        }

        if (isEmpty(autocomplete.getText().toString())) {
            _changeStatus("error", R.id.complaint_type_status, "Please select complaint type");
        }
        if (isEmpty(detail.getText().toString())) {
            _changeStatus("error", R.id.complaint_details_status, "Please enter detail");
        }

        if (isEmpty(landmark.getText().toString())) {
            _changeStatus("error", R.id.complaint_landmark_status, "Please enter phone number");
        }

        if (!isEmpty(phone.getText().toString()) && !isEmpty(email.getText().toString())
                && !isEmpty(detail.getText().toString()) && !isEmpty(landmark.getText().toString())) {
            ApiController.getInstance().addComplaint(this);
        }
    }

    private void _clearStatus(int[] ids) {
        toastShown = false;
        for (int id : ids) {
            setImageBackground(id, "success");
        }
    }

    private void _changeStatus(String type, int id, String message) {
        setImageBackground(id, type);
        showMsg(message);
    }

    private void showMsg(String message) {
        if (toastShown == false && message != null && !message.equals("")) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
            toastShown = true;
        }
    }

    private boolean isEmpty(String data) {
        return (data == null || (data != null && data.trim().equals("")));
    }

    private void setImageBackground(int id, String type) {
        int image = (type == "success") ? R.drawable.success_icon : R.drawable.error_icon;
        ((ImageView) findViewById(id)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(id)).setBackgroundResource(image);
    }

}
