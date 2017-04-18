/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.helper.CustomAutoCompleteTextView;
import org.egovernments.egoverp.helper.ImageCompressionHelper;
import org.egovernments.egoverp.helper.NoFilterAdapter;
import org.egovernments.egoverp.helper.UriPathHelper;
import org.egovernments.egoverp.models.GrievanceLocation;
import org.egovernments.egoverp.models.GrievanceLocationAPIResponse;
import org.egovernments.egoverp.models.GrievanceRequest;
import org.egovernments.egoverp.models.GrievanceType;
import org.egovernments.egoverp.models.GrievanceTypeAPIResponse;
import org.egovernments.egoverp.models.GrievanceTypeCategory;
import org.egovernments.egoverp.services.AddressService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * The Grievance creation page activity
 **/
//TODO frequent types

public class NewGrievanceActivity extends BaseActivity {

    //Codes used to start image picker tasks
    private static final int CAMERA_PHOTO = 111;
    private static final int GALLERY_PHOTO = 222;
    final private int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 456;
    final private int REQUEST_CODE_ASK_PERMISSIONS_READ_ACCESS = 789;
    final private int REQUEST_CODE_ASK_COMPLAINT_LOCATION = 777;
    int complaintTypeID;
    ImageView imgMapPick;
    ImageView imgClear;
    private List<GrievanceType> grievanceAllTypes = new ArrayList<>(); //stores all complaint types
    private List<GrievanceTypeCategory> grievanceAllCategories = new ArrayList<>(); //stores all complaint categories
    private List<String> grievanceTypes = new ArrayList<>(); //stores grievance types string for adapter
    private List<String> grievanceTypeCategories = new ArrayList<>(); //stores grievance categories string for adapter
    private Dialog imagePickerDialog;
    private AutoCompleteTextView autoCompleteComplaintLoc;
    private CustomAutoCompleteTextView autocompleteComplaintType;
    private CustomAutoCompleteTextView autoCompleteComplaintCategory;
    private List<GrievanceLocation> grievanceLocations;
    private int locationID = 0;
    private LatLng complaintLocLatLng;
    private EditText landmark;
    BroadcastReceiver addressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            String addressResult = intent.getStringExtra(AddressService.KEY_ADDRESS);
                            if (TextUtils.isEmpty(addressResult)) {
                                complaintLocLatLng = null;
                                showSnackBar(R.string.complaint_location_message);
                            } else {
                                autoCompleteComplaintLoc.setText(addressResult);
                            }
                            landmark.requestFocus();
                        }
                    });

        }
    };
    private EditText details;
    private int uploadCount = 0;
    //Used as to maintain unique image IDs
    private ArrayList<String> imageID = new ArrayList<>(Arrays.asList("1", "2", "3"));
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ViewPager viewPager;
    private GrievanceImagePagerAdapter grievanceImagePagerAdapter;
    private File cacheDir;
    private boolean isPickedLocationFromMap=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.newgrievance_label);
        setContentView(R.layout.activity_new_grievance);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cacheDir = this.getExternalCacheDir() == null ? this.getCacheDir() : this.getExternalCacheDir();

        final FloatingActionButton pictureFab = (FloatingActionButton) findViewById(R.id.picture_add);

        autocompleteComplaintType = (CustomAutoCompleteTextView) findViewById(R.id.autoCompleteComplaintType);
        autoCompleteComplaintCategory = (CustomAutoCompleteTextView) findViewById(R.id.autoCompleteComplaintCategory);

        autocompleteComplaintType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar(getString(R.string.fetch_complaint_list));
            }
        });

        autocompleteComplaintType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                NewGrievanceActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
                return false;
            }
        });

        viewPager = (ViewPager) findViewById(R.id.upload_complaint_image);
        grievanceImagePagerAdapter = new GrievanceImagePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(grievanceImagePagerAdapter);
        /*LinePageIndicator linePageIndicator = (LinePageIndicator) findViewById(R.id.new_indicator);
        linePageIndicator.setViewPager(viewPager);*/

        landmark = (EditText) findViewById(R.id.complaint_landmark);
        details = (EditText) findViewById(R.id.complaint_details);

        imgMapPick = (ImageView) findViewById(R.id.imgMapPick);
        imgMapPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGrievancePickLoc=new Intent(NewGrievanceActivity.this, GrievanceLocPickerActivity.class);
                if(complaintLocLatLng!=null) {
                    openGrievancePickLoc.putExtra(GrievanceLocPickerActivity.DEFAULT_LOCATION_LAT, complaintLocLatLng.latitude);
                    openGrievancePickLoc.putExtra(GrievanceLocPickerActivity.DEFAULT_LOCATION_LNG, complaintLocLatLng.longitude);
                }
                startActivityForResult(openGrievancePickLoc, REQUEST_CODE_ASK_COMPLAINT_LOCATION);
            }
        });

        imgClear=(ImageView)findViewById(R.id.imgClear);
        if(imgClear!=null) {
            imgClear.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            imgClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    complaintLocLatLng = null;
                    locationID = 0;
                    autoCompleteComplaintLoc.setText("");
                }
            });
        }

        autoCompleteComplaintLoc = (AutoCompleteTextView) findViewById(R.id.complaint_locationname);
        if(autoCompleteComplaintLoc!=null)
        autoCompleteComplaintLoc.setThreshold(3);
        autoCompleteComplaintLoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadComplaintLocationSuggestion(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteComplaintLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteComplaintLoc.setSelection(0);
                landmark.requestFocus();
                //Clear the complaint location from map is selected
                complaintLocLatLng=null;
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadComplaintCategoriesAndTypes();
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadCount < 3) {
                    imagePickerDialog = new Dialog(NewGrievanceActivity.this);
                    imagePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    imagePickerDialog.setContentView(R.layout.dialog_upload);
                    imagePickerDialog.setCanceledOnTouchOutside(true);


                    imagePickerDialog.findViewById(R.id.from_gallery).setOnClickListener(new View.OnClickListener() {
                        //Opens default gallery app
                        @Override
                        public void onClick(View v) {

                            if (Build.VERSION.SDK_INT < 23) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, GALLERY_PHOTO);
                            } else {
                                if (checkReadAccessPermission()) {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, GALLERY_PHOTO);
                                }
                            }

                            imagePickerDialog.dismiss();

                        }
                    });

                    imagePickerDialog.findViewById(R.id.from_camera).setOnClickListener(new View.OnClickListener() {
                        //Opens default camera app
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT < 23) {
                                fromCamera();
                            } else {
                                if (checkCameraPermission()) {
                                    fromCamera();
                                }
                            }

                            imagePickerDialog.dismiss();

                        }
                    });

                    imagePickerDialog.show();
                } else {
                    showSnackBar(getString(R.string.limit_3_image));
                }

            }
        };

         if(pictureFab!=null)
         pictureFab.setOnClickListener(onClickListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(NewGrievanceActivity.this).registerReceiver(addressReceiver,
                new IntentFilter(AddressService.BROADCAST_ADDRESS_RECEIVER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(NewGrievanceActivity.this).unregisterReceiver(addressReceiver);
    }

    private void loadComplaintLocationSuggestion(CharSequence s) {

        if (autoCompleteComplaintLoc.getText().toString().length() > 0) {
            imgClear.setVisibility(View.VISIBLE);
        } else {
            imgClear.setVisibility(View.GONE);
        }

        //used to skip below statements when populate address from maps
        if (isPickedLocationFromMap) {
            isPickedLocationFromMap = false;
            locationID = 0;
            autoCompleteComplaintLoc.setAdapter(null);
            return;
        } else { //if any text change detected from autocomplete text
            complaintLocLatLng = null;
        }

        if (s.length() >= 3) {

            if (autoCompleteComplaintLoc.getAdapter() != null) {
                int pos = ((NoFilterAdapter) autoCompleteComplaintLoc.getAdapter()).getItems().indexOf(autoCompleteComplaintLoc.getText().toString());
                if (pos >= 0) {
                    locationID = grievanceLocations.get(pos).getId();
                    return;
                } else {
                    locationID = 0;
                }
            }

            Call<GrievanceLocationAPIResponse> grievanceLocationAPIResponseCall = ApiController.getRetrofit2API(getApplicationContext())
                    .getComplaintLocation(s.toString(), sessionManager.getAccessToken());

            grievanceLocationAPIResponseCall.enqueue(new retrofit2.Callback<GrievanceLocationAPIResponse>() {
                @Override
                public void onResponse(Call<GrievanceLocationAPIResponse> call, retrofit2.Response<GrievanceLocationAPIResponse> response) {

                    GrievanceLocationAPIResponse grievanceLocationAPIResponse = response.body();

                    if (grievanceLocationAPIResponse == null) {
                        showSnackBar(R.string.invalid_response);
                        return;
                    }

                    grievanceLocations = new ArrayList<>();
                    grievanceLocations = grievanceLocationAPIResponse.getGrievanceLocation();

                    ArrayList<String> strings = new ArrayList<>();
                    try {

                        for (int i = 0; i < grievanceLocations.size(); i++) {
                            strings.add(grievanceLocations.get(i).getName());
                        }
                        NoFilterAdapter<String> adapter = new NoFilterAdapter<>(NewGrievanceActivity.this,
                                android.R.layout.select_dialog_item, strings);
                        autoCompleteComplaintLoc.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GrievanceLocationAPIResponse> call, Throwable t) {

                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_complaint_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_regcomplaint:
                validateAndSubmitGrievance();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //validate and registerComplaint grievance
    public void validateAndSubmitGrievance()
    {
        String complaintDetails = details.getText().toString().trim();
        String landmarkDetails = landmark.getText().toString().trim();

        complaintTypeID =0;

        for(GrievanceType grievanceType:grievanceAllTypes)
        {
            if (autocompleteComplaintType.getText().toString().trim().toUpperCase()
                    .equals(getGrievanceTypeNameByLocale(grievanceType).trim().toUpperCase()))
            {
                complaintTypeID =grievanceType.getId();
            }
        }

        if (complaintTypeID == 0) {
            showSnackBar(getString(R.string.please_select_complaint_type));
        }
        else if (locationID == 0 && (complaintLocLatLng == null)) {
            showSnackBar(getString(R.string.please_select_location_map_or_dropdown));
        }else if (TextUtils.isEmpty(complaintDetails) || complaintDetails.length() < 10) {
            showSnackBar(getString(R.string.complaint_details_10_chars));
        } else {
            GrievanceRequest grievanceRequest;
            if (complaintLocLatLng != null) {
                grievanceRequest=new GrievanceRequest(complaintLocLatLng.latitude, complaintLocLatLng.longitude, complaintDetails, complaintTypeID, landmarkDetails);
            } else {
                grievanceRequest=new GrievanceRequest(locationID, complaintDetails, complaintTypeID, landmarkDetails);
            }
            progressDialog.show();
            registerComplaint(grievanceRequest);
        }
    }

    //Prepares files for camera before starting it
    private void fromCamera() {

        File file = new File(cacheDir, "POST_IMAGE_" + imageID.get(0) + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CAMERA_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //If result is from camera
        if (requestCode == CAMERA_PHOTO && resultCode == Activity.RESULT_OK) {

            File savedImg=new File(cacheDir, "POST_IMAGE_" + imageID.get(0) + ".jpg");
            //Stores image in app cache
            Uri uri = Uri.fromFile(savedImg);

            //apply image compression
            ImageCompressionHelper.compressImage(savedImg.getAbsolutePath(), savedImg.getAbsolutePath());

            uriArrayList.add(uri);
            getContentResolver().notifyChange(uriArrayList.get(uriArrayList.size() - 1), null);

            grievanceImagePagerAdapter.notifyDataSetChanged();
            uploadCount++;

            if (uploadCount == 1) {
                loadComplaintLocationFromImage(uri);
            }

            imageID.remove(0);
            viewPager.setCurrentItem(uriArrayList.size());
        }

        //If result is from gallery
        else if (requestCode == GALLERY_PHOTO && resultCode == Activity.RESULT_OK) {
            uriArrayList.add(data.getData());
            grievanceImagePagerAdapter.notifyDataSetChanged();
            uploadCount++;
            imageID.remove(0);
            viewPager.setCurrentItem(uriArrayList.size());
            if (uploadCount == 1) {
                loadComplaintLocationFromImage(data.getData());
            }

        } else if (requestCode == REQUEST_CODE_ASK_COMPLAINT_LOCATION) {

            if(resultCode == RESULT_OK) {
                //reset location id
                locationID = 0;

                Double complaintLocLat = data.getDoubleExtra(GrievanceLocPickerActivity.SELECTED_LOCATION_LAT, 0d);
                Double complaintLocLng = data.getDoubleExtra(GrievanceLocPickerActivity.SELECTED_LOCATION_LNG, 0d);

                complaintLocLatLng = new LatLng(complaintLocLat, complaintLocLng);
                isPickedLocationFromMap = true; //to avoid to load suggestion list when set to Autocomplete

                String selectedLocAddress = data.getStringExtra(GrievanceLocPickerActivity.SELECTED_LOCATION_ADDRESS);

                if (!TextUtils.isEmpty(selectedLocAddress)) {
                    autoCompleteComplaintLoc.setText(selectedLocAddress);
                    landmark.requestFocus();
                } else {
                    getAndSetAddressToLocAutoComplete(complaintLocLat, complaintLocLng);
                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                showSnackBar(R.string.complaint_location_message);
            }

        }

    }

    //Invokes call to API
    private void registerComplaint(GrievanceRequest grievanceRequest) {

        Map<String, RequestBody> formDatas = new HashMap<>();

        //Used to upload multiple multipart parts with the same field name
        for (Uri uploadDoc : uriArrayList) {
            String mimeType = getMimeType(uploadDoc);
            String path;
            path = UriPathHelper.getRealPathFromURI(uploadDoc, getApplicationContext());
            path = ImageCompressionHelper.compressImage(path, path);
            File uploadFile = new File(path);
            RequestBody fileBody = RequestBody.create(MediaType.parse(mimeType), new File(path));
            formDatas.put("files\"; filename=\"" + uploadFile.getName(), fileBody);
        }

        RequestBody complaintDetails = RequestBody.create(MediaType.parse("text/plain"), new Gson().toJson(grievanceRequest));

        formDatas.put("json_complaint", complaintDetails);

        Call<JsonObject> createComplaintCall = ApiController.getRetrofit2API(getApplicationContext())
                .createComplaint(sessionManager.getAccessToken(), formDatas);

        if (validateInternetConnection()) {
            createComplaintCall.enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    progressDialog.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra(GrievanceActivity.RESULT_MESSAGE, getString(R.string.grievance_registered));
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }

    }

    private void loadComplaintCategoriesAndTypes()
    {

        setLoadingHintCustomAutoComplete(autoCompleteComplaintCategory);
        setLoadingHintCustomAutoComplete(autocompleteComplaintType);

        //Retrieves the list of complaint to populate dropdown. Dropdown is empty until it succeeds
        Call<GrievanceTypeAPIResponse> grievanceTypeAPIResponseCall = ApiController.getRetrofit2API(getApplicationContext())
                .getComplaintCategoryAndTypes(sessionManager.getAccessToken());

        grievanceTypeAPIResponseCall.enqueue(new retrofit2.Callback<GrievanceTypeAPIResponse>() {
            @Override
            public void onResponse(Call<GrievanceTypeAPIResponse> call, retrofit2.Response<GrievanceTypeAPIResponse> response) {

                GrievanceTypeAPIResponse grievanceTypeAPIResponse = response.body();

                if (grievanceTypeAPIResponse == null) {
                    showSnackBar(R.string.invalid_response);
                    return;
                }

                grievanceAllCategories = grievanceTypeAPIResponse.getResult().getGrievanceTypeCategories();
                grievanceTypeCategories = new ArrayList<>();
                grievanceTypes = new ArrayList<>();

                for (GrievanceTypeCategory grievanceTypeCategory : grievanceAllCategories) {
                    grievanceTypeCategories.add(getGrievanceCategoryNameByLocale(grievanceTypeCategory));
                    grievanceAllTypes.addAll(grievanceTypeCategory.getGrievanceTypes());

                    for (GrievanceType grievanceType : grievanceTypeCategory.getGrievanceTypes()) {
                        grievanceTypes.add(getGrievanceTypeNameByLocale(grievanceType));
                    }
                }

                final ArrayAdapter<String> adapterGrievanceCategories = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, grievanceTypeCategories);
                final ArrayAdapter<String> adapterGrievanceTypes = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, grievanceTypes);

                setCustomAutoCompleteTextViewWithAdapter(autoCompleteComplaintCategory, R.string.complaint_category, adapterGrievanceCategories);
                setCustomAutoCompleteTextViewWithAdapter(autocompleteComplaintType, R.string.complaint_type_text, adapterGrievanceTypes);

                final Runnable nextFocusRunnable = new Runnable() {
                    @Override
                    public void run() {
                        refreshGrievanceTypeAutoComplete(true);
                    }
                };

                final Handler focusOutHandler = new Handler();

                autoCompleteComplaintCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            if (TextUtils.isEmpty(autoCompleteComplaintCategory.getText())) {
                                focusOutHandler.postDelayed(nextFocusRunnable, 100);
                            }
                        }
                    }
                });

                autocompleteComplaintType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            focusOutHandler.removeCallbacks(nextFocusRunnable);
                            refreshGrievanceTypeAutoComplete(false);
                        }
                    }
                });

                autoCompleteComplaintCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        autoCompleteComplaintCategory.setSelection(0);
                        refreshGrievanceTypeAutoComplete(true);
                        autocompleteComplaintType.requestFocus();
                    }
                });

                autocompleteComplaintType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setGrievanceCategoryText();
                        autocompleteComplaintType.setSelection(0);
                        autoCompleteComplaintLoc.requestFocus();
                    }
                });

            }

            @Override
            public void onFailure(Call<GrievanceTypeAPIResponse> call, Throwable t) {
                setLoadingFailedHintCustomAutoComplete(autoCompleteComplaintCategory);
                setLoadingFailedHintCustomAutoComplete(autocompleteComplaintType);
            }
        });

    }

    private void setLoadingHintCustomAutoComplete(final CustomAutoCompleteTextView customAutoComplete)
    {
        customAutoComplete.setOnClickListener(null);
        customAutoComplete.setHint(R.string.loading_label);
        customAutoComplete.setCompoundDrawables(null, null, null, null);
    }

    private void setLoadingFailedHintCustomAutoComplete(final CustomAutoCompleteTextView customAutoComplete)
    {
        customAutoComplete.setHint(R.string.loading_failed);
        customAutoComplete.setDrawableClickListener(null);
        customAutoComplete.setOnClickListener(null);
        customAutoComplete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_refresh_black_24dp, 0);
        customAutoComplete.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {
                    loadComplaintCategoriesAndTypes();
                }
            }
        });
    }

    private void refreshGrievanceTypeAutoComplete(boolean isClearComplaintType)
    {

        String complaintCategoryTypedText=autoCompleteComplaintCategory.getText().toString();

        ArrayList<String> tempGrievanceTypes=new ArrayList<>();

        int selectedIdx=-1;
        int idx=0;
        for(GrievanceTypeCategory grievanceTypeCategory:grievanceAllCategories)
        {
            if (getGrievanceCategoryNameByLocale(grievanceTypeCategory).toUpperCase()
                    .equals(complaintCategoryTypedText.toUpperCase()))
            {
                selectedIdx=idx;
                grievanceTypes.clear();
                if(isClearComplaintType)
                {
                    autocompleteComplaintType.setText("");
                }
                for(GrievanceType grievanceType:grievanceTypeCategory.getGrievanceTypes())
                {
                    grievanceTypes.add(getGrievanceTypeNameByLocale(grievanceType));
                }
                break;
            }

            for(GrievanceType grievanceType:grievanceTypeCategory.getGrievanceTypes())
            {
                tempGrievanceTypes.add(getGrievanceTypeNameByLocale(grievanceType));
            }

            idx++;
        }

        if(selectedIdx==-1)
        {
            autocompleteComplaintType.setText("");
            autoCompleteComplaintCategory.setText("");
            grievanceTypes.clear();
            grievanceTypes.addAll(tempGrievanceTypes);
        }

        final ArrayAdapter<String> adapterGrievanceTypes = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, grievanceTypes);
        setCustomAutoCompleteTextViewWithAdapter(autocompleteComplaintType, R.string.complaint_type_text, adapterGrievanceTypes);

    }

    private void setGrievanceCategoryText() {

        ArrayList<String> tempGrievanceTypes=new ArrayList<>();
        boolean isPickedCategory=false;
        String complaintTypeText = autocompleteComplaintType.getText().toString();
        for(GrievanceTypeCategory grievanceTypeCategory:grievanceAllCategories)
        {
            if(isPickedCategory)
            {
                break;
            }
            tempGrievanceTypes.clear();
            for(GrievanceType grievanceType:grievanceTypeCategory.getGrievanceTypes())
            {
                tempGrievanceTypes.add(getGrievanceTypeNameByLocale(grievanceType));
                if (getGrievanceTypeNameByLocale(grievanceType).toUpperCase().equals(complaintTypeText.toUpperCase()))
                {
                    autoCompleteComplaintCategory.setText(getGrievanceCategoryNameByLocale(grievanceTypeCategory));
                    isPickedCategory=true;
                }
            }
        }

        if(isPickedCategory)
        {
            grievanceTypes.clear();
            grievanceTypes.addAll(tempGrievanceTypes);
        }

        final ArrayAdapter<String> adapterGrievanceCategories = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, grievanceTypeCategories);
        setCustomAutoCompleteTextViewWithAdapter(autoCompleteComplaintCategory, R.string.complaint_category, adapterGrievanceCategories);

        final ArrayAdapter<String> adapterGrievanceTypes = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, grievanceTypes);
        setCustomAutoCompleteTextViewWithAdapter(autocompleteComplaintType, R.string.complaint_type_text, adapterGrievanceTypes);

    }

    private void setCustomAutoCompleteTextViewWithAdapter(final CustomAutoCompleteTextView autoCompleteTextView, int hint, ArrayAdapter<?> adapter)
    {
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setHint(hint);
        autoCompleteTextView.setOnClickListener(null);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
        autoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {
                    autoCompleteTextView.showDropDown();
                    autoCompleteTextView.requestFocus();
                }
            }
        });
    }

    //Function converts lat/lng value from exif data to degrees
    private Double convertToDegree(String stringDMS) {
        Double result;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = Double.valueOf(stringD[0]);
        Double D1 = Double.valueOf(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = Double.valueOf(stringM[0]);
        Double M1 = Double.valueOf(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = Double.valueOf(stringS[0]);
        Double S1 = Double.valueOf(stringS[1]);
        Double FloatS = S0 / S1;

        result = FloatD + (FloatM / 60) + (FloatS / 3600);

        return result;


    }

    //Returns the mime type of file. If it cannot be resolved, assumed to be jpeg
    private String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = NewGrievanceActivity.this.getContentResolver();
            mimeType = contentResolver.getType(uri);
            return mimeType;
        }
        return "image/jpeg";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    fromCamera();
                } else {
                    showSnackBar(R.string.permission_camera_denied);
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_READ_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_PHOTO);
                } else {
                    showSnackBar(R.string.permission_read_denied);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void loadComplaintLocationFromImage(Uri imageUri) {
        try {
            String s = UriPathHelper.getRealPathFromURI(imageUri, this);
            ExifInterface exifInterface = new ExifInterface(s);
            double lat;
            double lng;
            try {
                lat = convertToDegree(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                lng = convertToDegree(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
            } catch (Exception e) {
                lat = 0;
                lng = 0;
            }
            if (lat != 0 && lng != 0) {
                complaintLocLatLng=new LatLng(lat, lng);
                locationID = 0;
                getAndSetAddressToLocAutoComplete(lat,lng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkCameraPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CAMERA);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
            return false;
        }
        return true;
    }


    /*//find selected location id from location collections
    public GrievanceLocation getGrievanceLocationByName(String complaintLocationName) {
        for (GrievanceLocation complaintLocation : grievanceLocations) {
            if (complaintLocation.getName().equals(complaintLocationName)) {
                return complaintLocation;
            }
        }
        return null;
    }*/

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkReadAccessPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS_READ_ACCESS);
            return false;
        }
        return true;
    }

    /*/If lat/lng is available attempt to resolve it to an address*/
    private void getAndSetAddressToLocAutoComplete(Double lat, Double lng) {
        progressDialog.show();
        Intent intent = new Intent(this, AddressService.class);
        intent.putExtra(AddressService.LAT, lat);
        intent.putExtra(AddressService.LNG, lng);
        startService(intent);
    }

    String getGrievanceCategoryNameByLocale(GrievanceTypeCategory grievanceTypeCategory) {
        if (!isCurrentLocaleEnglish()) {
            String localCategoryName = grievanceTypeCategory.getLocalName();
            return TextUtils.isEmpty(localCategoryName) ? grievanceTypeCategory.getCategoryName() : localCategoryName;
        } else
            return grievanceTypeCategory.getCategoryName();
    }

    String getGrievanceTypeNameByLocale(GrievanceType grievanceType) {
        if (!isCurrentLocaleEnglish()) {
            String localGrievanceTypeName = grievanceType.getLocalName();
            return TextUtils.isEmpty(localGrievanceTypeName) ? grievanceType.getName() : localGrievanceTypeName;
        } else
            return grievanceType.getName();
    }

    //Interface defined to be able to invoke function in fragment class. May be unnecessary
    public interface RemoveImageInterface {
        void removeFragmentImage(int position, UploadImageFragment fragment);
    }

    //The fragments of the viewpager
    public static class UploadImageFragment extends Fragment {
        RemoveImageInterface removeInf = null;
        Integer fragmentPosition = -1;
        Uri uri = null;


        public UploadImageFragment() {
        }


        @SuppressLint("ValidFragment")
        public UploadImageFragment(RemoveImageInterface removeInf, Uri uri) {
            this.removeInf = removeInf;
            this.uri = uri;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_upload_image, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_viewpager_item);

            ImageView cancel_button = (ImageView) view.findViewById(R.id.viewpager_cancel);

            //Draws the cancel icon in top right corner
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_cancel_white_24dp);
            drawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            cancel_button.setImageDrawable(drawable);

            Bundle arg = this.getArguments();

            //Generates a thumbnail of image to be displayed in the viewpager. The original image is unaffected.
            Bitmap ThumbImage = null;
            try {
                /*ThumbImage = ThumbnailUtils
                        .extractThumbnail(MediaStore.Images.Media.getBitmap
                                (getActivity().getContentResolver(),
                                        Uri.parse(arg.getString("uri"))), 1280, 720);*/
                ThumbImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(arg.getString("uri")));
            } catch (IOException e) {

                e.printStackTrace();
            }


            if (ThumbImage != null) {
                ThumbImage = Bitmap.createScaledBitmap(ThumbImage, ThumbImage.getWidth(), ThumbImage.getHeight(), true);
            } else {
                Toast toast = Toast.makeText(getActivity(), R.string.error_while_retrieve_image, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }

            imageView.setImageBitmap(ThumbImage);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            fragmentPosition = arg.getInt("pos");

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    removeInf.removeFragmentImage(fragmentPosition, UploadImageFragment.this);

                }
            });

            return view;

        }

    }

    //Custom adapter for viewpager
    private class GrievanceImagePagerAdapter extends FragmentStatePagerAdapter implements RemoveImageInterface {

        GrievanceImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getItemPosition(Object object) {
            // Returning none cause all fragments to be refreshed when data set changed. Is memory intensive
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putString("uri", (uriArrayList.get(position)).toString());
            args.putInt("pos", position);
            UploadImageFragment fragment = new UploadImageFragment(this, uriArrayList.get(position));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return uriArrayList.size();
        }

        @Override
        public void removeFragmentImage(int position, UploadImageFragment fragment) {

            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            uriArrayList.remove(position);
            uploadCount--;

            imageID.add(String.valueOf(position + 1));

            this.notifyDataSetChanged();

            viewPager.setCurrentItem(position);

        }
    }

}