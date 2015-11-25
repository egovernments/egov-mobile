package com.egovernments.egov.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

public class GrievanceDetailsActivity extends BaseActivity {

    public static final String GRIEVANCE_ITEM = "GrievanceItem";

    private Grievance grievance;

    private ListView listView;

    private EditText updateComment;

    private ProgressDialog progressDialog;

    private TextView complaintLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_details);
        grievance = (Grievance) getIntent().getSerializableExtra(GRIEVANCE_ITEM);

        TextView complaintDate = (TextView) findViewById(R.id.details_complaint_date);
        TextView complaintType = (TextView) findViewById(R.id.details_complaint_type);
        TextView complaintDetails = (TextView) findViewById(R.id.details_complaint_details);
        TextView complaintStatus = (TextView) findViewById(R.id.details_complaint_status);
        complaintLocation = (TextView) findViewById(R.id.details_complaint_location);
        TextView complaintNo = (TextView) findViewById(R.id.details_complaintNo);
        TextView commentBoxLabel = (TextView) findViewById(R.id.commentbox_label);

        Button updateButton = (Button) findViewById(R.id.grievance_update_button);

        updateComment = (EditText) findViewById(R.id.update_comment);

        listView = (ListView) findViewById(R.id.grievance_comments);

        final Spinner actionsSpinner = (Spinner) findViewById(R.id.update_action);
        final Spinner feedbackSpinner = (Spinner) findViewById(R.id.update_feedback);
        ArrayList<String> actions_open = new ArrayList<>(Arrays.asList("Update", "Withdraw"));
        ArrayList<String> actions_closed = new ArrayList<>(Arrays.asList("Update", "Re-open"));
        ArrayList<String> feedbackoptions = new ArrayList<>(Arrays.asList("Unspecified", "Satisfactory", "Unsatisfactory"));

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCancelable(false);

        ImageView default_image = (ImageView) findViewById(R.id.details_defaultimage);
        RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.details_imageslayout);

        if (grievance.getSupportDocsSize() == 0) {
            default_image.setVisibility(View.VISIBLE);
            imageLayout.setVisibility(View.GONE);
        } else {
            ViewPager viewPager = (ViewPager) findViewById(R.id.details_complaint_image);
            viewPager.setAdapter(new GrievanceImagePagerAdapter(getSupportFragmentManager()));

            LinePageIndicator linePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
            linePageIndicator.setViewPager(viewPager);
        }


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

        if (grievance.getLat() == null)
            complaintLocation.setText(grievance.getChildLocationName() + " " + grievance.getLocationName());
        else {
            getAddress(grievance.getLat(), grievance.getLng());
        }

        complaintNo.setText(grievance.getCrn());
        complaintStatus.setText(resolveStatus(grievance.getStatus()));

        if (grievance.getStatus().equals("COMPLETED") || grievance.getStatus().equals("REJECTED")) {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(GrievanceDetailsActivity.this, R.layout.view_grievanceupdate_spinner, actions_closed);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            actionsSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.view_grievanceupdate_spinner, GrievanceDetailsActivity.this));

            commentBoxLabel.setText("Feedback");

            feedbackSpinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> feedbackAdapter = new ArrayAdapter<>(GrievanceDetailsActivity.this, R.layout.view_grievanceupdate_spinner, feedbackoptions);
            feedbackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            feedbackSpinner.setAdapter(new NothingSelectedSpinnerAdapter(feedbackAdapter, R.layout.view_grievanceupdate_spinner, GrievanceDetailsActivity.this));


        } else {

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

        ApiController.getAPI().getComplaintHistory(grievance.getCrn(), sessionManager.getAccessToken(), new Callback<GrievanceCommentAPIResponse>() {
            @Override
            public void success(GrievanceCommentAPIResponse grievanceCommentAPIResponse, Response response) {

                GrievanceCommentAPIResult grievanceCommentAPIResult = grievanceCommentAPIResponse.getGrievanceCommentAPIResult();

                listView.setAdapter(new GrievanceCommentAdapter(grievanceCommentAPIResult.getGrievanceComments(), GrievanceDetailsActivity.this));


            }

            @Override
            public void failure(RetrofitError error) {

                try {
                    Toast.makeText(GrievanceDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(GrievanceDetailsActivity.this, "An unexpected error occurred while retrieving comments", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String action = (String) actionsSpinner.getSelectedItem();
                String comment = updateComment.getText().toString().trim();
                String feedback = "";
                if (feedbackSpinner.getVisibility() == View.VISIBLE) {
                    feedback = (String) feedbackSpinner.getSelectedItem();
                }

                if (action == null) {
                    Toast.makeText(GrievanceDetailsActivity.this, "Please select an action", Toast.LENGTH_SHORT).show();
                } else {
                    if (action.equals("Update") && comment.isEmpty()) {
                        Toast.makeText(GrievanceDetailsActivity.this, "Comment is necessary for this action", Toast.LENGTH_SHORT).show();
                    } else if (feedback == null) {
                        {
                            Toast.makeText(GrievanceDetailsActivity.this, "Please select a feedback option", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        switch (action) {
                            case "Update":
                                action = grievance.getStatus();
                                break;
                            case "Withdrawn":
                                action = "COMPLETED";
                                break;
                            case "Re-open":
                                action = "REGISTERED";
                                break;
                        }

                        progressDialog.show();

                        ApiController.getAPI().updateGrievance(grievance.getCrn(), new GrievanceUpdate(action, feedback.toUpperCase(), comment), sessionManager.getAccessToken(), new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {

                                Toast.makeText(GrievanceDetailsActivity.this, R.string.grievanceupdated_msg, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                ApiController.getAPI().getComplaintHistory(grievance.getCrn(), sessionManager.getAccessToken(), new Callback<GrievanceCommentAPIResponse>() {
                                    @Override
                                    public void success(GrievanceCommentAPIResponse grievanceCommentAPIResponse, Response response) {

                                        GrievanceCommentAPIResult grievanceCommentAPIResult = grievanceCommentAPIResponse.getGrievanceCommentAPIResult();

                                        listView.setAdapter(new GrievanceCommentAdapter(grievanceCommentAPIResult.getGrievanceComments(), GrievanceDetailsActivity.this));

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                        try {
                                            Toast.makeText(GrievanceDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Toast.makeText(GrievanceDetailsActivity.this, "An unexpected error occurred while retrieving comments", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }

                            @Override
                            public void failure(RetrofitError error) {

                                try {
                                    Toast.makeText(GrievanceDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(GrievanceDetailsActivity.this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();

                            }
                        });

                    }
                }
            }
        });

    }

    private int resolveStatus(String s) {
        if (s.equals("REGISTERED"))
            return R.string.registered_info;
        if (s.equals("PROCESSING") || s.equals("FORWARDED"))
            return R.string.processing_label;
        if (s.equals("COMPLETED"))
            return R.string.completed_label;
        if (s.equals("REJECTED"))
            return R.string.rejected_label;

        return 0;
    }

    private class GrievanceImagePagerAdapter extends FragmentPagerAdapter {

        public GrievanceImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (grievance.getSupportDocsSize() != 0)
                return GrievanceImageFragment.instantiateItem(getSessionManager().getAccessToken(), grievance.getCrn(), String.valueOf(grievance.getSupportDocsSize() - position));

            return null;
        }

        @Override
        public int getCount() {
            return grievance.getSupportDocsSize();
        }
    }

    private void getAddress(Double lat, Double lng) {

        Intent intent = new Intent(this, AddressService.class);
        intent.putExtra(AddressService.LAT, lat);
        intent.putExtra(AddressService.LNG, lng);
        startService(intent);
    }

    @SuppressWarnings("unused")
    public void onEvent(AddressReadyEvent addressReadyEvent) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                complaintLocation.setText(AddressService.addressResult);
            }
        });

    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
