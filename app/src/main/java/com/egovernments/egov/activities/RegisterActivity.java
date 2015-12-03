package com.egovernments.egov.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.helper.ConfigManager;
import com.egovernments.egov.helper.CustomAutoCompleteTextView;
import com.egovernments.egov.helper.NothingSelectedSpinnerAdapter;
import com.egovernments.egov.models.City;
import com.egovernments.egov.models.User;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.SessionManager;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//TODO action bar back not working

public class RegisterActivity extends AppCompatActivity {

    private String name;
    private String phoneno;
    private String email;
    private String password;
    private String confirmpassword;
    private String deviceID;
    private String deviceOS;
    private String deviceType;

    private TextView municipalityInfo;

    private TextInputLayout nameInputLayout;

    private String url;
    private String cityName;

    private ProgressDialog progressDialog;

    private ConfigManager configManager;

    private Handler handler;

    private Spinner spinner;

    private SessionManager sessionManager;

    private CustomAutoCompleteTextView autoCompleteTextView;

    private int check = 0;

    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        spinner = (Spinner) findViewById(R.id.signup_city);

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCancelable(false);

        sessionManager = new SessionManager(this);

        municipalityInfo = (TextView) findViewById(R.id.municipality_change_info);
        nameInputLayout = (TextInputLayout) findViewById(R.id.signup_name_inputlayout);

        final EditText name_edittext = (EditText) findViewById(R.id.signup_name);
        final EditText email_edittext = (EditText) findViewById(R.id.signup_email);
        final EditText phoneno_edittext = (EditText) findViewById(R.id.signup_phoneno);
        final EditText password_edittext = (EditText) findViewById(R.id.signup_password);
        final EditText confirmpassword_edittext = (EditText) findViewById(R.id.signup_confirmpassword);

        autoCompleteTextView = (CustomAutoCompleteTextView) findViewById(R.id.register_spinner_autocomplete);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Fetching municipality list, please wait", Toast.LENGTH_SHORT).show();
            }
        });

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

                    progressDialog.show();

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

        handler = new Handler();

        try {
            InputStream inputStream = getAssets().open("egov.conf");
            configManager = new ConfigManager(inputStream, RegisterActivity.this);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new GetAllCitiesTask().execute();

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

        if (url == null || name.isEmpty() || email.isEmpty() || phoneno.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (phoneno.length() != 10) {
            Toast.makeText(RegisterActivity.this, "Phone no. must be 10 digits", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!isValidEmail(email)) {
            Toast.makeText(RegisterActivity.this, "Please enter a valid email ID", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!isValidPassword(password)) {
            Toast.makeText(RegisterActivity.this, "Password must be 8-32 characters long, containing at least one uppercase letter, one lowercase letter, and one number or special character excluding '& < > # % \" ' / \\' and space", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (!password.equals(confirmpassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {

            sessionManager.setBaseURL(url, cityName, code);
            User user = new User(email, phoneno, name, password, deviceID, deviceType, deviceOS);
            ApiController.getAPI(RegisterActivity.this).registerUser(user, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                        }
                    });


                    ApiController.getAPI(RegisterActivity.this).sendOTP(phoneno, new Callback<JsonObject>() {
                        @Override
                        public void success(JsonObject jsonObject, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });


                    Intent intent = new Intent(RegisterActivity.this, AccountActivationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username", phoneno);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finish();
                }


                @Override
                public void failure(final RetrofitError error) {


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            JsonObject jsonObject = null;

                            progressDialog.dismiss();

                            if (error != null) {
                                if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().contains("400")) {
                                    Toast.makeText(RegisterActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        jsonObject = (JsonObject) error.getBody();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (jsonObject != null)
                                        Toast.makeText(RegisterActivity.this, "An account already exists with that email ID or mobile no.", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(RegisterActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_LONG).show();

                                }
                            } else
                                Toast.makeText(RegisterActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

    class GetAllCitiesTask extends AsyncTask<String, Integer, Object> {

        @Override
        protected Object doInBackground(String... params) {

            try {
                if (configManager.getString("api.multicities").equals("false")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            municipalityInfo.setVisibility(View.GONE);
                            spinner.setVisibility(View.GONE);
                            autoCompleteTextView.setVisibility(View.GONE);
                            nameInputLayout.setBackgroundResource(R.drawable.top_edittext);
                        }
                    });
                } else {
                    final List<City> cityList = ApiController.getAllCitiesURL(configManager.getString("api.multipleCitiesUrl"));
                    final List<String> cities = new ArrayList<>();

                    for (int i = 0; i < cityList.size(); i++) {
                        cities.add(cityList.get(i).getCityName());
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                            dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, RegisterActivity.this));
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    check = check + 1;
                                    if (check > 1) {
                                        url = cityList.get(position - 1).getUrl();
                                        cityName = cityList.get(position - 1).getCityName();
                                        code = cityList.get(position - 1).getCityCode();
                                        autoCompleteTextView.setText(cityList.get(position - 1).getCityName());
                                        autoCompleteTextView.dismissDropDown();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                            autoCompleteTextView.setHint("Municipality");
                            autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                            autoCompleteTextView.setOnClickListener(null);
                            autoCompleteTextView.setAdapter(autoCompleteAdapter);
                            autoCompleteTextView.setThreshold(1);
                            autoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                                @Override
                                public void onClick(DrawablePosition target) {
                                    if (target == DrawablePosition.RIGHT) {
                                        spinner.performClick();
                                    }
                                }
                            });
                            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    String s = autoCompleteTextView.getText().toString();
                                    for (City city : cityList) {
                                        if (s.equals(city.getCityName())) {
                                            url = city.getUrl();
                                            cityName = city.getCityName();
                                            code = city.getCityCode();
                                        }

                                    }
                                }
                            });

                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        autoCompleteTextView.setOnClickListener(null);
                        autoCompleteTextView.setHint("Loading failed");
                        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                        Toast.makeText(RegisterActivity.this, "An unexpected error occurred while retrieving the list of available municipalities.Please exit and return to this screen to attempt to retrieve municipality list", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
    }
}
