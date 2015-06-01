package com.egov.android.view.activity;

import com.egov.android.R;
import com.egov.android.controller.ApiController;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.listener.Event;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class EditProfileActivity extends BaseActivity {

    private boolean toastShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ((Button) findViewById(R.id.editprofile_doEditprofile)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.editprofile_doEditprofile:
                editProfile();
                break;
        }
    }

    private void editProfile() {

        _clearStatus(new int[] { R.id.edit_profile_name_status, R.id.edit_profile_mobile_status,
                R.id.edit_profile_email_status, R.id.edit_profile_alt_contact_status,
                R.id.edit_profile_dob_status });

        EditText name = (EditText) findViewById(R.id.edit_profile_name);
        EditText phone = (EditText) findViewById(R.id.edit_profile_mobile);
        EditText email = (EditText) findViewById(R.id.edit_profile_email);
        EditText alt_conatct_no = (EditText) findViewById(R.id.edit_profile_alt_contact);
        EditText date_of_birth = (EditText) findViewById(R.id.edit_profile_dob);

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.gender);
        int selected = radiogroup.getCheckedRadioButtonId();
        RadioButton b = (RadioButton) findViewById(selected);
        b.getText();

        if (isEmpty(name.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_name_status, "Please enter name");
        }
        if (isEmpty(phone.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_mobile_status, "Please enter mobile number");
        }
        if (isEmpty(email.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_email_status, "Please enter email");
        }
        if (isEmpty(alt_conatct_no.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_alt_contact_status,
                    "Please enter alternative contact number");
        }
        if (isEmpty(date_of_birth.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_dob_status,
                    "Please enter alternative contact number");
        }
        if (!isEmpty(name.getText().toString()) && !isEmpty(phone.getText().toString())
                && !isEmpty(email.getText().toString())
                && !isEmpty(alt_conatct_no.getText().toString())
                && !isEmpty(date_of_birth.getText().toString())) {
            ApiController.getInstance().updateProfile(this);
        }
    }

    private void _clearStatus(int[] ids) {
        toastShown = false;
        for (int id : ids) {
            setImageBackground(id, "success");
        }
    }

    private void _changeStatus(String type, int id, String message) {
        setImageBackground(id, type);
        showMsg(message);
    }

    private void showMsg(String message) {
        if (toastShown == false && message != null && !message.equals("")) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
            toastShown = true;
        }
    }

    private boolean isEmpty(String data) {
        return (data == null || (data != null && data.trim().equals("")));
    }

    private void setImageBackground(int id, String type) {
        int image = (type == "success") ? R.drawable.success_icon : R.drawable.error_icon;
        ((ImageView) findViewById(id)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(id)).setBackgroundResource(image);
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMsg(msg);
        if (status.equalsIgnoreCase("success")) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

}
