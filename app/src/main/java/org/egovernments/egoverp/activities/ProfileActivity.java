package org.egovernments.egoverp.activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.events.ProfileUpdatedEvent;
import org.egovernments.egoverp.models.Profile;
import org.egovernments.egoverp.models.ProfileUpdateFailedEvent;
import org.egovernments.egoverp.network.UpdateService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * The profile screen activity
 **/

public class ProfileActivity extends BaseActivity {

    public static Profile profile = null;

    private ProgressBar progressBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    public static boolean isUpdateFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final FloatingActionButton profileEditButton = (FloatingActionButton) findViewById(R.id.profile_edit);
        final com.melnykov.fab.FloatingActionButton profileEditButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.profile_editcompat);

        progressBar = (ProgressBar) findViewById(R.id.profile_placeholder);

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                intent.putExtra(ProfileEditActivity.PROFILE_EDIT_CONTENT, profile);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= 21) {
            profileEditButton.setOnClickListener(onClickListener);
        } else {
            profileEditButton.setVisibility(View.GONE);
            profileEditButtonCompat.setVisibility(View.VISIBLE);
            profileEditButtonCompat.setOnClickListener(onClickListener);
        }
        if (profile != null)
            updateProfile();

        if (isUpdateFailed) {
            progressBar.setVisibility(View.GONE);
            isUpdateFailed = false;
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.profile_refreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(ProfileActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_PROFILE);
                startService(intent);
            }
        });

    }

    //Cause the layout items to be refreshed
    private void updateProfile() {

        progressBar.setVisibility(View.GONE);

        final TextView name = (TextView) findViewById(R.id.profile_name);
        final TextView emailId = (TextView) findViewById(R.id.profile_email);
        final TextView mobileNo = (TextView) findViewById(R.id.profile_phoneno);
        final TextView altMobileNo = (TextView) findViewById(R.id.profile_altphoneno);
        final TextView dob = (TextView) findViewById(R.id.profile_dateofbirth);
        final TextView aadhaar = (TextView) findViewById(R.id.profile_aadhaarcardno);
        final TextView pan = (TextView) findViewById(R.id.profile_PANcardno);
        final TextView gender = (TextView) findViewById(R.id.profile_gender);

        name.setText(profile.getName());
        emailId.setText(profile.getEmailId());
        mobileNo.setText(profile.getMobileNumber());
        altMobileNo.setText(profile.getAltContactNumber());
        if (profile.getDob() != null) {
            try {
                dob.setText(new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(profile.getDob())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        aadhaar.setText(profile.getAadhaarCard());
        pan.setText(profile.getPanCard());
        if (profile.getGender() != null) {
            switch (profile.getGender()) {
                case "MALE":
                    gender.setText(R.string.gender_male_label);
                    break;

                case "FEMALE":
                    gender.setText(R.string.gender_female_label);
                    break;

                case "OTHERS":
                    gender.setText(R.string.gender_unmentioned_label);
                    break;
            }
        }
    }

    //Subscribes the activity to events
    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    //Unsubscribes the activity to events
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //Updates the profile page when subscribed and a ProfileUpdatedEvent is posted by the UpdateService
    @SuppressWarnings("unused")
    public void onEvent(ProfileUpdatedEvent profileUpdatedEvent) {
        updateProfile();
        swipeRefreshLayout.setRefreshing(false);
    }

    @SuppressWarnings("unused")
    public void onEvent(ProfileUpdateFailedEvent profileUpdateFailedEvent) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

}
