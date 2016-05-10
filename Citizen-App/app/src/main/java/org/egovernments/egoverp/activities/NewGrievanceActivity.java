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
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.events.AddressReadyEvent;
import org.egovernments.egoverp.helper.CustomAutoCompleteTextView;
import org.egovernments.egoverp.helper.ImageCompressionHelper;
import org.egovernments.egoverp.helper.NoFilterAdapter;
import org.egovernments.egoverp.helper.UriPathHelper;
import org.egovernments.egoverp.models.GrievanceLocation;
import org.egovernments.egoverp.models.GrievanceLocationAPIResponse;
import org.egovernments.egoverp.models.GrievanceRequest;
import org.egovernments.egoverp.models.GrievanceType;
import org.egovernments.egoverp.models.GrievanceTypeAPIResponse;
import org.egovernments.egoverp.models.errors.ErrorResponse;
import org.egovernments.egoverp.network.AddressService;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * The Grievance creation page activity
 **/
//TODO frequent types

public class NewGrievanceActivity extends AppCompatActivity implements LocationListener {

    private List<GrievanceType> grievanceTypes = new ArrayList<>();

    private ProgressDialog progressDialog;

    private Dialog imagePickerDialog;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private AutoCompleteTextView autoCompleteComplaintLoc;

    private CustomAutoCompleteTextView autocompleteComplaintType;

    private SessionManager sessionManager;

    private List<GrievanceLocation> grievanceLocations;

    private int locationID = 0;
    private int typeID;

    private Marker marker;

    private EditText landmark;
    private EditText details;

    //Codes used to start image picker tasks
    private static final int CAMERA_PHOTO = 111;
    private static final int GALLERY_PHOTO = 222;

    private int uploadCount = 0;

    //Used as to maintain unique image IDs
    private ArrayList<String> imageID = new ArrayList<>(Arrays.asList("1", "2", "3"));

    private ArrayList<Uri> uriArrayList = new ArrayList<>();

    private ViewPager viewPager;

    private GrievanceImagePagerAdapter grievanceImagePagerAdapter;

    private File cacheDir;

    private GoogleMap googleMap;

    private int LOCATION_REQUEST_CODE = 333;

    final private int REQUEST_CODE_ASK_PERMISSIONS_LOCATION = 123;
    final private int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 456;
    final private int REQUEST_CODE_ASK_PERMISSIONS_READ_ACCESS = 789;

    private ImageView imgMapPick;
    private ImageView imgClear;

    private boolean isPickedLocationFromMap=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grievance);

        sessionManager = new SessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cacheDir = this.getExternalCacheDir() == null ? this.getCacheDir() : this.getExternalCacheDir();

        final FloatingActionButton pictureAddButton = (FloatingActionButton) findViewById(R.id.picture_add);
        final com.melnykov.fab.FloatingActionButton pictureAddButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.picture_addcompat);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final MapDialog locationPickerDialog = new MapDialog(NewGrievanceActivity.this);
        intializeDialogComponents(locationPickerDialog);

        if (!sessionManager.isDemoMode()) {

            if (Build.VERSION.SDK_INT < 23) {
                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else {
                    startLocationListener();
                }
            } else {

                if (checkLocationPermission()) {
                    startLocationListener();
                }
            }

        }

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCanceledOnTouchOutside(true);

        autocompleteComplaintType = (CustomAutoCompleteTextView) findViewById(R.id.grievancetype_spinner_placeholder);

        autocompleteComplaintType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(NewGrievanceActivity.this, "Fetching type list, please wait", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
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
                locationPickerDialog.setMapLocationMarker(marker);
                locationPickerDialog.show();
            }
        });

        imgClear=(ImageView)findViewById(R.id.imgClear);
        imgClear.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker=null;
                locationID=0;
                autoCompleteComplaintLoc.setText("");
            }
        });

        autoCompleteComplaintLoc = (AutoCompleteTextView) findViewById(R.id.complaint_locationname);
        autoCompleteComplaintLoc.setThreshold(3);
        autoCompleteComplaintLoc.addTextChangedListener(new TextWatcher() {
                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                            }

                                                            //When 2 or more characters are entered, API is called to provide a matching location name
                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start, int before, int count) {


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
                                                                    marker = null;
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

                                                                    ApiController.getAPI(NewGrievanceActivity.this).getComplaintLocation(s.toString(), sessionManager.getAccessToken(), new Callback<GrievanceLocationAPIResponse>() {
                                                                                @Override
                                                                                public void success(GrievanceLocationAPIResponse grievanceLocationAPIResponse, Response response) {

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

                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void failure(RetrofitError error) {
                                                                                    if (error.getLocalizedMessage() != null)
                                                                                        if (error.getLocalizedMessage().equals("Invalid access token")) {
                                                                                            Toast toast = Toast.makeText(NewGrievanceActivity.this, "Session expired", Toast.LENGTH_SHORT);
                                                                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                                            toast.show();
                                                                                            sessionManager.logoutUser();
                                                                                            startActivity(new Intent(NewGrievanceActivity.this, LoginActivity.class));
                                                                                        } else {
                                                                                            Toast toast = Toast.makeText(NewGrievanceActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                                                                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                                            toast.show();
                                                                                        }
                                                                                    else {
                                                                                        Toast toast = Toast.makeText(NewGrievanceActivity.this, "An unexpected error occurred while retrieving location", Toast.LENGTH_SHORT);
                                                                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                                        toast.show();
                                                                                    }
                                                                                }
                                                                            }

                                                                    );
                                                                }
                                                            }

                                                            @Override
                                                            public void afterTextChanged(Editable s) {
                                                            }
                                                        }

        );

        autoCompleteComplaintLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*String selection = (String)parent.getItemAtPosition(position);
                int pos=((NoFilterAdapter)autoCompleteComplaintLoc.getAdapter()).getItems().indexOf(selection);
                locationID = grievanceLocations.get(pos).getId();*/

                //Clears marker when a location is selected
                if (marker != null)
                    marker.remove();
                marker = null;
            }
        });

        //Retrieves the list of complaint to populate dropdown. Dropdown is empty until it succeeds
        ApiController.getAPI(NewGrievanceActivity.this).getComplaintTypes(sessionManager.getAccessToken(), new Callback<GrievanceTypeAPIResponse>() {
                    @Override
                    public void success(GrievanceTypeAPIResponse grievanceTypeAPIResponse, Response response) {
                        grievanceTypes = grievanceTypeAPIResponse.getGrievanceType();
                        final List<String> strings = new ArrayList<>();
                        for (int i = 0; i < grievanceTypes.size(); i++) {
                            strings.add(grievanceTypes.get(i).getName());
                        }

                        final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
                        autocompleteComplaintType.setHint(R.string.complaint_type_label);
                        autocompleteComplaintType.setOnClickListener(null);
                        autocompleteComplaintType.setAdapter(autoCompleteAdapter);
                        autocompleteComplaintType.setThreshold(1);
                        autocompleteComplaintType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                        autocompleteComplaintType.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                            @Override
                            public void onClick(DrawablePosition target) {
                                if (target == DrawablePosition.RIGHT) {
                                    autocompleteComplaintType.showDropDown();
                                }
                            }
                        });
                        autocompleteComplaintType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                             @Override
                                                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                                                 String s = autocompleteComplaintType.getText().toString();
                                                                                 for (GrievanceType grievanceType : grievanceTypes) {
                                                                                     if (s.equals(grievanceType.getName()))
                                                                                         typeID = grievanceType.getId();
                                                                                 }
                                                                             }
                                                                         }

                        );

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        autocompleteComplaintType.setOnClickListener(null);
                        autocompleteComplaintType.setHint("Loading Failed");
                        autocompleteComplaintType.setCompoundDrawables(null, null, null, null);
                        if (error.getLocalizedMessage() != null)
                            if (error.getLocalizedMessage().equals("Invalid access token")) {
                                Toast toast = Toast.makeText(NewGrievanceActivity.this, "Session expired", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                                sessionManager.logoutUser();
                                startActivity(new Intent(NewGrievanceActivity.this, LoginActivity.class));
                            } else {
                                Toast toast = Toast.makeText(NewGrievanceActivity.this, error.getLocalizedMessage() + "Could not retrieve grievance types.", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        else {
                            Toast toast = Toast.makeText(NewGrievanceActivity.this, "An unexpected error occurred while retrieving complaint types", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                        autocompleteComplaintType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_refresh_black_24dp, 0);
                        autocompleteComplaintType.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                            @Override
                            public void onClick(DrawablePosition target) {
                                if (target == DrawablePosition.RIGHT) {
                                    autocompleteComplaintType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    autocompleteComplaintType.setDrawableClickListener(null);
                                    autocompleteComplaintType.setHint(getString(R.string.loading_label));

                                    //Same code as above because I'm stupid
                                    ApiController.getAPI(NewGrievanceActivity.this).getComplaintTypes(sessionManager.getAccessToken(), new Callback<GrievanceTypeAPIResponse>() {
                                                @Override
                                                public void success(GrievanceTypeAPIResponse grievanceTypeAPIResponse, Response response) {
                                                    grievanceTypes = grievanceTypeAPIResponse.getGrievanceType();
                                                    List<String> strings = new ArrayList<>();
                                                    for (int i = 0; i < grievanceTypes.size(); i++) {
                                                        strings.add(grievanceTypes.get(i).getName());
                                                    }

                                                    final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
                                                    autocompleteComplaintType.setHint("Complaint Type*");
                                                    autocompleteComplaintType.setOnClickListener(null);
                                                    autocompleteComplaintType.setAdapter(autoCompleteAdapter);
                                                    autocompleteComplaintType.setThreshold(1);
                                                    autocompleteComplaintType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                                                    autocompleteComplaintType.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                                                        @Override
                                                        public void onClick(DrawablePosition target) {
                                                            if (target == DrawablePosition.RIGHT) {
                                                                autocompleteComplaintType.showDropDown();
                                                            }
                                                        }
                                                    });
                                                    autocompleteComplaintType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                                                         @Override
                                                                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                                                                             String s = autocompleteComplaintType.getText().toString();
                                                                                                             for (GrievanceType grievanceType : grievanceTypes) {
                                                                                                                 if (s.equals(grievanceType.getName()))
                                                                                                                     typeID = grievanceType.getId();
                                                                                                             }
                                                                                                         }
                                                                                                     }

                                                    );

                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    autocompleteComplaintType.setOnClickListener(null);
                                                    autocompleteComplaintType.setCompoundDrawables(null, null, null, null);
                                                    if (error.getLocalizedMessage() != null)
                                                        if (error.getLocalizedMessage().equals("Invalid access token")) {
                                                            Toast toast = Toast.makeText(NewGrievanceActivity.this, "Session expired", Toast.LENGTH_SHORT);
                                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();
                                                            sessionManager.logoutUser();
                                                            startActivity(new Intent(NewGrievanceActivity.this, LoginActivity.class));
                                                        } else {
                                                            Toast toast = Toast.makeText(NewGrievanceActivity.this, error.getLocalizedMessage() + "Could not retrieve grievance types.", Toast.LENGTH_SHORT);
                                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();
                                                        }
                                                    else {
                                                        Toast toast = Toast.makeText(NewGrievanceActivity.this, "An unexpected error occurred while retrieving complaint types", Toast.LENGTH_SHORT);
                                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                        toast.show();
                                                    }
                                                    autocompleteComplaintType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_refresh_black_24dp, 0);
                                                    autocompleteComplaintType.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                                                        @Override
                                                        public void onClick(DrawablePosition target) {

                                                        }
                                                    });
                                                }
                                            }

                                    );
                                }
                            }
                        });
                    }
                }

        );


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
                    Toast toast = Toast.makeText(NewGrievanceActivity.this, "Limited to 3 image", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }

            }
        };

        if (Build.VERSION.SDK_INT >= 21) {
            pictureAddButton.setOnClickListener(onClickListener);

        } else {
            pictureAddButton.setVisibility(View.GONE);
            pictureAddButtonCompat.setVisibility(View.VISIBLE);
            pictureAddButtonCompat.setOnClickListener(onClickListener);
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


    //validate and submit grievance
    public void validateAndSubmitGrievance()
    {
        String complaintDetails = details.getText().toString().trim();
        double lat;
        double lng;
        String landmarkDetails = landmark.getText().toString().trim();

        if (typeID == 0) {
            Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please select complaint type", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        else if (locationID == 0 && (marker == null)) {
            Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please select location on map or select a location from dropdown", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }else if (TextUtils.isEmpty(complaintDetails) || complaintDetails.length() < 10) {
            Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please enter complaint details (at least 10 characters", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } else {
            if (marker != null) {
                lat = marker.getPosition().latitude;
                lng = marker.getPosition().longitude;
                progressDialog.show();
                submit(new GrievanceRequest(lat, lng, complaintDetails, typeID, landmarkDetails));
            } else {
                progressDialog.show();
                submit(new GrievanceRequest(locationID, complaintDetails, typeID, landmarkDetails));
            }
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
                addMarkerFromImage(uri);
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
                addMarkerFromImage(data.getData());
            }

        } else if (requestCode == LOCATION_REQUEST_CODE && resultCode == 0) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    //Invokes call to API
    private void submit(GrievanceRequest grievanceRequest) {

        //Used to upload multiple multipart parts with the same field name
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

        multipartTypedOutput.addPart("json_complaint", new TypedString(new Gson().toJson(grievanceRequest)));

        if (uploadCount != 0) {
            for (Uri uri : uriArrayList) {

                String mimeType = getMimeType(uri);

                String path;

                File imgFile = new File(uri.getPath());
                path = uri.getPath();

                if (!imgFile.exists()) {
                    try {
                        new File(UriPathHelper.getRealPathFromURI(uri, NewGrievanceActivity.this));
                        path = UriPathHelper.getRealPathFromURI(uri, NewGrievanceActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                path = ImageCompressionHelper.compressImage(path, path);

                multipartTypedOutput.addPart("files", new TypedFile(mimeType, new File(path)));

            }

        }


        ApiController.getAPI(NewGrievanceActivity.this).createComplaint(multipartTypedOutput, sessionManager.getAccessToken(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                Toast toast = Toast.makeText(NewGrievanceActivity.this, "Grievance successfully registered", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                progressDialog.dismiss();

                setResult(RESULT_OK, new Intent());
                finish();


            }

            @Override
            public void failure(RetrofitError error) {

                progressDialog.dismiss();
                if (error.getLocalizedMessage() != null)
                    if (error.getLocalizedMessage().equals("Invalid access token")) {
                        Toast toast = Toast.makeText(NewGrievanceActivity.this, "Session expired", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        sessionManager.logoutUser();
                        startActivity(new Intent(NewGrievanceActivity.this, LoginActivity.class));
                    } else if (error.getLocalizedMessage().contains("400")) {
                        try {
                            ErrorResponse errorResponse = (ErrorResponse) error.getBodyAs(ErrorResponse.class);
                            Toast toast = Toast.makeText(NewGrievanceActivity.this, errorResponse.getErrorStatus().getMessage(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(NewGrievanceActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(NewGrievanceActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                else {
                    Toast toast = Toast.makeText(NewGrievanceActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        if (googleMap != null)
            googleMap.animateCamera(cameraUpdate);
        removeLocationListener();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Interface defined to be able to invoke function in fragment class. May be unnecessary
    public interface RemoveImageInterface {
        void removeFragmentImage(int position, UploadImageFragment fragment);
    }

    //Custom adapter for viewpager
    private class GrievanceImagePagerAdapter extends FragmentStatePagerAdapter implements RemoveImageInterface {

        public                      GrievanceImagePagerAdapter(FragmentManager fm) {
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
                ThumbImage=MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(arg.getString("uri")));
            } catch (IOException e) {

                e.printStackTrace();
            }


            if (ThumbImage != null) {
                ThumbImage = Bitmap.createScaledBitmap(ThumbImage, ThumbImage.getWidth(), ThumbImage.getHeight(), true);
            } else {
                Toast toast = Toast.makeText(getActivity(), "An error was encountered retrieving this image", Toast.LENGTH_SHORT);
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
        removeLocationListener();
    }

    //removing location listener
    public void removeLocationListener() {
        try {
            if (locationManager != null)
                locationManager.removeUpdates(this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
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

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(settingsIntent, LOCATION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void startLocationListener() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startLocationListener();
                } else {
                    Toast.makeText(NewGrievanceActivity.this, "You're disabled location access!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    fromCamera();
                } else {
                    Toast.makeText(NewGrievanceActivity.this, "You're disabled camera access!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_READ_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_PHOTO);
                } else {
                    Toast.makeText(NewGrievanceActivity.this, "You're disabled read access!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void addMarkerFromImage(Uri imageUri) {
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
                addMarkerToMap(new LatLng(lat, lng));
                locationID = 0;
                getAddress(lat,lng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addMarkerToMap(LatLng latLng) {
        if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions().position(latLng));
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        googleMap.animateCamera(location);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkLocationPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
            return false;
        }
        return true;
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

    public class MapDialog extends Dialog implements OnMapReadyCallback, DialogInterface.OnShowListener, GoogleMap.OnMapClickListener {

        Marker mapLocationMarker;

        public Marker getMapLocationMarker() {
            return mapLocationMarker;
        }

        public void setMapLocationMarker(Marker mapLocationMarker) {
            this.mapLocationMarker = mapLocationMarker;
        }

        public MapDialog(Context context) {
            super(context, R.style.AppTheme);
            setContentView(R.layout.dialog_picklocation);
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        //Performs initial setup of the map
        @Override
        public void onMapReady(final GoogleMap googleMap) {
            //Disable map toolbar
            UiSettings uiSettings = googleMap.getUiSettings();
            uiSettings.setMapToolbarEnabled(false);
            // Set map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LatLng latLng = new LatLng(sessionManager.getCityLatitude(), sessionManager.getCityLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
            googleMap.animateCamera(cameraUpdate);
            googleMap.setOnMapClickListener(this);

        }

        @Override
        public void onShow(DialogInterface dialog) {
            googleMap.clear();
            if(mapLocationMarker!=null)
            {
                LatLng latLng=new LatLng(mapLocationMarker.getPosition().latitude, mapLocationMarker.getPosition().longitude);
                mapLocationMarker=googleMap.addMarker(new MarkerOptions().position(latLng));
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                googleMap.animateCamera(location);
            }
        }

        @Override
        public void onMapClick(LatLng latLng) {
            if (mapLocationMarker != null) {
                mapLocationMarker.remove();
            }
            mapLocationMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 18);
            googleMap.animateCamera(location);
        }

    }

    public void intializeDialogComponents(final MapDialog mapDialog)
    {
        Toolbar toolbar = (Toolbar) mapDialog.findViewById(R.id.toolbar);
        toolbar.setTitle("Pick Complaint Location");
        toolbar.inflateMenu(R.menu.map_dialog_actions);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDialog.dismiss();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_pickloc:
                        mapDialog.dismiss();
                        //check for location is picked or not
                        if(mapDialog.getMapLocationMarker()!=null)
                        {
                            if(marker!=null)
                            {
                                //check whether location is changed or not
                                if(marker.getPosition().latitude==mapDialog.getMapLocationMarker().getPosition().latitude && marker.getPosition().longitude==mapDialog.getMapLocationMarker().getPosition().longitude)
                                {
                                    return true;
                                }
                            }
                            marker=mapDialog.getMapLocationMarker();
                            getAddress(marker.getPosition().latitude, marker.getPosition().longitude);
                            locationID = 0;
                        }

                        return true;
                }
                return false;
            }
        });

        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        googleMap=map.getMap();
        map.getMapAsync(mapDialog);
        mapDialog.setOnShowListener(mapDialog);
    }

    /*/If lat/lng is available attempt to resolve it to an address*/
    private void getAddress(Double lat, Double lng) {
        progressDialog.show();
        Intent intent = new Intent(this, AddressService.class);
        intent.putExtra(AddressService.LAT, lat);
        intent.putExtra(AddressService.LNG, lng);
        startService(intent);
    }

    //Handles AddressReadyEvent posted by AddressService on success
    @SuppressWarnings("unused")
    public void onEvent(AddressReadyEvent addressReadyEvent) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                isPickedLocationFromMap=true;
                autoCompleteComplaintLoc.setText(AddressService.addressResult);
            }
        });

    }

    //Subscribes the activity to events
    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    //Unsubscribes the activity to events
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
