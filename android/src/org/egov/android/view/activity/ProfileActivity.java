package org.egov.android.view.activity;

import java.io.File;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.common.StorageManager;
import org.egov.android.listener.Event;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends BaseActivity {
    private String mobileNo = "";
    private String userName = "";
    private String mailId = "";
    private String gender = "";
    private String altContactNumber = "";
    private String dateOfBirth = "";
    private String panCardNumber = "";
    private String aadhaarCardNumber = "";
    private String langauge = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
        if (apiLevel > 13) {
            setContentView(R.layout.activity_profile);
        } else {
            setContentView(R.layout.activity_lower_version_profile);
        }
        ((ImageView) findViewById(R.id.edit_icon)).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApiController.getInstance().getProfile(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.edit_icon:
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("mailId", mailId);
                intent.putExtra("mobileNo", mobileNo);
                intent.putExtra("gender", gender);
                intent.putExtra("altContactNumber", altContactNumber);
                intent.putExtra("dateOfBirth", dateOfBirth);
                intent.putExtra("panCardNumber", panCardNumber);
                intent.putExtra("aadhaarCardNumber", aadhaarCardNumber);
                intent.putExtra("langauge", langauge);
                startActivity(intent);
                break;
        }
    }

    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();

        if (status.equalsIgnoreCase("success")) {
            try {
                JSONObject jo = new JSONObject(event.getData().getResponse().toString());
                userName = _getValue(jo, "name");
                mailId = _getValue(jo, "emailId");
                mobileNo = _getValue(jo, "mobileNumber");
                gender = (_getValue(jo, "gender") == "") ? "" : _getValue(jo, "gender").substring(
                        0, 1).toUpperCase()
                        + _getValue(jo, "gender").substring(1).toLowerCase();
                altContactNumber = _getValue(jo, "altContactNumber");
                dateOfBirth = _getValue(jo, "dob");
                panCardNumber = _getValue(jo, "panCard");
                aadhaarCardNumber = _getValue(jo, "aadhaarCard");
                langauge = _getValue(jo, "preferredLanguage");

                ((TextView) findViewById(R.id.name)).setText(userName);
                ((TextView) findViewById(R.id.email)).setText(mailId);
                ((TextView) findViewById(R.id.mobile)).setText(mobileNo);
                ((TextView) findViewById(R.id.gender)).setText(gender);
                ((TextView) findViewById(R.id.alt_contact_num)).setText(altContactNumber);
                ((TextView) findViewById(R.id.dob)).setText(dateOfBirth);
                ((TextView) findViewById(R.id.pan)).setText(panCardNumber);
                ((TextView) findViewById(R.id.aadhaar)).setText(aadhaarCardNumber);
                ((TextView) findViewById(R.id.pref_lang)).setText(langauge);

                StorageManager sm = new StorageManager();
                Object[] obj = sm.getStorageInfo();
                String profPath = obj[0].toString() + "/egovernments/profile";
                sm.mkdirs(profPath);
                File imgFile = new File(profPath + "/photo_" + mobileNo + ".jpg");
                if (imgFile.exists()) {
                    ImageView image = (ImageView) findViewById(R.id.profile_image);
                    image.setImageBitmap(_getBitmapImage(profPath + "/photo_" + mobileNo + ".jpg"));
                }
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

    private Bitmap _getBitmapImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        return BitmapFactory.decodeFile(path, options);
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
