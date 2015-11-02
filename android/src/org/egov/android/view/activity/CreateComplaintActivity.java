/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.view.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.api.ApiClient;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.ApiUrl;
import org.egov.android.api.IApiUrl;
import org.egov.android.common.StorageManager;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.listener.Event;
import org.egov.android.model.Complaint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    LocationManager locationManager;
    ProgressDialog progressDialog;
    private boolean isCurrentLocation=true;
    int gpsTimeOutSec;
    Timer gpsTimeOutTimer=new Timer();
	private final static String TAG = CreateComplaintActivity.class.getName();

    
    /**
     * It is used to initialize an activity. An Activity is an application component that provides a
     * screen with which users can interact in order to do something, To initialize the
     * CreateComplaintActivity, set click listener to the complaint type,complaint location,add
     * complaint photo and create complaint button. StorageManager is the interface to the systems'
     * storage service. The storage manager handles storage-related items. mkdirs creates a new
     * directory on a device storage area to store the complaint photos
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_complaint);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressDialog=new ProgressDialog(CreateComplaintActivity.this);
        progressDialog.setCancelable(false);

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
        if (!getGpsStatus()) {
          _showSettingsAlert();
        }
        
    }

    /**
     * Function called if the user didn't enable GPS/Location in their device. Give options to
     * enable GPS/Location and cancel the pop up.
     */
    public void _showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Settings");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
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
     * Event triggered when clicking on the item having click listener. When clicking on create
     * complaint button, _addComplaint() will be called When clicking on location icon, MapActivity
     * is started. When clicking on add photo icon, call to _openDialog() method. open dialog method
     * has two options, add photo from gallery and camera. When clicking on from_gallery
     * _openGalleryImages() will be called. When clicking on from_camera _openCamera() will be
     * called. When clicking on complaint_type FreqComplaintTypeActivity is started.
     */

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.complaint_doComplaint:
                _addComplaint();
                break;
            case R.id.complaint_location_icon:
            	if(isCurrentLocation && getGpsStatus() && (latitude==0.0f && longitute ==0.0f))
            	{
            		progressDialog.setMessage("Please, wait...");
            		progressDialog.show();
            		gpsTimeOutSec=30;//12 sec timeout
            		scheduleTimerTask();
            		
            	}
            	else
            	{
            		Intent intent = new Intent(this, MapActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitute);
                    startActivityForResult(intent, GET_LOCATION);
            	}
                
                break;
            case R.id.add_photo:
                _openDialog();
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

    public class gpsTimeOutTask extends TimerTask
    {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(latitude!=0.0f && longitute!=0.0f)
			{
				if(progressDialog.isShowing())
				{
					progressDialog.dismiss();
				}
				startMapActivitity();
			}
			else if(gpsTimeOutSec > 0)
			{
			  gpsTimeOutSec--;
			  scheduleTimerTask();
			}
			else
			{
				if(progressDialog.isShowing())
				{
					progressDialog.dismiss();
				}
				startMapActivitity();
			}
		}
    }
    
    private void scheduleTimerTask()
    {
    	gpsTimeOutTimer=new Timer();
    	gpsTimeOutTimer.schedule(new gpsTimeOutTask(), 1000);
    }
    
    private void startMapActivitity()
    {
    	Intent intent = new Intent(CreateComplaintActivity.this, MapActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitute);
        startActivityForResult(intent, GET_LOCATION);
    }
    
    /**
     * Function called when clicking on add photo. Used to show the options where to pick the image,
     * i.e, from gallery or camera
     */
    private void _openDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_upload_dialog);
        ((LinearLayout) dialog.findViewById(R.id.from_gallery)).setOnClickListener(this);
        ((LinearLayout) dialog.findViewById(R.id.from_camera)).setOnClickListener(this);
        dialog.show();
    }

    /**
     * Function called when choose the gallery option. Start the implicit intent ACTION_PICK for
     * result.
     */
    private void _openGalleryImages() {
        Intent photo_picker = new Intent(Intent.ACTION_PICK);
        photo_picker.setType("image/*");
        startActivityForResult(photo_picker, FROM_GALLERY);
    }

    /**
     * Function called when choosing the camera option. Start the implicit intent
     * ACTION_IMAGE_CAPTURE for result.
     */
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

    /**
     * Function used to copy the file to complaint folder from gallery
     * 
     * @param path
     *            => image file path from gallery
     */
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

    /**
     * Function used to delete the file
     * 
     * @param path
     *            => file path
     */
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

    /**
     * Function used to order the images if any one is deleted in between the image list
     */
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

    /**
     * Event triggered when an action is completed in another activity(request from this activity)).
     * We have checked the request code value If the request code value is equal to the
     * CAPTURE_IMAGE value and RESULT_OK then _validateImageUrl will be called. If the request code
     * value is equal to the FROM_GALLERY then uri of the selected image file from gallery is stored
     * to the variable selectedImage and the selected image is accessed through the ContentProvider
     * object.The ContentResolver object communicates with the ContentProvider object and the result
     * is sent to the cursor object,the cursor object contains the imagepath. _createImageFile() is
     * called to store the selected gallery image to the complaint photos directory on the storage
     * device.If the request code value is equal to the GET_LOCATION then latitude,longitude are
     * retrieved from the map activity then the location is displayed to the create complaint
     * layout.
     */
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

    /**
     * Function used to check file extension before adding it to complaint
     * 
     * @param filePath
     * @return
     */
    private boolean checkFileExtension(String filePath) {
        String fileType = (String) getConfig().get("upload.file.type", "");
        Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(" + fileType + "))$)");
        Matcher mtch = fileExtnPtrn.matcher(filePath);
        if (mtch.matches()) {
            return true;
        }
        return false;
    }

    /**
     * Function used to validate the image file before upload. Here we have checked the file
     * extension, size and count
     * 
     * @param imagePath
     */
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

            if (file_upload_limit == 1) {
                _getLatAndLng(imagePath);
            }
        }
    }

    private void _getLatAndLng(String imageUrl) {
        try {
            double lat = 0.0;
            double lng = 0.0;
            ExifInterface exif = new ExifInterface(imageUrl);
            String attrLatitute = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String attrLatituteRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String attrLONGITUDEREf = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if (attrLatitute == null && attrLONGITUDE == null) {
                return;
            }

            if (attrLatituteRef.equals("N")) {
                lat = convertToDegree(attrLatitute);
            }

            if (attrLONGITUDEREf.equals("E")) {
                lng = convertToDegree(attrLONGITUDE);
            }
            isCurrentLocation=false;
            _getCurrentLocation(lat, lng);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void _getCurrentLocation(double lat, double lng) {
       if(location.getText().toString().trim().length()==0 && isCurrentLocation)
       {
    	 latitude = lat;
         longitute = lng;
         String cityName = getCurrentLocation(lat, lng);
         location.setText(cityName);
       }
       else
       {
    	   latitude = lat;
           longitute = lng;
           String cityName = getCurrentLocation(lat, lng);
           location.setText(cityName);
       }
    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;

    }

    /**
     * Function used to show the image added to complaint in image view. A close icon is shown at
     * the top right corner of the image to delete it
     * 
     * @param imagePath
     */
    @SuppressLint("InflateParams")
    private void _addImageView(String imagePath) {
        final ImageView image_container = (ImageView) findViewById(R.id.image_container);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_photo, null);

        RelativeLayout inner_container = (RelativeLayout) view.findViewById(R.id.inner_container);
        LinearLayout.LayoutParams inner_container_params = new LinearLayout.LayoutParams(
                _dpToPix(100), _dpToPix(100));

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

    /**
     * Function used to decode the file(for memory consumption) and return the bitmap to show it in
     * image view
     * 
     * @param path
     *            => image file path
     * @return bitmap
     */
    private Bitmap _getBitmapImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    /**
     * Function used to convert dp unit to pixel unit
     * 
     * @param value
     *            => dp value
     * @return pixel value
     */
    private int _dpToPix(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources()
                .getDisplayMetrics());
    }

    /**
     * Function to set the data to auto complete text view component to show the location
     * 
     * @param list
     */
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

    /**
     * The onResponse method will be invoked after the API call onResponse methods will contain the
     * response If the response has a status as 'success' then we have checked whether the access
     * token is valid or not If the access token is invalid ,redirect to login page.
     */
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

    /**
     * Function used to upload the images attached in a complaint to server.
     * 
     * @param id
     *            => complaint id
     */
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
                            "INSERT INTO tbl_jobs(data, status, type, triedCount) values ('"
                                    + jo.toString() + "', 'waiting', 'upload', 0)");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        ServiceController.getInstance().startJobs();
        finish();
    }

    /**
     * Function called when clicking on create complaint
     */
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
        } /*else if (isEmpty(landmark)) {
            showMessage(getMessage(R.string.landmark_empty));
            return;
        }*/ /** Disabled landmark field required validation **/

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

    /**
     * Event triggered on auto complete text view's text change. When the text length is 3, call the
     * api to get the locations
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 3) {
            ApiController.getInstance().getLocationByName(this, s.toString());
        }
        if (s.length() == 0) {
            latitude = 0;
            longitute = 0;
        }
    }

    /**
     * Event triggered when clicking on any location from the list.
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        try {
            latitude = 0.0;
            longitute = 0.0;
            locationId = list.get(position).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    private final LocationListener gpsLocationListener =new LocationListener(){

        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d(TAG,"GPS available again\n");
                break;
            case LocationProvider.OUT_OF_SERVICE:
            	Log.d(TAG,"GPS out of service\n");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
            	Log.d(TAG,"GPS temporarily unavailable\n");
                break;
            }
        }

        public void onProviderEnabled(String provider) {
        	Log.d(TAG,"GPS Provider Enabled\n");
        }

        public void onProviderDisabled(String provider) {
        	Log.d(TAG,"GPS Provider Disabled\n");
        }

        @Override
        public void onLocationChanged(Location location) {
        	Log.d(TAG,"New gps location: "
                    + String.format("%9.6f", location.getLatitude()) + ", "
                    + String.format("%9.6f", location.getLongitude()) + "\n");
        	_getCurrentLocation(location.getLatitude(), location.getLongitude());
        	locationManager.removeUpdates(this);
        }

    };
    
    private final LocationListener networkLocationListener = new LocationListener(){

        public void onStatusChanged(String provider, int status, Bundle extras){
            switch (status) {
            case LocationProvider.AVAILABLE:
            	Log.d(TAG,"Network location available again\n");
                break;
            case LocationProvider.OUT_OF_SERVICE:
            	Log.d(TAG,"Network location out of service\n");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
            	Log.d(TAG,"Network location temporarily unavailable\n");
                break;
            }
        }

        public void onProviderEnabled(String provider) {
        	Log.d(TAG,"Network Provider Enabled\n");
        }

        public void onProviderDisabled(String provider) {
        	Log.d(TAG,"Network Provider Disabled\n");
        }

        @Override
        public void onLocationChanged(Location location) {
        	Log.d(TAG,"New network location: "
                    + String.format("%9.6f", location.getLatitude()) + ", "
                    + String.format("%9.6f", location.getLongitude()) + "\n");
        	_getCurrentLocation(location.getLatitude(), location.getLongitude());
        	locationManager.removeUpdates(this);
        	
        }
    };
    
    public String getCurrentLocation(double lat, double lng) {

        String cityName = "";

        if (lat == 0 && lng == 0) {
            return "";
        }

        Geocoder geocoder = new Geocoder(CreateComplaintActivity.this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                cityName=address.getThoroughfare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }
    
    public boolean getGpsStatus() {
    	boolean gpsStatus=false;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsStatus = false;
        } else {
            gpsStatus = true;
        }
        return gpsStatus;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(isCurrentLocation)
    	{
	    	locationManager.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, 0, 0,
	                networkLocationListener);
	        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
	                0, 0, gpsLocationListener);
    	}
    	
    };
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	locationManager.removeUpdates(networkLocationListener);
        locationManager.removeUpdates(gpsLocationListener);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }
    
}
