package org.egov.android.view.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.egov.android.R;
import org.egov.android.api.ApiUrl;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.IApiUrl;
import org.egov.android.common.StorageManager;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.listener.Event;
import org.egov.android.model.Complaint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreateComplaintActivity extends BaseActivity implements TextWatcher,
        OnItemClickListener {

    private int locationId = 0;
    private Dialog dialog = null;
    private String assetPath = "";
    AutoCompleteTextView location;
    private List<JSONObject> list;
    private double latitude = 0.0;
    private double longitute = 0.0;
    private int complaintTypeId = 0;
    private int file_upload_limit = 0;
    private String currentPhotoPath = "";
    private static final int CAPTURE_IMAGE = 1000;
    private static final int FROM_GALLERY = 2000;
    private static final int GET_LOCATION = 3000;
    ArrayList<String> imageUrl = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_complaint);

        Bundle bundle = getIntent().getExtras();
        complaintTypeId = bundle.getInt("complaintTypeId");
        ((TextView) findViewById(R.id.complaint_type)).setText(bundle
                .getString("complaintTypeName"));
        ((RelativeLayout) findViewById(R.id.complaint_type_container)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.complaint_location_icon)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.add_photo)).setOnClickListener(this);
        ((Button) findViewById(R.id.complaint_doComplaint)).setOnClickListener(this);

        location = (AutoCompleteTextView) findViewById(R.id.complaint_location);
        location.addTextChangedListener(this);
        location.setOnItemClickListener(this);

        StorageManager sm = new StorageManager();
        Object[] obj = sm.getStorageInfo();
        assetPath = obj[0].toString() + "/egovernments/complaints";
        _deleteFile(assetPath + File.separator + "current");
        sm.mkdirs(assetPath + File.separator + "current");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.complaint_doComplaint:
                _addComplaint();
                break;
            case R.id.complaint_location_icon:
                startActivityForResult(new Intent(this, MapActivity.class), GET_LOCATION);
                break;
            case R.id.add_photo:
                openDialog();
                break;
            case R.id.from_gallery:
                _openGalleryImages();
                dialog.cancel();
                break;
            case R.id.from_camera:
                _openCamera();
                dialog.cancel();
                break;
            case R.id.complaint_type_container:
                startActivity(new Intent(this, FreqComplaintTypeActivity.class));
                break;
        }
    }

    private void openDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_upload_dialog);
        ((LinearLayout) dialog.findViewById(R.id.from_gallery)).setOnClickListener(this);
        ((LinearLayout) dialog.findViewById(R.id.from_camera)).setOnClickListener(this);
        dialog.show();
    }

    private void _openGalleryImages() {
        Intent photo_picker = new Intent(Intent.ACTION_PICK);
        photo_picker.setType("image/*");
        startActivityForResult(photo_picker, FROM_GALLERY);
    }

    private void _openCamera() {
        File imageFile = null;
        try {
            int photoNo = file_upload_limit + 1;
            imageFile = new File(assetPath + File.separator + "current" + File.separator + "photo_"
                    + photoNo + ".jpg");
            currentPhotoPath = imageFile.getAbsolutePath();
            Intent mediaCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mediaCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(mediaCamera, CAPTURE_IMAGE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    private void _createImageFile(String path) {
        try {
            int photoNo = file_upload_limit + 1;
            String url = assetPath + File.separator + "current" + File.separator + "photo_"
                    + photoNo + ".jpg";
            InputStream in = new FileInputStream(path);
            StorageManager sm = new StorageManager();
            Object[] obj = sm.getStorageInfo();
            long totalSize = (Long) obj[2];
            if (totalSize < in.toString().length()) {
                showMessage(getMessage(R.string.sdcard_space_not_sufficient));
                return;
            }
            FileOutputStream out = new FileOutputStream(url);
            byte[] data = new byte[in.available()];
            in.read(data);
            out.write(data);
            in.close();
            out.close();
            _validateImageUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void _deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    new File(file, children[i]).delete();
                }
            } else {
                file.delete();
            }
        }
    }

    private void _reorderFiles() {
        String path = assetPath + File.separator + "current";
        File folder = new File(path);
        File list[] = folder.listFiles();
        imageUrl = new ArrayList<String>();
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        for (int i = 1; i <= list.length; i++) {
            String newPath = assetPath + File.separator + "current" + File.separator + "photo_" + i
                    + ".jpg";
            list[i - 1].renameTo(new File(newPath));
            imageUrl.add(newPath);
            ((ViewGroup) ((ViewGroup) container.getChildAt(i - 1)).getChildAt(0)).getChildAt(0)
                    .setTag(newPath);
        }

        if (imageUrl.size() > 0) {
            ((ImageView) findViewById(R.id.image_container))
                    .setImageBitmap((_getBitmapImage(imageUrl.get(imageUrl.size() - 1).toString())));
        } else {
            ((ImageView) findViewById(R.id.image_container))
                    .setImageResource(R.drawable.default_image);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            _validateImageUrl(currentPhotoPath);
        } else if (requestCode == FROM_GALLERY && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
                    null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            _createImageFile(imagePath);
        } else if (requestCode == GET_LOCATION && data != null) {
            String city_name = data.getStringExtra("city_name");
            latitude = data.getDoubleExtra("latitude", 0);
            longitute = data.getDoubleExtra("longitute", 0);
            location.setText(city_name);
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

    private void _validateImageUrl(String imagePath) {
        if (!checkFileExtension(imagePath)) {
            _deleteFile(imagePath);
            showMessage(getMessage(R.string.file_type));
            return;
        }

        File file = new File(imagePath);
        long bytes = file.length();
        long fileSize = getConfig().getInt("upload.file.size") * 1024 * 1024;

        if (bytes > Long.valueOf(fileSize)) {
            _deleteFile(imagePath);
            showMessage(getMessage(R.string.file_size));
        } else if (getConfig().getInt("upload.file.limit") <= file_upload_limit) {
            _deleteFile(imagePath);
            showMessage(getMessage(R.string.file_upload_count));
        } else {
            file_upload_limit++;
            _addImageView(imagePath);
        }
    }

    @SuppressLint("InflateParams")
    private void _addImageView(String imagePath) {
        final ImageView image_container = (ImageView) findViewById(R.id.image_container);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_photo, null);

        RelativeLayout inner_container = (RelativeLayout) view.findViewById(R.id.inner_container);
        LinearLayout.LayoutParams inner_container_params = new LinearLayout.LayoutParams(
                dpToPix(100), dpToPix(100));

        inner_container.setLayoutParams(inner_container_params);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageBitmap(_getBitmapImage(imagePath));
        image.setTag(imagePath);
        container.addView(inner_container);
        imageUrl.add(imagePath);

        image_container.setImageBitmap(_getBitmapImage(imagePath));

        ImageView delete_icon = (ImageView) view.findViewById(R.id.delete_photo);
        delete_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                file_upload_limit--;
                RelativeLayout deleteView = (RelativeLayout) v.getParent();
                ((LinearLayout) findViewById(R.id.container)).removeView(deleteView);
                ImageView image = (ImageView) deleteView.findViewById(R.id.image);
                _deleteFile(image.getTag().toString());
                _reorderFiles();
            }
        });
        inner_container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView image = (ImageView) v.findViewById(R.id.image);
                ((ImageView) findViewById(R.id.image_container))
                        .setImageBitmap(_getBitmapImage(image.getTag().toString()));
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

    private Bitmap _getBitmapImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    private int dpToPix(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources()
                .getDisplayMetrics());
    }

    private void setSpinnerData(List<JSONObject> list) {
        ArrayList<String> item = new ArrayList<String>();
        try {
            for (int i = 0; i < list.size(); i++) {
                item.add(list.get(i).getString("name"));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.select_dialog_item, item);
            location.setAdapter(adapter);
            location.setThreshold(3);
            location.setTag(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        IApiUrl url = event.getData().getApiMethod().getApiUrl();
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        if (status.equalsIgnoreCase("success")) {
            try {
                if (url.getUrl().equals(ApiUrl.GET_LOCATION_BY_NAME.getUrl())) {
                    try {
                        JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                        list = new ArrayList<JSONObject>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);
                            list.add(jo);
                        }
                        setSpinnerData(list);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (url.getUrl().equals(ApiUrl.ADD_COMPLAINT.getUrl())) {
                    showMessage(msg);
                    JSONObject result = new JSONObject((String) event.getData().getResponse());
                    File f1 = new File(assetPath + File.separator + "current");
                    File f2 = new File(assetPath + File.separator + result.getString("crn"));
                    f1.renameTo(f2);
                    _addUploadJobs(result.getString("crn"));
                    Intent intent = new Intent(this, ComplaintActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
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

    private void _addUploadJobs(String id) {
        File folder = new File(assetPath + File.separator + id);
        File[] listOfFiles = new File[] {};

        if (folder.exists()) {
            listOfFiles = folder.listFiles();
        }

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                try {
                    JSONObject jo = new JSONObject();
                    jo.put("file", assetPath + File.separator + id + File.separator
                            + listOfFiles[i].getName());
                    jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                            + "/api/v1.0/complaint/" + id + "/uploadSupportDocument");
                    SQLiteHelper.getInstance().execSQL(
                            "INSERT INTO jobs(data, status, type, triedCount) values ('"
                                    + jo.toString() + "', 'waiting', 'upload', 0)");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        ServiceController.getInstance().startJobs();
        finish();
    }

    private void _addComplaint() {

        String detail = ((EditText) findViewById(R.id.complaint_details)).getText().toString()
                .trim();
        String landmark = ((EditText) findViewById(R.id.complaint_landmark)).getText().toString()
                .trim();

        if (isEmpty(location.getText().toString().trim())) {
            showMessage(getMessage(R.string.location_empty));
            return;
        } else if (isEmpty(detail)) {
            showMessage(getMessage(R.string.detail_empty));
            return;
        } else if (isEmpty(landmark)) {
            showMessage(getMessage(R.string.landmark_empty));
            return;
        }

        Complaint complaint = new Complaint();
        complaint.setComplaintTypeId(Integer.valueOf(complaintTypeId));
        complaint.setDetails(detail);
        complaint.setLandmarkDetails(landmark);
        complaint.setLocationId(locationId);
        complaint.setLatitude(latitude);
        complaint.setLongitute(longitute);
        ApiController.getInstance().addComplaint(this, complaint);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 3) {
            ApiController.getInstance().getLocationByName(this, s.toString());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        try {
            locationId = list.get(position).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
