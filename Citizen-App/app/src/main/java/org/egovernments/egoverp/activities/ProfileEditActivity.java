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


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.models.Profile;
import org.egovernments.egoverp.models.ProfileAPIResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;

/**
 * The profile edit screen activity
 **/

public class ProfileEditActivity extends BaseActivity {

    public static final String PROFILE_EDIT_CONTENT = "profile";
    private EditText profileName;
    private EditText profilePhone;
    private EditText profileAltPhone;
    private EditText profileEmail;
    private EditText profileDOB;
    private EditText profileAadhaar;
    private EditText profilePAN;
    private RadioGroup profileGender;
    private String date_of_birth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Profile profile = (Profile) getIntent().getSerializableExtra(PROFILE_EDIT_CONTENT);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileName = (EditText) findViewById(R.id.editprofile_name);
        profilePhone = (EditText) findViewById(R.id.editprofile_phoneno);
        profileAltPhone = (EditText) findViewById(R.id.editprofile_altphoneno);
        profileEmail = (EditText) findViewById(R.id.editprofile_email);
        profileDOB = (EditText) findViewById(R.id.editprofile_dateofbirth);
        profileAadhaar = (EditText) findViewById(R.id.editprofile_aadhaarcardno);
        profilePAN = (EditText) findViewById(R.id.editprofile_PANcardno);

        if (profile != null) {
            profileName.setText(profile.getName());
            profilePhone.setText(profile.getMobileNumber());
            profileAltPhone.setText(profile.getAltContactNumber());
            profileEmail.setText(profile.getEmailId());
            date_of_birth = profile.getDob();
            if (!(profile.getDob() == null)) {
                try {
                    profileDOB.setText(new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(profile.getDob())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            profileAadhaar.setText(profile.getAadhaarNumber());
            profilePAN.setText(profile.getPan());

            profileGender = (RadioGroup) findViewById(R.id.editprofile_gender);
            profileGender.check(R.id.radioButton_others);

            if (profile.getGender() != null) {
                switch (profile.getGender()) {
                    case "MALE":
                        profileGender.check(R.id.radioButton_male);
                        break;
                    case "FEMALE":
                        profileGender.check(R.id.radioButton_female);
                        break;
                    default :
                        profileGender.check(R.id.radioButton_others);
                        break;
                }
            }
            else{
                profileGender.check(R.id.radioButton_male);
            }


            final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    try {
                        String selectedDateStr = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year);
                        profileDOB.setText(new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH).format(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(selectedDateStr)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    date_of_birth = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth);
                }
            };

            Calendar dob = Calendar.getInstance();
            if (!TextUtils.isEmpty(profile.getDob())) {
                try {
                    dob.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(profile.getDob()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            final DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileEditActivity.this, onDateSetListener, dob.get(Calendar.YEAR), dob.get(Calendar.MONTH), dob.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setCancelable(false);

            profileDOB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.show();
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profiledit_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.confirm:
                submit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Invokes call to the API
    private void submit() {

        profileName = (EditText) findViewById(R.id.editprofile_name);
        profilePhone = (EditText) findViewById(R.id.editprofile_phoneno);
        profileAltPhone = (EditText) findViewById(R.id.editprofile_altphoneno);
        profileEmail = (EditText) findViewById(R.id.editprofile_email);
        profileDOB = (EditText) findViewById(R.id.editprofile_dateofbirth);
        profileAadhaar = (EditText) findViewById(R.id.editprofile_aadhaarcardno);
        profilePAN = (EditText) findViewById(R.id.editprofile_PANcardno);

        profileGender = (RadioGroup) findViewById(R.id.editprofile_gender);
        String gender = null;
        if (profileGender.getCheckedRadioButtonId() == R.id.radioButton_male) {
            gender = "MALE";
        } else if (profileGender.getCheckedRadioButtonId() == R.id.radioButton_female) {
            gender = "FEMALE";
        } else if (profileGender.getCheckedRadioButtonId() == R.id.radioButton_others) {
            gender = "OTHERS";
        }

        String name = profileName.getText().toString().trim();
        String emailId = profileEmail.getText().toString().trim();
        String mobileNumber = profilePhone.getText().toString().trim();
        String altContactNumber = profileAltPhone.getText().toString().trim();
        String dob = date_of_birth;
        String panCard = profilePAN.getText().toString().trim();
        String aadhaarCard = profileAadhaar.getText().toString().trim();

        if (mobileNumber.length() != 10) {
            showSnackBar("Phone no. must be 10 digits");
        } else if (TextUtils.isEmpty(mobileNumber)) {
            showSnackBar("Please enter mobile number");
        } else if (TextUtils.isEmpty(emailId)) {
            showSnackBar("Please enter email ID");
        } else if (!TextUtils.isEmpty(altContactNumber) && altContactNumber.length() != 10) {
            showSnackBar("Alternate Phone no. must be 10 digits");
        } else if (!AppUtils.isValidEmail(emailId)) {
            showSnackBar("Please enter a valid email ID");
        } else if (TextUtils.isEmpty(dob)) {
            showSnackBar("Please enter date of birth");
        } else  if(!TextUtils.isEmpty(profileAadhaar.getText()) && profileAadhaar.getText().length()!=12) {
            showSnackBar("Please enter valid aadhar card number");
        } else  if(!TextUtils.isEmpty(profilePAN.getText()) && !AppUtils.isValidPANNo(profilePAN.getText().toString())) {
            showSnackBar("Please enter valid pan card number");
        }else {

            final Profile update_profile = new Profile(name, emailId, mobileNumber, mobileNumber, altContactNumber, gender, panCard, dob, aadhaarCard);

            progressDialog.show();

            Call<ProfileAPIResponse> profileAPIResponseCall = ApiController.getRetrofit2API(getApplicationContext())
                    .updateProfile(update_profile, sessionManager.getAccessToken());

            profileAPIResponseCall.enqueue(new retrofit2.Callback<ProfileAPIResponse>() {
                @Override
                public void onResponse(Call<ProfileAPIResponse> call, retrofit2.Response<ProfileAPIResponse> response) {

                    ProfileAPIResponse profileAPIResponse = response.body();

                    showSnackBar("Profile updated successfully");

                    ProfileActivity.profile = profileAPIResponse.getProfile();

                    progressDialog.dismiss();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);

                    finish();
                }

                @Override
                public void onFailure(Call<ProfileAPIResponse> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });

            /*ApiController.getAPI(ProfileEditActivity.this).updateProfile(update_profile, sessionManager.getAccessToken(), new Callback<ProfileAPIResponse>() {
                @Override
                public void success(ProfileAPIResponse profileAPIResponse, Response response) {

                    showSnackBar("Profile updated successfully");

                    ProfileActivity.profile = profileAPIResponse.getProfile();

                    progressDialog.dismiss();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);

                    finish();
                }

                @Override
                public void failure(RetrofitError error) {

                    if (error.getLocalizedMessage() != null)
                        if (error.getLocalizedMessage().equals(CustomErrorHandler.SESSION_EXPRIED_MESSAGE)) {
                            Toast toast = Toast.makeText(ProfileEditActivity.this, R.string.session_timeout, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                            sessionManager.logoutUser();
                            startActivity(new Intent(ProfileEditActivity.this, LoginActivity.class));
                        } else if (error.getLocalizedMessage().contains("400")) {
                            try {
                                ErrorResponse errorResponse = (ErrorResponse) error.getBodyAs(ErrorResponse.class);
                                Toast toast = Toast.makeText(ProfileEditActivity.this, errorResponse.getErrorStatus().getMessage(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(ProfileEditActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(ProfileEditActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    else {
                        Toast toast = Toast.makeText(ProfileEditActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }

                    progressDialog.dismiss();

                }
            });*/

        }
    }

}
