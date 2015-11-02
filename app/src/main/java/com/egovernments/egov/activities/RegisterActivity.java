package com.egovernments.egov.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.helper.NothingSelectedSpinnerAdapter;
import com.egovernments.egov.models.User;
import com.egovernments.egov.network.ApiController;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterActivity extends AppCompatActivity {

    private String name;
    private String phoneno;
    private String email;
    private String password;
    private String confirmpassword;
    private String deviceID;
    private String deviceOS;
    private String deviceType;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        Spinner dropdown = (Spinner) findViewById(R.id.signup_city);
        String[] items = new String[]{"Bangalore", "Chennai", "Hyderabad"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.view_register_spinner, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.view_register_spinner, this));

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCancelable(false);

        final EditText name_edittext = (EditText) findViewById(R.id.signup_name);
        final EditText email_edittext = (EditText) findViewById(R.id.signup_email);
        final EditText phoneno_edittext = (EditText) findViewById(R.id.signup_phoneno);
        final EditText password_edittext = (EditText) findViewById(R.id.signup_password);
        final EditText confirmpassword_edittext = (EditText) findViewById(R.id.signup_confirmpassword);

        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceOS = Integer.toString(Build.VERSION.SDK_INT);
        deviceType = "mobile";


        Button register = (Button) findViewById(R.id.signup_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                name = name_edittext.getText().toString().trim();
                email = email_edittext.getText().toString().trim();
                phoneno = phoneno_edittext.getText().toString().trim();
                password = password_edittext.getText().toString().trim();
                confirmpassword = confirmpassword_edittext.getText().toString().trim();
                submit(name, email, phoneno, password, confirmpassword);
            }
        });

        confirmpassword_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    name = name_edittext.getText().toString().trim();
                    email = email_edittext.getText().toString().trim();
                    phoneno = phoneno_edittext.getText().toString().trim();
                    password = password_edittext.getText().toString().trim();
                    confirmpassword = confirmpassword_edittext.getText().toString().trim();
                    submit(name, email, phoneno, password, confirmpassword);
                    return true;
                }
                return false;
            }
        });
    }

    private boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String expression = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$;:+=-_?()!]).{8,32})";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void submit(final String name, final String email, final String phoneno, final String password, final String confirmpassword) {

        if (name.isEmpty() || email.isEmpty() || phoneno.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (phoneno.length() != 10) {
            Toast.makeText(RegisterActivity.this, "Phone no. must be 10 digits", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (!isValidEmail(email)) {
            Toast.makeText(RegisterActivity.this, "Please enter a valid email ID", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (!isValidPassword(password)) {
            Toast.makeText(RegisterActivity.this, "Password must be 8-32 characters long, containing at least one uppercase and one lowercase letter, and one number or special character excluding '& < > # % \" ' / \\' and space", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (!password.equals(confirmpassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else {


            ApiController.getAPI().registerUser(new User(email, phoneno, name, password, deviceID, deviceType, deviceOS), new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {

                    Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();


                    ApiController.getAPI().sendOTP(phoneno, new Callback<JsonObject>() {
                        @Override
                        public void success(JsonObject jsonObject, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                    progressDialog.dismiss();

                    Intent intent = new Intent(RegisterActivity.this, AccountActivationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username", phoneno);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finish();
                }


                @Override
                public void failure(RetrofitError error) {
                    JsonObject jsonObject = null;
                    progressDialog.dismiss();

                    if (error != null) {
                        try {
                            jsonObject = (JsonObject) error.getBody();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Server is down for maintenance or over capacity", Toast.LENGTH_LONG).show();
                        }
                        if (jsonObject != null)
                            Toast.makeText(RegisterActivity.this, "An account already exists with that email ID or mobile no.", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(RegisterActivity.this, "You are not connected to the internet", Toast.LENGTH_LONG).show();

                }
            });
        }
    }


}
