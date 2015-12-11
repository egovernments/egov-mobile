package com.egovernments.egov.activities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.NYXDigital.NiceSupportMapFragment;
import com.egovernments.egov.R;
import com.egovernments.egov.helper.CustomAutoCompleteTextView;
import com.egovernments.egov.helper.ImageCompressionHelper;
import com.egovernments.egov.helper.NoFilterAdapter;
import com.egovernments.egov.helper.NothingSelectedSpinnerAdapter;
import com.egovernments.egov.helper.UriPathHelper;
import com.egovernments.egov.models.GrievanceLocation;
import com.egovernments.egov.models.GrievanceLocationAPIResponse;
import com.egovernments.egov.models.GrievanceRequest;
import com.egovernments.egov.models.GrievanceType;
import com.egovernments.egov.models.GrievanceTypeAPIResponse;
import com.egovernments.egov.models.errors.ErrorResponse;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.SessionManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.viewpagerindicator.LinePageIndicator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class NewGrievanceActivity extends BaseActivity implements OnMapReadyCallback {

    private List<GrievanceType> grievanceTypes = new ArrayList<>();

    private Spinner dropdown;

    private Button button;

    private ProgressDialog progressDialog;

    private Dialog dialog;

    private Location myLocation;

    private AutoCompleteTextView autoCompleteTextView;

    private CustomAutoCompleteTextView listTextView;

    private SessionManager sessionManager;

    private List<GrievanceLocation> grievanceLocations;

    private int locationID = 0;
    private int check = 0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grievance);


        sessionManager = new SessionManager(getApplicationContext());

        cacheDir = this.getExternalCacheDir() == null ? this.getCacheDir() : this.getExternalCacheDir();

        final FloatingActionButton pictureAddButton = (FloatingActionButton) findViewById(R.id.picture_add);
        final com.melnykov.fab.FloatingActionButton pictureAddButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.picture_addcompat);

        NiceSupportMapFragment niceSupportMapFragment = (NiceSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.complaint_map);
        googleMap = niceSupportMapFragment.getMap();
        niceSupportMapFragment.getMapAsync(this);

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCanceledOnTouchOutside(true);

        listTextView = (CustomAutoCompleteTextView) findViewById(R.id.grievancetype_spinner_placeholder);

        listTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(NewGrievanceActivity.this, "Fetching type list, please wait", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.upload_complaint_image);
        grievanceImagePagerAdapter = new GrievanceImagePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(grievanceImagePagerAdapter);
        LinePageIndicator linePageIndicator = (LinePageIndicator) findViewById(R.id.new_indicator);
        linePageIndicator.setViewPager(viewPager);

        landmark = (EditText) findViewById(R.id.complaint_landmark);
        details = (EditText) findViewById(R.id.complaint_details);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.complaint_locationname);
        autoCompleteTextView.setThreshold(3);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                                                        @Override
                                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                        }

                                                        //When 2 or more characters are entered, API is called to provide a matching location name
                                                        @Override
                                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                            if (s.length() >= 3) {
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
                                                                                    autoCompleteTextView.setAdapter(adapter);

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

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                locationID = grievanceLocations.get(position).getId();
                //Clears marker when a location is selected
                if (marker != null)
                    marker.remove();
                marker = null;
            }
        });


        button = (Button) findViewById(R.id.button_submit);


        dropdown = (Spinner) findViewById(R.id.complaint_type);

        //Retrieves the list of complaint to populate dropdown. Dropdown is empty until it succeeds
        ApiController.getAPI(NewGrievanceActivity.this).getComplaintTypes(sessionManager.getAccessToken(), new Callback<GrievanceTypeAPIResponse>() {
                    @Override
                    public void success(GrievanceTypeAPIResponse grievanceTypeAPIResponse, Response response) {
                        grievanceTypes = grievanceTypeAPIResponse.getGrievanceType();
                        List<String> strings = new ArrayList<>();
                        for (int i = 0; i < grievanceTypes.size(); i++) {
                            strings.add(grievanceTypes.get(i).getName());
                        }
                        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
                        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dropdown.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, NewGrievanceActivity.this));
                        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                check = check + 1;
                                if (check > 1) {
                                    typeID = grievanceTypes.get(position - 1).getId();
                                    listTextView.setText(grievanceTypes.get(position - 1).getName());
                                    listTextView.dismissDropDown();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
                        listTextView.setHint(R.string.complaint_type_label);
                        listTextView.setOnClickListener(null);
                        listTextView.setAdapter(autoCompleteAdapter);
                        listTextView.setThreshold(1);
                        listTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                        listTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                            @Override
                            public void onClick(DrawablePosition target) {
                                if (target == DrawablePosition.RIGHT) {
                                    dropdown.performClick();
                                }
                            }
                        });
                        listTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                                    String s = listTextView.getText().toString();
                                                                    for (GrievanceType grievanceType : grievanceTypes) {
                                                                        if (s.equals(grievanceType.getName()))
                                                                            typeID = grievanceType.getId();
                                                                    }
                                                                }
                                                            }

                        );
                        button.setOnClickListener(new View.OnClickListener()

                                                  {
                                                      @Override
                                                      public void onClick(View v) {

                                                          String complaintDetails = details.getText().toString().trim();
                                                          double lat;
                                                          double lng;
                                                          String landmarkDetails = landmark.getText().toString().trim();

                                                          if (locationID == 0 && (marker == null)) {
                                                              Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please select location on map or select a location from dropdown", Toast.LENGTH_SHORT);
                                                              toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                              toast.show();
                                                          } else if (typeID == 0) {
                                                              Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please select complaint type", Toast.LENGTH_SHORT);
                                                              toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                              toast.show();
                                                          } else if (complaintDetails.isEmpty() || complaintDetails.length() < 10) {
                                                              Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please enter additional details (at least 10 characters", Toast.LENGTH_SHORT);
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
                                                  }

                        );


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        listTextView.setOnClickListener(null);
                        listTextView.setHint("Loading Failed");
                        listTextView.setCompoundDrawables(null, null, null, null);
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
                        listTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                        listTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                            @Override
                            public void onClick(DrawablePosition target) {
                                if (target == DrawablePosition.RIGHT) {
                                    listTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    listTextView.setDrawableClickListener(null);
                                    listTextView.setHint(getString(R.string.loading_label));

                                    //Same code as above because I'm stupid
                                    ApiController.getAPI(NewGrievanceActivity.this).getComplaintTypes(sessionManager.getAccessToken(), new Callback<GrievanceTypeAPIResponse>() {
                                                @Override
                                                public void success(GrievanceTypeAPIResponse grievanceTypeAPIResponse, Response response) {
                                                    grievanceTypes = grievanceTypeAPIResponse.getGrievanceType();
                                                    List<String> strings = new ArrayList<>();
                                                    for (int i = 0; i < grievanceTypes.size(); i++) {
                                                        strings.add(grievanceTypes.get(i).getName());
                                                    }
                                                    ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
                                                    dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    dropdown.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, NewGrievanceActivity.this));
                                                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            check = check + 1;
                                                            if (check > 1) {
                                                                typeID = grievanceTypes.get(position - 1).getId();
                                                                listTextView.setText(grievanceTypes.get(position - 1).getName());
                                                                listTextView.dismissDropDown();
                                                            }
                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });

                                                    final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(NewGrievanceActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
                                                    listTextView.setHint("Complaint Type*");
                                                    listTextView.setOnClickListener(null);
                                                    listTextView.setAdapter(autoCompleteAdapter);
                                                    listTextView.setThreshold(1);
                                                    listTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                                                    listTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                                                        @Override
                                                        public void onClick(DrawablePosition target) {
                                                            if (target == DrawablePosition.RIGHT) {
                                                                dropdown.performClick();
                                                            }
                                                        }
                                                    });
                                                    listTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                                            @Override
                                                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                                                                String s = listTextView.getText().toString();
                                                                                                for (GrievanceType grievanceType : grievanceTypes) {
                                                                                                    if (s.equals(grievanceType.getName()))
                                                                                                        typeID = grievanceType.getId();
                                                                                                }
                                                                                            }
                                                                                        }

                                                    );
                                                    button.setOnClickListener(new View.OnClickListener()

                                                                              {
                                                                                  @Override
                                                                                  public void onClick(View v) {

                                                                                      String complaintDetails = details.getText().toString().trim();
                                                                                      double lat;
                                                                                      double lng;
                                                                                      String landmarkDetails = landmark.getText().toString().trim();

                                                                                      if (locationID == 0 && (marker == null)) {
                                                                                          Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please select location on map or select a location from dropdown", Toast.LENGTH_SHORT);
                                                                                          toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                                          toast.show();
                                                                                      } else if (typeID == 0) {
                                                                                          Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please select complaint type", Toast.LENGTH_SHORT);
                                                                                          toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                                          toast.show();
                                                                                      } else if (complaintDetails.isEmpty() || complaintDetails.length() < 10) {
                                                                                          Toast toast = Toast.makeText(NewGrievanceActivity.this, "Please enter additional details (at least 10 characters", Toast.LENGTH_SHORT);
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
                                                                              }

                                                    );


                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    listTextView.setOnClickListener(null);
                                                    listTextView.setHint("Loading Failed");
                                                    listTextView.setCompoundDrawables(null, null, null, null);
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
                                                    listTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                                                    listTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
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
                    dialog = new Dialog(NewGrievanceActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_upload);
                    dialog.setCanceledOnTouchOutside(true);


                    dialog.findViewById(R.id.from_gallery).setOnClickListener(new View.OnClickListener() {
                        //Opens default gallery app
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, GALLERY_PHOTO);
                            dialog.dismiss();

                        }
                    });

                    dialog.findViewById(R.id.from_camera).setOnClickListener(new View.OnClickListener() {
                        //Opens default camera app
                        @Override
                        public void onClick(View v) {

                            fromCamera();
                            dialog.dismiss();

                        }
                    });

                    dialog.show();
                } else {
                    Toast toast = Toast.makeText(NewGrievanceActivity.this, "Limited to 3 image", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }

            }
        };

        if (Build.VERSION.SDK_INT >= 21)

        {
            pictureAddButton.setOnClickListener(onClickListener);

        } else

        {
            pictureAddButton.setVisibility(View.GONE);
            pictureAddButtonCompat.setVisibility(View.VISIBLE);
            pictureAddButtonCompat.setOnClickListener(onClickListener);
        }

        ImageButton imageButton = (ImageButton) findViewById(R.id.map_marker_clear_button);
        Drawable drawable = ContextCompat.getDrawable(NewGrievanceActivity.this, R.drawable.ic_cancel_white_24dp);
        drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        imageButton.setImageDrawable(drawable);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker != null)
                    marker.remove();
                marker = null;
            }
        });

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

            //Stores image in app cache
            Uri uri = Uri.fromFile(new File(cacheDir, "POST_IMAGE_" + imageID.get(0) + ".jpg"));
            uriArrayList.add(uri);

            getContentResolver().notifyChange(uriArrayList.get(uriArrayList.size() - 1), null);

            grievanceImagePagerAdapter.notifyDataSetChanged();

            uploadCount++;

            //Attempts to extract GPS data from image and place marker on map
            try {
                String s = UriPathHelper.getRealPathFromURI(uri, this);
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
                    LatLng latLng = new LatLng(lat, lng);
                    if (marker != null) {
                        marker.remove();
                    }
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng));

                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                    googleMap.animateCamera(location);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageID.remove(0);

            viewPager.setCurrentItem(uriArrayList.size());


        }


        //If result is from gallery
        if (requestCode == GALLERY_PHOTO && resultCode == Activity.RESULT_OK) {

            uriArrayList.add(data.getData());

            grievanceImagePagerAdapter.notifyDataSetChanged();

            uploadCount++;

            imageID.remove(0);

            viewPager.setCurrentItem(uriArrayList.size());

        }

    }

    //Performs initial setup of the map
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        //Disable map toolbar
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        //Attempt to get user location
        try {
            myLocation = locationManager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setMyLocationEnabled(true);


        if (myLocation != null) {

            // Get latitude of the current location
            double latitude = myLocation.getLatitude();

            // Get longitude of the current location
            double longitude = myLocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            LatLng myCoordinates = new LatLng(latitude, longitude);

            marker = null;

            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 16);
            googleMap.animateCamera(yourLocation);
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (marker != null)
                    marker.remove();
                marker = googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        });

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

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
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

    //Interface defined to be able to invoke function in fragment class. May be unnecessary
    public interface RemoveImageInterface {
        void removeFragmentImage(int position, UploadImageFragment fragment);
    }

    //Custom adapter for viewpager
    private class GrievanceImagePagerAdapter extends FragmentStatePagerAdapter implements RemoveImageInterface {

        public GrievanceImagePagerAdapter(FragmentManager fm) {
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

        @Nullable
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
                ThumbImage = ThumbnailUtils
                        .extractThumbnail(MediaStore.Images.Media.getBitmap
                                (getActivity().getContentResolver(),
                                        Uri.parse(arg.getString("uri"))), 1280, 720);
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
}
