package com.egov.android.view.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    private String imagePath = "";
    private ImageView addIcon = null;
    private TextView percentage = null;
    private int file_upload_limit = 0;
    private boolean toastShown = false;
    private LinearLayout container = null;
    private RelativeLayout deleteView = null;
    private AutoCompleteTextView autocomplete = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        ((ImageView) findViewById(R.id.complaint_location_icon)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.add_photo)).setOnClickListener(this);
        ((Button) findViewById(R.id.complaint_doComplaint)).setOnClickListener(this);
        autocomplete = (AutoCompleteTextView) findViewById(R.id.complaint_type);
        ApiController.getInstance().getComplaintType(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.complaint_doComplaint:
                //addComplaint();
                break;
            case R.id.complaint_location_icon:
                startActivityForResult(new Intent(this, MapActivity.class), 2);
                break;
            case R.id.add_photo:
                openDialog(true);
                break;
            case R.id.from_gallery:
                uploadImageFromSDCard();
                dialog.cancel();
                break;
            case R.id.from_camera:
                captureImage();
                dialog.cancel();
                break;
            case R.id.view:
                Intent view_photo = new Intent(Intent.ACTION_PICK);
                view_photo.setType("image/*,video/*");
                startActivity(view_photo);
                dialog.cancel();
                break;
            case R.id.delete:
                file_upload_limit--;
                container.removeView(deleteView);
                dialog.cancel();
                break;
        }
    }

    private void openDialog(boolean upload) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (upload) {
            dialog.setContentView(R.layout.custom_upload_dialog);
            ((LinearLayout) dialog.findViewById(R.id.from_gallery)).setOnClickListener(this);
            ((LinearLayout) dialog.findViewById(R.id.from_camera)).setOnClickListener(this);
        } else {
            dialog.setContentView(R.layout.custom_confirm_dialog);
            ((LinearLayout) dialog.findViewById(R.id.view)).setOnClickListener(this);
            ((LinearLayout) dialog.findViewById(R.id.delete)).setOnClickListener(this);
        }
        dialog.show();
    }

    private void uploadImageFromSDCard() {
        Intent photo_picker = new Intent(Intent.ACTION_PICK);
        photo_picker.setType("image/*,video/*");
        startActivityForResult(photo_picker, 1);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        toastShown = false;
        if (requestCode == 1 && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
                    null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();

            if (!checkFileExtension(imagePath)) {
                showMsg(_setMessage(R.string.file_type));
                return;
            }
            File file = new File(imagePath);
            long bytes = file.length();
            long fileSize = getConfig().getInt("upload.file.size") * 1024 * 1024;

            if (bytes > Long.valueOf(fileSize)) {
                showMsg(_setMessage(R.string.file_size));
            } else if (getConfig().getInt("upload.file.limit") <= file_upload_limit) {
                showMsg(_setMessage(R.string.file_upload_count));
            } else {
                file_upload_limit++;
                _uploadImage(imagePath);
            }
        } else if (requestCode == 2 && null != data) {
            String city_name = data.getStringExtra("city_name");
            ((EditText) findViewById(R.id.complaint_location)).setText(city_name);
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

    private void _uploadImage(String image_path) {
        imagePath = image_path;
        percentage = (TextView) findViewById(R.id.percentage);
        addIcon = (ImageView) findViewById(R.id.add_photo);

        Uploader upload = new Uploader();
        upload.setUrl("http://192.168.1.91/charles/egovernance/upload.php");
        upload.setInputFile(imagePath);
        upload.setListener(this);
        upload.upload();

        addIcon.setVisibility(View.GONE);
        percentage.setVisibility(View.VISIBLE);
    }

    private void _addImageView() {
        container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_photo, null);

        RelativeLayout inner_container = (RelativeLayout) view.findViewById(R.id.inner_container);
        LinearLayout.LayoutParams inner_container_params = new LinearLayout.LayoutParams(
                dpToPix(120), dpToPix(120));

        inner_container_params.setMargins(0, 0, 10, 0);
        inner_container.setLayoutParams(inner_container_params);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        container.addView(view, 0);

        RelativeLayout addLinear = (RelativeLayout) findViewById(R.id.add_button);

        LinearLayout.LayoutParams add_params = new LinearLayout.LayoutParams(dpToPix(120),
                dpToPix(120));
        add_params.setMargins(5, 0, 0, 0);
        addLinear.setLayoutParams(add_params);

        inner_container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteView = (RelativeLayout) v;
                openDialog(false);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hr_scroll);
                hsv.scrollTo(hsv.getWidth() + 600, 0);
            }
        }, 500);
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
                    _setMessage(R.string.location_empty));
        }
        if (isEmpty(phone.getText().toString())) {
            _changeStatus("error", R.id.complaint_phone_status, _setMessage(R.string.phone_empty));
        }
        if (isEmpty(email.getText().toString())) {
            _changeStatus("error", R.id.complaint_email_status, _setMessage(R.string.email_empty));
        }

        if (isEmpty(autocomplete.getText().toString())) {
            _changeStatus("error", R.id.complaint_type_status, _setMessage(R.string.email_empty));
        }
        if (isEmpty(detail.getText().toString())) {
            _changeStatus("error", R.id.complaint_details_status,
                    _setMessage(R.string.detail_empty));
        }

        if (isEmpty(landmark.getText().toString())) {
            _changeStatus("error", R.id.complaint_landmark_status,
                    _setMessage(R.string.landmark_empty));
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

    private String _setMessage(int id) {
        return getResources().getString(id);
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
        percentage.setText(String.valueOf(percent) + "%");
    }

    @Override
    public void onComplete(byte[] data) {
        percentage.setVisibility(View.GONE);
        addIcon.setVisibility(View.VISIBLE);
        _addImageView();
    }

    @Override
    public void onError(byte[] data) {
        Toast.makeText(this, "Error in file upload", Toast.LENGTH_LONG).show();
        file_upload_limit--;
        percentage.setVisibility(View.GONE);
        addIcon.setVisibility(View.VISIBLE);
    }

}
