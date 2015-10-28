package com.egovernments.egov.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egovernments.egov.R;
import com.egovernments.egov.fragments.GrievanceImageFragment;
import com.egovernments.egov.models.Grievance;
import com.viewpagerindicator.LinePageIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GreivanceDetailsActivity extends BaseActivity {

    public static final String GRIEVANCEITEM = "GrievanceItem";
    private Grievance grievance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_details);
        grievance = (Grievance) getIntent().getSerializableExtra(GRIEVANCEITEM);

        TextView complaintDate = (TextView) findViewById(R.id.details_complaint_date);
        TextView complaintType = (TextView) findViewById(R.id.details_complaint_type);
        TextView complaintDetails = (TextView) findViewById(R.id.details_complaint_details);
        TextView complaintStatus = (TextView) findViewById(R.id.details_complaint_status);
        TextView complaintLocation = (TextView) findViewById(R.id.details_complaint_location);
        TextView complaintNo = (TextView) findViewById(R.id.details_complaintNo);

        ImageView default_image = (ImageView) findViewById(R.id.details_defaultimage);
        RelativeLayout imagelayout = (RelativeLayout) findViewById(R.id.details_imageslayout);

        if (grievance.getSupportDocsSize() == 0) {
            default_image.setVisibility(View.VISIBLE);
            imagelayout.setVisibility(View.GONE);
        } else {
            ViewPager viewPager = (ViewPager) findViewById(R.id.details_complaint_image);
            viewPager.setAdapter(new GrievanceImagePagerAdapter(getSupportFragmentManager()));

            LinePageIndicator linePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
            linePageIndicator.setViewPager(viewPager);
        }


        try {
            complaintDate.setText(new SimpleDateFormat("EEEE, d MMMM, yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.ENGLISH).parse(grievance.getCreatedDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        complaintType.setText(grievance.getComplaintTypeName());
        complaintDetails.setText(grievance.getDetail());
        complaintLocation.setText(grievance.getLocationName());
        complaintNo.setText(grievance.getCrn());

        //TODO find out different complaint stasuseses
        if (grievance.getStatus().equals("REGISTERED"))
            complaintStatus.setText(R.string.registered_info);

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
