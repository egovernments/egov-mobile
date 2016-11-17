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


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.events.ProfileUpdatedEvent;
import org.egovernments.egoverp.models.Profile;
import org.egovernments.egoverp.models.ProfileUpdateFailedEvent;
import org.egovernments.egoverp.services.UpdateService;

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

    //private ProgressDialog progressDialog;

    public static boolean isUpdateFailed = false;

    private final int ACTION_UPDATE_REQUIRED = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final FloatingActionButton profileEditButton = (FloatingActionButton) findViewById(R.id.profile_edit);
        progressDialog=new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
/*
        final com.melnykov.fab.FloatingActionButton profileEditButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.profile_editcompat);
*/

        //progressBar = (ProgressBar) findViewById(R.id.profile_placeholder);

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                intent.putExtra(ProfileEditActivity.PROFILE_EDIT_CONTENT, profile);
                startActivityForResult(intent, ACTION_UPDATE_REQUIRED);
            }
        };

        profileEditButton.setOnClickListener(onClickListener);
        /*if (Build.VERSION.SDK_INT >= 21) {
            profileEditButton.setOnClickListener(onClickListener);
        } else {
            profileEditButton.setVisibility(View.GONE);
            profileEditButtonCompat.setVisibility(View.VISIBLE);
            profileEditButtonCompat.setOnClickListener(onClickListener);
        }*/
        if (profile != null) {
            updateProfile(profile);
        }
        else
        {
            progressDialog.show();
            getProfileDetailsFromServer();
        }

        if (isUpdateFailed) {
            //progressBar.setVisibility(View.GONE);
            isUpdateFailed = false;
        }

        final CollapsingToolbarLayout collapsingToolbar=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

        AppBarLayout appBar=(AppBarLayout)findViewById(R.id.appbar);


        final ImageView imgProfile=(ImageView)findViewById(R.id.profile_image);
        AppBarLayout.OnOffsetChangedListener mListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(collapsingToolbar.getHeight() + verticalOffset < 5 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
                    imgProfile.setAlpha(0.5f);
                } else {
                    imgProfile.setAlpha(1f);
                }
            }
        };

        appBar.addOnOffsetChangedListener(mListener);

    }

    //Cause the layout items to be refreshed
    private void updateProfile(Profile profile) {

        //progressBar.setVisibility(View.GONE);

            if(progressDialog.isShowing()) {
                sessionManager.setName(profile.getName());
                progressDialog.dismiss();
                finish();
                startActivity(getIntent());
                return;
            }

            sessionManager.setName(profile.getName());
            sessionManager.setMobileNo(profile.getMobileNumber());

            getSupportActionBar().setTitle(sessionManager.getName());
            final TextView name = (TextView) findViewById(R.id.profile_name);
            final TextView emailId = (TextView) findViewById(R.id.profile_email);
            final TextView mobileNo = (TextView) findViewById(R.id.profile_phoneno);
            final TextView altMobileNo = (TextView) findViewById(R.id.profile_altphoneno);
            final TextView dob = (TextView) findViewById(R.id.profile_dateofbirth);
            final TextView aadhaar = (TextView) findViewById(R.id.profile_aadhaarcardno);
            final TextView pan = (TextView) findViewById(R.id.profile_PANcardno);
            final TextView gender = (TextView) findViewById(R.id.profile_gender);

            name.setText(validateIsEmpty(profile.getName()));
            emailId.setText(validateIsEmpty(profile.getEmailId()));
            mobileNo.setText(validateIsEmpty(profile.getMobileNumber()));
            altMobileNo.setText(validateIsEmpty(profile.getAltContactNumber()));
            if (!TextUtils.isEmpty(profile.getDob())) {
                try {
                    dob.setText(new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(profile.getDob())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                dob.setText("-");
            }
            aadhaar.setText(validateIsEmpty(profile.getAadhaarNumber()));
            pan.setText(validateIsEmpty(profile.getPan()));
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
                    default:
                        gender.setText("-");
                        break;

                }
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_UPDATE_REQUIRED && resultCode == RESULT_OK) {
            recreate();
        }

    }

    public String validateIsEmpty(String value)
    {
        if(TextUtils.isEmpty(value))
        {
            return "-";
        }
        return value;
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
        updateProfile(profileUpdatedEvent.getProfile());
    }

    @SuppressWarnings("unused")
    public void onEvent(ProfileUpdateFailedEvent profileUpdateFailedEvent) {
        finish();
        //progressBar.setVisibility(View.GONE);
        //progressDialog.show();
    }

    void getProfileDetailsFromServer()
    {
        Intent intent = new Intent(ProfileActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_PROFILE);
        startService(intent);
    }

}
