package com.egovernments.egov.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.adapters.GrievanceCommentAdapter;
import com.egovernments.egov.fragments.GrievanceImageFragment;
import com.egovernments.egov.helper.NothingSelectedSpinnerAdapter;
import com.egovernments.egov.models.Grievance;
import com.egovernments.egov.models.GrievanceCommentAPIResponse;
import com.egovernments.egov.models.GrievanceCommentAPIResult;
import com.egovernments.egov.models.GrievanceUpdate;
import com.egovernments.egov.network.ApiController;
import com.google.gson.JsonObject;
import com.viewpagerindicator.LinePageIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GrievanceDetailsActivity extends BaseActivity {

    public static final String GRIEVANCE_ITEM = "GrievanceItem";

    private Grievance grievance;

    private ListView listView;

    private EditText updateComment;

    private boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_details);
        grievance = (Grievance) getIntent().getSerializableExtra(GRIEVANCE_ITEM);

        TextView complaintDate = (TextView) findViewById(R.id.details_complaint_date);
        TextView complaintType = (TextView) findViewById(R.id.details_complaint_type);
        TextView complaintDetails = (TextView) findViewById(R.id.details_complaint_details);
        TextView complaintStatus = (TextView) findViewById(R.id.details_complaint_status);
        TextView complaintLocation = (TextView) findViewById(R.id.details_complaint_location);
        TextView complaintNo = (TextView) findViewById(R.id.details_complaintNo);

        Button updateButton = (Button) findViewById(R.id.grievance_update_button);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.update_grievance_layout);

        updateComment = (EditText) findViewById(R.id.update_comment);

        listView = (ListView) findViewById(R.id.grievance_comments);

        final Spinner spinner = (Spinner) findViewById(R.id.update_action);
        ArrayList<String> strings = new ArrayList<>(Arrays.asList("Update", "Withdrawn"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(GrievanceDetailsActivity.this, R.layout.view_grievanceupdate_spinner, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.view_grievanceupdate_spinner, GrievanceDetailsActivity.this));

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
        complaintLocation.setText(grievance.getLocationName());
        complaintNo.setText(grievance.getCrn());
        complaintStatus.setText(resolveStatus(grievance.getStatus()));

        if (grievance.getStatus().equals("COMPLETED") || grievance.getStatus().equals("REJECTED")) {

            linearLayout.setVisibility(View.GONE);
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

                Toast.makeText(GrievanceDetailsActivity.this, "Could not retrieve comments", Toast.LENGTH_SHORT).show();

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String action = (String) spinner.getSelectedItem();
                String comment = updateComment.getText().toString().trim();

                if (!isSelected) {
                    Toast.makeText(GrievanceDetailsActivity.this, "Please select an action", Toast.LENGTH_SHORT).show();
                } else if (action.equals("Update") && comment.isEmpty()) {
                    Toast.makeText(GrievanceDetailsActivity.this, "Comment is necessary for this action", Toast.LENGTH_SHORT).show();
                } else {

                    if (action.equals("Update")) {
                        action = grievance.getStatus();
                    } else if (action.equals("Withdrawn")) {
                        action = "COMPLETED";
                    }
                    ApiController.getAPI()
                            .updateGrievance(grievance.getCrn(), new GrievanceUpdate(action, comment), sessionManager.getAccessToken(), new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, Response response) {

                                    Toast.makeText(GrievanceDetailsActivity.this, "Grievance updated", Toast.LENGTH_SHORT).show();


                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    Toast.makeText(GrievanceDetailsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();

                                }
                            });

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
}
