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

package org.egov.employee.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.viewpagerindicator.LinePageIndicator;

import org.egov.employee.adapter.NothingSelectedSpinnerAdapter;
import org.egov.employee.api.ApiController;
import org.egov.employee.data.ComplaintHistory;
import org.egov.employee.data.ComplaintViewAPIResponse;
import org.egov.employee.data.Grievance;
import org.egov.employee.data.GrievanceUpdate;
import org.egov.employee.data.SupportDoc;
import org.egov.employee.event.AddressReadyEvent;
import org.egov.employee.service.AddressService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import offices.org.egov.egovemployees.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Displays the details of a complaint when clicked in GrievanceActivity recycler view
 **/

public class GrievanceDetailsActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String GRIEVANCE_ITEM = "GrievanceItem";
    public static final String GRIEVANCE_SUPPORT_DOCS = "GrievanceSupportDocs";

    private static Grievance grievance;

    private EditText updateComment;

    private ProgressDialog progressDialog;

    private String action;

    private boolean isComment = false;

    private ProgressBar progressBar;

    private LinearLayout layoutCompComments;

    private LinearLayout layoutToggleComments;

    private Button btnMoreComments;

    Spinner actionsSpinner;

    private RatingBar feedbackRatingBar;
    private TextView tvRatingBar;

    private final ArrayList<String> feedbackOptions = new ArrayList<>(Arrays.asList("UNSPECIFIED", "ONE", "TWO", "THREE", "FOUR", "FIVE"));
    private final String[] feedBackText=new String[]{"UNSPECIFIED", "VERY POOR", "POOR", "NOT BAD", "GOOD", "VERY GOOD"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableBackButton();

        grievance = (Grievance)getIntent().getExtras().get(GRIEVANCE_ITEM);
        grievance.setSupportDocs((ArrayList<SupportDoc>)getIntent().getExtras().get(GRIEVANCE_SUPPORT_DOCS));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView complaintLocation;
        CardView mapCardView;

        TextView complaintDate = (TextView) findViewById(R.id.details_complaint_date);
        TextView complaintType = (TextView) findViewById(R.id.details_complaint_type);
        TextView complainantName = (TextView) findViewById(R.id.details_complainant_name);
        TextView complainantPhoneNo = (TextView) findViewById(R.id.details_complainant_phoneno);
        TextView complainantEmail = (TextView) findViewById(R.id.details_complainant_email);
        TextView complaintDetails = (TextView) findViewById(R.id.details_complaint_details);
        TextView complaintStatus = (TextView) findViewById(R.id.details_complaint_status);
        TextView complaintLandmark = (TextView) findViewById(R.id.details_complaint_landmark);
        TextView complaintNo = (TextView) findViewById(R.id.details_complaintNo);
        TextView commentBoxLabel = (TextView) findViewById(R.id.commentbox_label);
        LinearLayout layoutLandMark=(LinearLayout)findViewById(R.id.layoutlandmark);
        complaintLocation = (TextView) findViewById(R.id.details_complaint_location);
        mapCardView=(CardView)findViewById(R.id.mapcardview);
        layoutCompComments =(LinearLayout)findViewById(R.id.complaintcommentscontainer);
        layoutToggleComments = (LinearLayout) findViewById(R.id.complainttogglecomments);
        btnMoreComments=(Button)findViewById(R.id.btnmorecomments);
        feedbackRatingBar=(RatingBar)findViewById(R.id.feedbackRatingBar);
        tvRatingBar=(TextView)findViewById(R.id.tvRatingBar);

        final LinearLayout feedbackLayout = (LinearLayout) findViewById(R.id.feedback_layout);

        Button updateButton = (Button) findViewById(R.id.grievance_update_button);

        updateComment = (EditText) findViewById(R.id.update_comment);

        progressBar = (ProgressBar) findViewById(R.id.grievance_history_placeholder);

        actionsSpinner = (Spinner) findViewById(R.id.update_action);
        ArrayList<String> actions_open = new ArrayList<>(Arrays.asList("Select", "Withdraw"));
        ArrayList<String> actions_closed = new ArrayList<>(Arrays.asList("Select", "Re-open"));

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCancelable(false);

        btnMoreComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutToggleComments.getVisibility() == View.VISIBLE)
                {
                    btnMoreComments.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_white_24dp, 0);
                    layoutToggleComments.setVisibility(View.GONE);
                }
                else
                {
                    btnMoreComments.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_up_white_24dp,0);
                    layoutToggleComments.setVisibility(View.VISIBLE);
                }
            }
        });

        //The layout for uploaded images
        RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.details_imageslayout);

        //If no uploaded images
        if (grievance.getSupportDocsSize() == 0) {
            imageLayout.setVisibility(View.GONE);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewPager viewPager = (ViewPager) findViewById(R.id.details_complaint_image);
                    viewPager.setAdapter(new GrievanceImagePagerAdapter(getSupportFragmentManager()));
                    LinePageIndicator linePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
                    linePageIndicator.setViewPager(viewPager);
                }
            });

        }

        //Parses complaint date into a more readable format
        complaintDate.setText(formatDateString(grievance.getCreatedDate(),"yyyy-MM-dd hh:mm:ss.SSS","dd/MM/yyyy hh:mm aa"));

        complainantName.setText(grievance.getComplainantName());

        complaintType.setText(grievance.getComplaintTypeName());
        complaintDetails.setText(grievance.getDetail());
        if (TextUtils.isEmpty(grievance.getLandmarkDetails())) {
            layoutLandMark.setVisibility(View.GONE);
        } else
            complaintLandmark.setText(grievance.getLandmarkDetails());

        //If grievance has lat/lng values, location name is null
        if (grievance.getLat() == null || grievance.getLat() <= 0) {
            mapCardView.setVisibility(View.GONE);
            String complaintloc =grievance.getChildLocationName() + " - " + grievance.getLocationName();
            complaintLocation.setText(complaintloc);
        }
        else {
            complaintLocation.setVisibility(View.GONE);
            getAddress(grievance.getLat(), grievance.getLng());
            final MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        map.getMapAsync(GrievanceDetailsActivity.this);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        }

        complaintNo.setText(grievance.getCrn());
        complaintStatus.setText(resolveStatus(grievance.getStatus()));

        if(!grievance.getStatus().equals("WITHDRAWN")) {
            //Display feedback spinner
            if (grievance.getStatus().equals("COMPLETED") || grievance.getStatus().equals("REJECTED")) {

                ArrayAdapter<String> adapter = new ArrayAdapter<>(GrievanceDetailsActivity.this, R.layout.spinner_view_template, actions_closed);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                actionsSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.spinner_view_template, GrievanceDetailsActivity.this));

                commentBoxLabel.setText(getResources().getString(R.string.feedback));

                feedbackLayout.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(grievance.getCitizenFeedback()))
                {
                    int rating=feedbackOptions.indexOf(grievance.getCitizenFeedback());
                    feedbackRatingBar.setRating(rating);
                    tvRatingBar.setText(feedBackText[rating]);
                }
                else
                {
                    tvRatingBar.setText(feedBackText[0]);
                }

            }
            //Display default spinners
            else {

                ArrayAdapter<String> adapter = new ArrayAdapter<>(GrievanceDetailsActivity.this, R.layout.spinner_view_template, actions_open);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                actionsSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.spinner_view_template, GrievanceDetailsActivity.this));
                commentBoxLabel.setText(getResources().getString(R.string.updatecomplaint));
            }
        }
        else
        {
            LinearLayout layoutGrievanceUpdate=(LinearLayout)findViewById(R.id.update_grievance_layout);
            layoutGrievanceUpdate.setVisibility(View.GONE);
        }


        //load complaint history
        loadComplaintHistory();

        feedbackRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

                int value= Math.round(rating);
                tvRatingBar.setText(feedBackText[value]);

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                action = (String) actionsSpinner.getSelectedItem();
                String comment = updateComment.getText().toString().trim();
                String feedback = "";
                if (feedbackLayout.getVisibility() == View.VISIBLE) {
                    feedback = feedbackOptions.get(Math.round(feedbackRatingBar.getRating()));
                }

                if (action == null) {
                    Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "Please select an action", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    if ((action.equals("Select") || action.equals("Re-open")) && TextUtils.isEmpty(comment)) {
                        Toast.makeText(GrievanceDetailsActivity.this, "Comment is necessary for this action", Toast.LENGTH_SHORT).show();
                    } else if (feedback == null) {
                        Toast toast = Toast.makeText(GrievanceDetailsActivity.this, "Please select a feedback option", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    } else {
                        switch (action) {
                            case "Select":
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


                        Call<JsonObject> updateComplaint = ApiController.getAPI(getApplicationContext(), GrievanceDetailsActivity.this).updateComplaint(grievance.getCrn(), new GrievanceUpdate(action, feedback.toUpperCase(), comment), preference.getApiAccessToken());

                        Callback<JsonObject> updateComplaintCallback = new Callback<JsonObject>() {

                            @Override
                            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                                progressDialog.dismiss();
                                if(isComment && feedbackLayout.getVisibility() == View.GONE)
                                {
                                    showSnackBar(getString(R.string.grievanceupdated_msg));
                                    loadComplaintHistory();
                                }
                                else{
                                    Intent intent=new Intent();
                                    intent.putExtra(GrievanceActivity.RESULT_MESSAGE,
                                            isComment && feedbackLayout.getVisibility() == View.VISIBLE? "Your feedback submitted successfully":"Complaint updated successfully");
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                progressDialog.dismiss();
                                showSnackBar(t.getLocalizedMessage());
                            }
                        };
                        updateComplaint.enqueue(updateComplaintCallback);


                        /*ApiController.getAPI(GrievanceDetailsActivity.this).updateGrievance(grievance.getCrn(), new GrievanceUpdate(action, feedback.toUpperCase(), comment), sessionManager.getAccessToken(), new Callback<JsonObject>() {
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

                                        loadComplaintComments(grievanceCommentAPIResult.getGrievanceComments());
                                        actionsSpinner.setSelection(0);
                                        feedbackSpinner.setSelection(0);
                                        updateComment.getText().clear();

                                        if (!isComment) {
                                            setResult(RESULT_OK,new Intent());
                                            finish();
                                        }

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        if (error.getLocalizedMessage() != null)
                                            if (error.getLocalizedMessage().equals(CustomErrorHandler.SESSION_EXPRIED_MESSAGE)) {
                                                Toast toast = Toast.makeText(GrievanceDetailsActivity.this, R.string.session_timeout, Toast.LENGTH_SHORT);
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
                                    if (error.getLocalizedMessage().equals(CustomErrorHandler.SESSION_EXPRIED_MESSAGE)) {
                                        Toast toast = Toast.makeText(GrievanceDetailsActivity.this, R.string.session_timeout, Toast.LENGTH_SHORT);
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
                        });*/

                    }
                }
            }
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_grievance_details;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        //Add marker at complaint filed location in map and zoom map to complaint location
        LatLng latLng = new LatLng(grievance.getLat(), grievance.getLng());
        googleMap.addMarker(new MarkerOptions().position(latLng));
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        googleMap.animateCamera(location);
    }

    private void loadComplaintHistory()
    {

        Call<ComplaintViewAPIResponse.HistoryAPIResponse> getCompHistory = ApiController.getAPI(getApplicationContext(), GrievanceDetailsActivity.this).getComplaintHistory(grievance.getCrn(), preference.getApiAccessToken());

        Callback<ComplaintViewAPIResponse.HistoryAPIResponse> complaintHistoryCallBack = new Callback<ComplaintViewAPIResponse.HistoryAPIResponse>() {

            @Override
            public void onResponse(Response<ComplaintViewAPIResponse.HistoryAPIResponse> response, Retrofit retrofit) {
                actionsSpinner.setSelection(0);
                updateComment.getText().clear();
                progressBar.setVisibility(View.GONE);
                loadComplaintComments(response.body().getResult().getComments());
            }

            @Override
            public void onFailure(Throwable t) {
                showSnackBar(t.getLocalizedMessage());
                progressBar.setVisibility(View.GONE);
            }
        };
        getCompHistory.enqueue(complaintHistoryCallBack);

    }

    //The viewpager custom adapter
    private class GrievanceImagePagerAdapter extends FragmentPagerAdapter {

        public GrievanceImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (grievance.getSupportDocsSize() != 0) {
                return GrievanceImageFragment.instantiateItem(position, preference.getApiAccessToken(), grievance.getSupportDocs().get(position).getFileId());
            }
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

    //return date format dd/MM/yyy hh:mm aa
    private String formatDateString(String dateText, String currentDateFormat, String outputDateFormat)
    {
        try {
            return new SimpleDateFormat(outputDateFormat, Locale.ENGLISH)
                    .format(new SimpleDateFormat(currentDateFormat, Locale.ENGLISH)
                            .parse(dateText));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void loadComplaintComments(List<ComplaintHistory> grievanceComments)
    {

        //more comments button show hide check
        if(grievanceComments.size()<=2)
        {
            btnMoreComments.setVisibility(View.GONE);
        }
        else{
            btnMoreComments.setVisibility(View.VISIBLE);
            String commentsText=(grievanceComments.size()-2)+" COMMENTS";
            btnMoreComments.setText(commentsText);
        }

        layoutToggleComments.removeAllViews();
        layoutCompComments.removeAllViews();


        for(int i=(grievanceComments.size()-1);i>=0;i--)
        {
            View commentItemTemplate=getLayoutInflater().inflate(R.layout.template_comment_item,null);
            ComplaintHistory comment=grievanceComments.get(i);
            TextView tvUserName=(TextView)commentItemTemplate.findViewById(R.id.commenter_name);



            //Log.v("USERNAME", preference.getUserName()+"<>"+ comment.getUpdatedBy().split("::")[0] +" -> ");

            if(preference.getUserName().equals(comment.getUpdatedBy()) || (preference.getUserName().equals(comment.getUpdatedBy().split("::")[0])))
                tvUserName.setText("Me");
            else if (comment.getUpdatedUserType().equals("EMPLOYEE")) {
                tvUserName.setText(comment.getUser());
                tvUserName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            }

            /*if (comment.getUpdatedUserType().equals("EMPLOYEE") || !comment.getUpdatedUserType().equals("CITIZEN")) {
                tvUserName.setText(comment.getUser());
                tvUserName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            }
            else
            {
                tvUserName.setText(comment.getUpdatedBy());
            }*/


            ((TextView)commentItemTemplate.findViewById(R.id.comment_datetime)).setText(formatDateString(comment.getDate(), "MMM dd, yyyy hh:mm:ss aa", "dd/MM/yyyy hh:mm aa"));
            if(!TextUtils.isEmpty(comment.getComments()))
            {
                ((TextView)commentItemTemplate.findViewById(R.id.comment_text)).setText(comment.getComments());
            }
            else
            {
                String commentText="Status has been changed into "+comment.getStatus();
                ((TextView)commentItemTemplate.findViewById(R.id.comment_text)).setText(commentText);
            }

            ((TextView)commentItemTemplate.findViewById(R.id.comment_status)).setText(comment.getStatus());

            if(i<=1)
            {
                layoutCompComments.addView(commentItemTemplate);
            }
            else {
                layoutToggleComments.addView(commentItemTemplate);
            }
        }
    }


    //Handles AddressReadyEvent posted by AddressService on success
    @SuppressWarnings("unused")
    public void onEvent(AddressReadyEvent addressReadyEvent) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.map_location_text)).setText(AddressService.addressResult);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
