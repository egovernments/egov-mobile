package com.egovernments.egov.activities;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.events.ProfileUpdatedEvent;
import com.egovernments.egov.helper.NothingSelectedSpinnerAdapter;
import com.egovernments.egov.models.Profile;
import com.egovernments.egov.models.ProfileAPIResponse;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.SessionManager;
import com.egovernments.egov.network.UpdateService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileEditActivity extends BaseActivity {

    private EditText profileName;
    private EditText profilePhone;
    private EditText profileAltPhone;
    private EditText profileEmail;
    private EditText profileDOB;
    private EditText profileAadhaar;
    private EditText profilePAN;

    private RadioGroup profileGender;
    private ProgressDialog progressDialog;

    private SessionManager sessionManager;

    private String date_of_birth;

    public static final String PROFILE_EDIT_CONTENT = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Profile profile = (Profile) getIntent().getSerializableExtra(PROFILE_EDIT_CONTENT);

        sessionManager = new SessionManager(getApplicationContext());

        Spinner dropdown = (Spinner) findViewById(R.id.editprofile_municipality);
        String[] items = new String[]{"Bangalore", "Chennai", "Hyderabad"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.view_register_spinner, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.view_register_spinner, this));

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCancelable(false);

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
                    //noinspection SpellCheckingInspection
                    profileDOB.setText(new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(profile.getDob())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            profileAadhaar.setText(profile.getAadhaarCard());
            profilePAN.setText(profile.getPanCard());

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
                }
            }


            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    profileDOB.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year));
                    date_of_birth = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth);
                }
            };

            Calendar dob = Calendar.getInstance();
            if (profile.getDob() != null) {
                try {
                    dob.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(profile.getDob()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            final DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileEditActivity.this, onDateSetListener, dob.get(Calendar.YEAR), dob.get(Calendar.MONTH), dob.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setCancelable(false);
            datePickerDialog.setTitle(R.string.dob_label_editprofile);

            profileDOB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.show();
                }
            });
        }

        FloatingActionButton profileEditPictureButton = (FloatingActionButton) findViewById(R.id.editprofile_changeimage);
        com.melnykov.fab.FloatingActionButton profileEditPictureButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.editprofile_changeimagecompat);

        if (Build.VERSION.SDK_INT >= 21) {

//            profileEditPictureButton.setOnClickListener(onClickListener);

        } else {

            profileEditPictureButton.setVisibility(View.GONE);
            profileEditPictureButtonCompat.setVisibility(View.VISIBLE);
//            profileEditButtonCompat.setOnClickListener(onClickListener);

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
                progressDialog.show();
                submit();
                break;

            case R.id.badge:
                Intent intent = new Intent(ProfileEditActivity.this, NotificationsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

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
            Toast.makeText(ProfileEditActivity.this, "Phone no. must be 10 digits", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (mobileNumber.isEmpty()) {
            Toast.makeText(ProfileEditActivity.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (emailId.isEmpty()) {
            Toast.makeText(ProfileEditActivity.this, "Please enter email ID", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!altContactNumber.isEmpty() && altContactNumber.length() != 10) {
            Toast.makeText(ProfileEditActivity.this, "Alternate Phone no. must be 10 digits", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!isValidEmail(emailId)) {
            Toast.makeText(ProfileEditActivity.this, "Please enter a valid email ID", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (dob.isEmpty()) {
            Toast.makeText(ProfileEditActivity.this, "Please enter date of birth", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
            Profile update_profile = new Profile(name, emailId, mobileNumber, mobileNumber, altContactNumber, gender, panCard, dob, aadhaarCard);

            ApiController.getAPI().updateProfile(update_profile, sessionManager.getAccessToken(), new Callback<ProfileAPIResponse>() {
                @Override
                public void success(ProfileAPIResponse profileAPIResponse, Response response) {

                    Toast.makeText(ProfileEditActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();

                    ProfileActivity.profile = profileAPIResponse.getProfile();

                    EventBus.getDefault().post(new ProfileUpdatedEvent());

                    progressDialog.dismiss();
                    Intent intent = new Intent(ProfileEditActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    startService(new Intent(ProfileEditActivity.this, UpdateService.class)
                            .putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_PROFILE));
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {

                    Toast.makeText(ProfileEditActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            });

        }
    }

    private boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
