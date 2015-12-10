package com.egovernments.egov.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.adapters.GrievanceCommentAdapter;
import com.egovernments.egov.events.AddressReadyEvent;
import com.egovernments.egov.fragments.GrievanceImageFragment;
import com.egovernments.egov.helper.NothingSelectedSpinnerAdapter;
import com.egovernments.egov.models.Grievance;
import com.egovernments.egov.models.GrievanceCommentAPIResponse;
import com.egovernments.egov.models.GrievanceCommentAPIResult;
import com.egovernments.egov.models.GrievanceUpdate;
import com.egovernments.egov.network.AddressService;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.UpdateService;
import com.google.gson.JsonObject;
import com.viewpagerindicator.LinePageIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Displays the details of a complaint when clicked in GrievanceActivity recycler view
 **/

public class GrievanceDetailsActivity extends BaseActivity {

    public static final String GRIEVANCE_ITEM = "GrievanceItem";

    private static Grievance grievance;

    private ListView listView;

    private EditText updateComment;

    private ProgressDialog progressDialog;

    private TextView complaintLocation;

    private String action;

    private boolean isComment = false;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_details);
        grievance = (Grievance) getIntent().getSerializableExtra(GRIEVANCE_ITEM);

        TextView complaintDate = (TextView) findViewById(R.id.details_complaint_date);
        TextView complaintType = (TextView) findViewById(R.id.details_complaint_type);
        TextView complaintDetails = (TextView) findViewById(R.id.details_complaint_details);
        TextView complaintStatus = (TextView) findViewById(R.id.details_complaint_status);
        TextView complaintLandmark = (TextView) findViewById(R.id.details_complaint_landmark);
        TextView complaintNo = (TextView) findViewById(R.id.details_complaintNo);
        TextView commentBoxLabel = (TextView) findViewById(R.id.commentbox_label);
        complaintLocation = (TextView) findViewById(R.id.details_complaint_location);

        final LinearLayout feedbackLayout = (LinearLayout) findViewById(R.id.feedback_layout);

        Button updateButton = (Button) findViewById(R.id.grievance_update_button);

        updateComment = (EditText) findViewById(R.id.update_comment);

        progressBar = (ProgressBar) findViewById(R.id.grievance_history_placeholder);

        listView = (ListView) findViewById(R.id.grievance_comments);

        final Spinner actionsSpinner = (Spinner) findViewById(R.id.update_action);
        final Spinner feedbackSpinner = (Spinner) findViewById(R.id.update_feedback);
        ArrayList<String> actions_open = new ArrayList<>(Arrays.asList("Comment", "Withdraw"));
        ArrayList<String> actions_closed = new ArrayList<>(Arrays.asList("Comment", "Re-open"));
        ArrayList<String> feedbackOptions = new ArrayList<>(Arrays.asList("Unspecified", "Satisfactory", "Unsatisfactory"));

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCancelable(false);

        //The default image when complaint has no uploaded images
        ImageView default_image = (ImageView) findViewById(R.id.details_defaultimage);
        //The layout for uploaded images
        RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.details_imageslayout);

        //If no uploaded images
        if (grievance.getSupportDocsSize() == 0) {
            default_image.setVisibility(View.VISIBLE);
            imageLayout.setVisibility(View.GONE);
        } else {
            ViewPager viewPager = (ViewPager) findViewById(R.id.details_complaint_image);
            viewPager.setAdapter(new GrievanceImagePagerAdapter(getSupportFragmentManager()));

            LinePageIndicator linePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
            linePageIndicator.setViewPager(viewPager);
        }

        //Parses complaint date into a more readable format
        try {
            //noinspection SpellCheckingInspection
            complaintDate.setText(new SimpleDateFormat("EEEE, d MMMM, yyyy", Locale.ENGLISH)
                    .format(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.ENGLISH)
                            .parse(grievance.getCreatedDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        complaintType.setText(grievance.getComplaintTypeName());
        complaintDetails.setText(grievance.getDetail());
        if (grievance.getLandmarkDetails().isEmpty()) {
            findViewById(R.id.details_complaint_landmark_label).setVisibility(View.GONE);
            complaintLandmark.setVisibility(View.GONE);

        } else
            complaintLandmark.setText(grievance.getLandmarkDetails());

        //If grievance has lat/lng values, location name is null
        if (grievance.getLat() == null)
            complaintLocation.setText(grievance.getChildLocationName() + " - " + grievance.getLocationName());
        else {
            getAddress(grievance.getLat(), grievance.getLng());
        }

        complaintNo.setText(grievance.getCrn());
        complaintStatus.setText(resolveStatus(grievance.getStatus()));

        //Display feedback spinner
        if (grievance.getStatus().equals("COMPLETED") || grievance.getStatus().equals("REJECTED") || grievance.getStatus().equals("WITHDRAWN")) {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(GrievanceDetailsActivity.this, R.layout.view_grievanceupdate_spinner, actions_closed);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            actionsSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.view_grievanceupdate_spinner, GrievanceDetailsActivity.this));

            commentBoxLabel.setText("Feedback");

            feedbackLayout.setVisibility(View.VISIBLE);
            ArrayAdapter<String> feedbackAdapter = new ArrayAdapter<>(GrievanceDetailsActivity.this, R.layout.view_grievancefeedback_spinner, feedbackOptions);
            feedbackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            feedbackSpinner.setAdapter(new NothingSelectedSpinnerAdapter(feedbackAdapter, R.layout.view_grievancefeedback_spinner, GrievanceDetailsActivity.this));


        }
        //Display default spinners
        else {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(GrievanceDetailsActivity.this, R.layout.view_grievanceupdate_spinner, actions_open);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            actionsSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.view_grievanceupdate_spinner, GrievanceDetailsActivity.this));

            commentBoxLabel.setText("Update grievance");
        }

        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ApiController.getAPI(GrievanceDetailsActivity.this).getComplaintHistory(grievance.getCrn(), sessionManager.getAccessToken(), new Callback<GrievanceCommentAPIResponse>() {
            @Override
            public void success(GrievanceCommentAPIResponse grievanceCommentAPIResponse, Response response) {

                GrievanceCommentAPIResult grievanceCommentAPIResult = grievanceCommentAPIResponse.getGrievanceCommentAPIResult();

                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(new GrievanceCommentAdapter(grievanceCommentAPIResult.getGrievanceComments(), GrievanceDetailsActivity.this));


            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getLocalizedMessage() != null)
                    if (error.getLocalizedMessage().equals("Invalid access token")) {
                        Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "Session expired", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        sessionManager.logoutUser();
                        startActivity(new Intent(GrievanceDetailsActivity.this, LoginActivity.class));
                    } else {
                        Toast toast = Toast.makeText(GrievanceDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                else {
                    Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "An unexpected error occurred while retrieving comments", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
                progressBar.setVisibility(View.GONE);
            }

        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                action = (String) actionsSpinner.getSelectedItem();
                String comment = updateComment.getText().toString().trim();
                String feedback = "";
                if (feedbackLayout.getVisibility() == View.VISIBLE) {
                    feedback = (String) feedbackSpinner.getSelectedItem();
                }

                if (action == null) {
                    Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "Please select an action", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    if ((action.equals("Comment") || action.equals("Re-open")) && comment.isEmpty()) {
                        Toast.makeText(GrievanceDetailsActivity.this, "Comment is necessary for this action", Toast.LENGTH_SHORT).show();
                    } else if (feedback == null) {
                        {
                            Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "Please select a feedback option", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    } else {
                        switch (action) {
                            case "Comment":
                                isComment = true;
                                action = grievance.getStatus();
                                break;
                            case "Withdraw":
                                isComment = false;
                                action = "WITHDRAWN";
                                break;
                            case "Re-open":
                                isComment = false;
                                action = "REOPENED";
                                break;
                        }

                        progressDialog.show();

                        ApiController.getAPI(GrievanceDetailsActivity.this).updateGrievance(grievance.getCrn(), new GrievanceUpdate(action, feedback.toUpperCase(), comment), sessionManager.getAccessToken(), new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {

                                Toast toast = Toast.makeText(GrievanceDetailsActivity.this, R.string.grievanceupdated_msg, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                                progressDialog.dismiss();

                                ApiController.getAPI(GrievanceDetailsActivity.this).getComplaintHistory(grievance.getCrn(), sessionManager.getAccessToken(), new Callback<GrievanceCommentAPIResponse>() {
                                    @Override
                                    public void success(GrievanceCommentAPIResponse grievanceCommentAPIResponse, Response response) {

                                        GrievanceCommentAPIResult grievanceCommentAPIResult = grievanceCommentAPIResponse.getGrievanceCommentAPIResult();

                                        listView.setAdapter(new GrievanceCommentAdapter(grievanceCommentAPIResult.getGrievanceComments(), GrievanceDetailsActivity.this));
                                        actionsSpinner.setSelection(0);
                                        feedbackSpinner.setSelection(0);
                                        updateComment.getText().clear();
                                        if (!isComment) {
                                            Intent intent = new Intent(GrievanceDetailsActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_COMPLAINTS);
                                            intent.putExtra(UpdateService.COMPLAINTS_PAGE, "1");
                                            startService(intent);
                                            finish();
                                        }

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        if (error.getLocalizedMessage() != null)
                                            if (error.getLocalizedMessage().equals("Invalid access token")) {
                                                Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "Session expired", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                toast.show();
                                                sessionManager.logoutUser();
                                                startActivity(new Intent(GrievanceDetailsActivity.this, LoginActivity.class));
                                            } else {
                                                Toast toast = Toast.makeText(GrievanceDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                toast.show();
                                            }
                                        else {
                                            Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "An unexpected error occurred while retrieving comments", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                            toast.show();
                                        }
                                    }
                                });


                            }

                            @Override
                            public void failure(RetrofitError error) {

                                if (error.getLocalizedMessage() != null)
                                    if (error.getLocalizedMessage().equals("Invalid access token")) {
                                        Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "Session expired", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                        sessionManager.logoutUser();
                                        startActivity(new Intent(GrievanceDetailsActivity.this, LoginActivity.class));
                                    } else {
                                        Toast toast = Toast.makeText(GrievanceDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }
                                else {
                                    Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                }
                                progressDialog.dismiss();

                            }
                        });

                    }
                }
            }
        });

    }

    //Converts status parameters from server to a more readable form. Probably unnecessary but whatevs
    private int resolveStatus(String s) {
        if (s.equals("REGISTERED"))
            return R.string.registered_info;
        if (s.equals("PROCESSING"))
            return R.string.processing_label;
        if (s.equals("COMPLETED"))
            return R.string.completed_label;
        if (s.equals("FORWARDED"))
            return R.string.forwarded_label;
        if (s.equals("WITHDRAWN"))
            return R.string.withdrawn_label;
        if (s.equals("REJECTED"))
            return R.string.rejected_label;
        if (s.equals("REOPENED"))
            return R.string.reopend_label;

        return 0;
    }

    public static Grievance getGrievance() {
        return grievance;
    }

    //The viewpager custom adapter
    private class GrievanceImagePagerAdapter extends FragmentPagerAdapter {

        public GrievanceImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (grievance.getSupportDocsSize() != 0)
                return GrievanceImageFragment.instantiateItem(position, getSessionManager().getAccessToken(), grievance.getCrn(), String.valueOf(grievance.getSupportDocsSize() - position));

            return null;
        }

        @Override
        public int getCount() {
            return grievance.getSupportDocsSize();
        }
    }

    //If lat/lng is available attempt to resolve it to an address
    private void getAddress(Double lat, Double lng) {

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
                complaintLocation.setText(AddressService.addressResult);
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
