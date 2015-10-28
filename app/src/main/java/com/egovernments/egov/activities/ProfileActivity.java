package com.egovernments.egov.activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egovernments.egov.R;
import com.egovernments.egov.events.ProfileUpdatedEvent;
import com.egovernments.egov.models.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class ProfileActivity extends BaseActivity {

    public static Profile profile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final FloatingActionButton profileEditbutton = (FloatingActionButton) findViewById(R.id.profile_edit);
        final com.melnykov.fab.FloatingActionButton profileEditbuttoncompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.profile_editcompat);

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                intent.putExtra(ProfileEditActivity.PROFILEEDIT_CONTENT, profile);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= 21) {
            profileEditbutton.setOnClickListener(onClickListener);
        } else {
            profileEditbutton.setVisibility(View.GONE);
            profileEditbuttoncompat.setVisibility(View.VISIBLE);
            profileEditbuttoncompat.setOnClickListener(onClickListener);
        }
        if (profile != null)
            updateProfile();

    }

    private void updateProfile() {


        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.profile_data);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.profile_placeholder);

        relativeLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        final TextView name = (TextView) findViewById(R.id.profile_name);
        final TextView emailId = (TextView) findViewById(R.id.profile_email);
        final TextView mobileno = (TextView) findViewById(R.id.profile_phoneno);
        final TextView altmobileno = (TextView) findViewById(R.id.profile_altphoneno);
        final TextView dob = (TextView) findViewById(R.id.profile_dateofbirth);
        final TextView aadhaar = (TextView) findViewById(R.id.profile_aadhaarcardno);
        final TextView pan = (TextView) findViewById(R.id.profile_PANcardno);
        final TextView gender = (TextView) findViewById(R.id.profile_gender);

        name.setText(profile.getName());
        emailId.setText(profile.getEmailId());
        mobileno.setText(profile.getMobileNumber());
        altmobileno.setText(profile.getAltContactNumber());
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

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    public void onEvent(ProfileUpdatedEvent profileUpdatedEvent) {
        updateProfile();
    }

}
