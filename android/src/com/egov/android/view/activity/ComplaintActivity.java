package com.egov.android.view.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.egov.android.R;
import com.egov.android.api.ApiUrl;
import com.egov.android.controller.ApiController;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.api.IApiUrl;
import com.egov.android.library.http.IHttpClientListener;
import com.egov.android.library.http.Uploader;
import com.egov.android.library.listener.Event;

public class ComplaintActivity extends BaseActivity implements IHttpClientListener {

    List<String> list = null;
    private Dialog dialog = null;
    private boolean toastShown = false;
    AutoCompleteTextView autocomplete = null;
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        ((ImageView) findViewById(R.id.complaint_location_icon)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.add_photo)).setOnClickListener(this);
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
            case R.id.add_photo:
                openDialog();
                break;
            case R.id.from_gallery:
                uploadImageFromSDCard();
                dialog.cancel();
                break;
            case R.id.from_camera:
                captureImage();
                dialog.cancel();
                break;
        }
    }

    private void openDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();

        ((LinearLayout) dialog.findViewById(R.id.from_gallery)).setOnClickListener(this);
        ((LinearLayout) dialog.findViewById(R.id.from_camera)).setOnClickListener(this);
    }

    private void uploadImageFromSDCard() {
        Intent photo_picker = new Intent(Intent.ACTION_PICK);
        photo_picker.setType("image/*,video/*");
        startActivityForResult(photo_picker, RESULT_LOAD_IMAGE);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        toastShown = false;
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
                    null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();

            if (!checkFileExtension(imagePath)) {
                showMsg("File type must be jpg or png or bmp");
                return;
            }

            File file = new File(imagePath);
            long bytes = file.length();
            long fileSize = getConfig().getInt("upload.file.size") * 1024 * 1024;

            if (bytes > Long.valueOf(fileSize)) {
                showMsg("File size must be less than 1MB");
            } else {
                _setImageResource(imagePath);
            }
        }
    }

    private boolean checkFileExtension(String filePath) {
        String fileType = (String) getConfig().get("upload.file.type", "");
        Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(" + fileType + "))$)");
        Matcher mtch = fileExtnPtrn.matcher(filePath);
        if (mtch.matches()) {
            return true;
        }
        return false;
    }

    private void _setImageResource(String imagePath) {
        LinearLayout linear = (LinearLayout) findViewById(R.id.innerLayout);

        LinearLayout innerLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linear_params = new LinearLayout.LayoutParams(dpToPix(120),
                dpToPix(120));
        linear_params.setMargins(10, 0, 0, 0);
        innerLayout.setLayoutParams(linear_params);
        innerLayout.setBackgroundResource(R.drawable.edittext_border);

        ImageView image = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPix(120), dpToPix(120));
        params.gravity = Gravity.CENTER;
        image.setLayoutParams(params);

        Uploader upload = new Uploader();
        upload.setUrl("http://192.168.1.91/charles/egovernance/upload.php");
        upload.setInputFile(imagePath);
        upload.setListener(this);
        upload.upload();

        image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        innerLayout.addView(image);
        linear.addView(innerLayout);
    }

    private int dpToPix(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources()
                .getDisplayMetrics());
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
                    "Please select location");
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

    @Override
    public void onProgress(int percent) {
        Log.d(TAG, "---------------------------> " + String.valueOf(percent));

    }

    @Override
    public void onComplete(byte[] data) {
        // enable / disable

        String res = new String(data);
        Log.d(TAG, "result " + res);

    }

    @Override
    public void onError(byte[] data) {
        // enable / disable

    }

}
