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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.viewpagerindicator.LinePageIndicator;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.fragments.GrievanceImageFragment;
import org.egovernments.egoverp.models.Grievance;
import org.egovernments.egoverp.models.GrievanceComment;
import org.egovernments.egoverp.models.GrievanceCommentAPIResponse;
import org.egovernments.egoverp.models.GrievanceCommentAPIResult;
import org.egovernments.egoverp.models.GrievanceUpdate;
import org.egovernments.egoverp.models.SupportDoc;
import org.egovernments.egoverp.services.AddressService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * Displays the details of a complaint when clicked in GrievanceActivity recycler view
 **/

public class GrievanceDetailsActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String GRIEVANCE_ITEM = "GrievanceItem";
    public static final String GRIEVANCE_SUPPORT_DOCS = "GrievanceSupportDocs";

    private static Grievance grievance;
    private final ArrayList<String> feedbackOptions = new ArrayList<>(Arrays.asList("UNSPECIFIED", "ONE", "TWO", "THREE", "FOUR", "FIVE"));
    private final String[] feedBackText = new String[]{"UNSPECIFIED", "VERY POOR", "POOR", "NOT BAD", "GOOD", "VERY GOOD"};
    Spinner actionsSpinner;
    String action;
    BroadcastReceiver addressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.map_location_text)).setText(intent.getStringExtra(AddressService.KEY_ADDRESS));
                }
            });

        }
    };
    private EditText updateComment;
    private boolean isComment = false;
    private ProgressBar progressBar;
    private LinearLayout layoutCompComments;
    private LinearLayout layoutToggleComments;
    private Button btnMoreComments;
    private RatingBar feedbackRatingBar;
    private TextView tvRatingBar;

    public static Grievance getGrievance() {
        return grievance;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.grievancedetails_label);
        setContentView(R.layout.activity_grievance_details);

        grievance = (Grievance)getIntent().getExtras().get(GRIEVANCE_ITEM);
        grievance.setSupportDocs((ArrayList<SupportDoc>)getIntent().getExtras().get(GRIEVANCE_SUPPORT_DOCS));

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView complaintLocation;
        CardView mapCardView;

        TextView complaintDate = (TextView) findViewById(R.id.details_complaint_date);
        TextView complaintType = (TextView) findViewById(R.id.details_complaint_type);
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

        btnMoreComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutToggleComments.getVisibility() == View.VISIBLE)
                {
                    btnMoreComments.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                    layoutToggleComments.setVisibility(View.GONE);
                }
                else
                {
                    btnMoreComments.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_up_black_24dp,0);
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
                actionsSpinner.setAdapter(adapter);

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
                actionsSpinner.setAdapter(adapter);
                commentBoxLabel.setText(getResources().getString(R.string.updatecomplaint));
            }

            actionsSpinner.setSelection(0);
        }
        else
        {
            LinearLayout layoutGrievanceUpdate=(LinearLayout)findViewById(R.id.update_grievance_layout);
            layoutGrievanceUpdate.setVisibility(View.GONE);
        }


        //load complaint history
        new LoadComplaintHistory().execute();

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
                updateComplaint(feedbackLayout);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(GrievanceDetailsActivity.this).registerReceiver(addressReceiver,
                new IntentFilter(AddressService.BROADCAST_ADDRESS_RECEIVER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(GrievanceDetailsActivity.this).unregisterReceiver(addressReceiver);
    }


    private void updateComplaint(final LinearLayout feedbackLayout) {
        action = (String) actionsSpinner.getSelectedItem();
        String comment = updateComment.getText().toString().trim();
        String feedback = "";
        if (feedbackLayout.getVisibility() == View.VISIBLE) {
            feedback = feedbackOptions.get(Math.round(feedbackRatingBar.getRating()));
        }

        if (action == null) {
            showSnackBar(getString(R.string.please_select_action));
        } else {
            if ((action.equals("Select") || action.equals("Re-open")) && TextUtils.isEmpty(comment)) {
                showSnackBar(getString(R.string.comment_is_necessary_action));
            } else if (feedback == null) {
                showSnackBar(getString(R.string.feedback_rating_info));
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

                Call<JsonObject> updateGrievanceCall = ApiController.getRetrofit2API(getApplicationContext())
                        .updateGrievance(grievance.getCrn(), new GrievanceUpdate(action, feedback.toUpperCase(), comment),
                                sessionManager.getAccessToken());

                if (validateInternetConnection()) {
                    updateGrievanceCall.enqueue(new retrofit2.Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                            if (isComment && feedbackLayout.getVisibility() == View.GONE) {
                                showSnackBar(R.string.grievanceupdated_msg);
                                loadComplaintHistory();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(GrievanceActivity.RESULT_MESSAGE,
                                        isComment && feedbackLayout.getVisibility() == View.VISIBLE ? getString(R.string.feedback_submitted) : getString(R.string.grievance_updated));
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            progressDialog.dismiss();

                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            progressDialog.dismiss();
                        }
                    });
                }

            }
        }
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

        Call<GrievanceCommentAPIResponse> grievanceCommentAPIResponseCall = ApiController.getRetrofit2API(getApplicationContext())
                .getComplaintHistory(grievance.getCrn(), sessionManager.getAccessToken());

        grievanceCommentAPIResponseCall.enqueue(new retrofit2.Callback<GrievanceCommentAPIResponse>() {
            @Override
            public void onResponse(Call<GrievanceCommentAPIResponse> call, retrofit2.Response<GrievanceCommentAPIResponse> response) {
                GrievanceCommentAPIResponse grievanceCommentAPIResponse = response.body();
                GrievanceCommentAPIResult grievanceCommentAPIResult = grievanceCommentAPIResponse.getGrievanceCommentAPIResult();
                actionsSpinner.setSelection(0);
                updateComment.getText().clear();
                progressBar.setVisibility(View.GONE);
                List<GrievanceComment> grievanceComments=grievanceCommentAPIResult.getGrievanceComments();
                loadComplaintComments(grievanceComments);
            }

            @Override
            public void onFailure(Call<GrievanceCommentAPIResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

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

    private void loadComplaintComments(List<GrievanceComment> grievanceComments)
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
            final ViewGroup nullParent = null;
            View commentItemTemplate=getLayoutInflater().inflate(R.layout.template_comment_item,nullParent);
            GrievanceComment comment=grievanceComments.get(i);
            TextView tvUserName=(TextView)commentItemTemplate.findViewById(R.id.commenter_name);

            if (comment.getUpdatedUserType().equals("EMPLOYEE") || !comment.getUpdatedUserType().equals("CITIZEN")) {
                tvUserName.setText(comment.getUser());
                tvUserName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            }
            else
            {
                tvUserName.setText(comment.getUpdatedBy());
            }

            ((TextView)commentItemTemplate.findViewById(R.id.comment_datetime)).setText(formatDateString(comment.getDate(), "MMM dd, yyyy hh:mm:ss aa", "dd/MM/yyyy hh:mm aa"));
            if(!TextUtils.isEmpty(comment.getComments()))
            {
                ((TextView)commentItemTemplate.findViewById(R.id.comment_text)).setText(comment.getComments());
            }
            else
            {
                String commentText = getString(R.string.status_changed_into) + comment.getStatus();
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

    //Load complaint History
    public class LoadComplaintHistory extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            loadComplaintHistory();
            return null;
        }
    }

    //The viewpager custom adapter
    private class GrievanceImagePagerAdapter extends FragmentPagerAdapter {

        GrievanceImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (grievance.getSupportDocsSize() != 0)
                return GrievanceImageFragment.instantiateItem(position, sessionManager.getAccessToken(), grievance.getSupportDocs().get(position).getFileId());
            return null;
        }

        @Override
        public int getCount() {
            return grievance.getSupportDocsSize();
        }
    }
}
