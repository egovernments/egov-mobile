package com.egovernments.egov.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.helper.ConfigManager;
import com.egovernments.egov.helper.CustomAutoCompleteTextView;
import com.egovernments.egov.helper.NothingSelectedSpinnerAdapter;
import com.egovernments.egov.models.City;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.SessionManager;
import com.egovernments.egov.network.UpdateService;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The login screen activity
 **/

public class LoginActivity extends Activity {

    private String username;
    private String password;
    private String url;
    private String cityName;

    private ProgressBar progressBar;

    private FloatingActionButton loginButton;
    private com.melnykov.fab.FloatingActionButton loginButtonCompat;
    private TextView forgotLabel;
    private Button signupButton;

    private EditText username_edittext;
    private EditText password_edittext;

    private Handler handler;

    private SessionManager sessionManager;

    private ConfigManager configManager;

    private CustomAutoCompleteTextView autoCompleteTextView;

    private Spinner spinner;

    private int check = 0;

    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        //Checks if session manager believes that the user is logged in.
        if (sessionManager.isLoggedIn()) {
            //If user is logged in and has a stored access token, immediately login user
            if (sessionManager.getAccessToken() != (null)) {
                //Start fetching data from server
                startService(new Intent(LoginActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_ALL));
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, R.string.session_expiry_message, Toast.LENGTH_SHORT).show();
            }
        }

        setContentView(R.layout.activity_login);

        url = sessionManager.getBaseURL();
        cityName = sessionManager.getUrlLocation();
        code = sessionManager.getUrlLocationCode();

        spinner = (Spinner) findViewById(R.id.signin_city);

        loginButton = (FloatingActionButton) findViewById(R.id.signin_submit);
        loginButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.signin_submit_compat);

        forgotLabel = (TextView) findViewById(R.id.signin_forgot);
        signupButton = (Button) findViewById(R.id.signin_register);

        progressBar = (ProgressBar) findViewById(R.id.loginprogressBar);

        username_edittext = (EditText) findViewById(R.id.signin_username);
        password_edittext = (EditText) findViewById(R.id.signin_password);

        autoCompleteTextView = (CustomAutoCompleteTextView) findViewById(R.id.login_spinner_autocomplete);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Fetching municipality list, please wait", Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                username = username_edittext.getText().toString().trim();
                password = password_edittext.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);
                loginButtonCompat.setVisibility(View.GONE);
                forgotLabel.setVisibility(View.INVISIBLE);
                signupButton.setVisibility(View.INVISIBLE);

                submit(username, password);
            }
        };

//        To make fab compatible in older android versions
        if (Build.VERSION.SDK_INT >= 21) {

            loginButton.setOnClickListener(onClickListener);

        } else {

            loginButton.setVisibility(View.GONE);
            loginButtonCompat.setVisibility(View.VISIBLE);
            loginButtonCompat.setOnClickListener(onClickListener);

        }

        forgotLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });

        password_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.GONE);
                    loginButtonCompat.setVisibility(View.GONE);
                    forgotLabel.setVisibility(View.INVISIBLE);
                    signupButton.setVisibility(View.INVISIBLE);

                    username = username_edittext.getText().toString().trim();
                    password = password_edittext.getText().toString().trim();
                    submit(username, password);
                    return true;
                }
                return false;
            }
        });

        handler = new Handler();

        try {
            InputStream inputStream = getAssets().open("egov.conf");
            configManager = new ConfigManager(inputStream, LoginActivity.this);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new GetAllCitiesTask().execute();
    }

    //Invokes call to API
    private void submit(final String username, final String password) {

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.login_field_empty_prompt, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            forgotLabel.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= 21) {
                loginButton.setVisibility(View.VISIBLE);
            } else
                loginButtonCompat.setVisibility(View.VISIBLE);

        } else {

            sessionManager.setBaseURL(url, cityName, code);
            ApiController.getLoginAPI(LoginActivity.this).login(username, "read write", password, "password", new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {

                    //Stores access token in session manager
                    sessionManager.loginUser(password, username, jsonObject.get("access_token").toString());
                    startService(new Intent(LoginActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_ALL));
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                }

                @Override
                public void failure(RetrofitError error) {
                    JsonObject jsonObject = null;
                    if (error != null) {
                        if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().contains("400")) {
                            Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                jsonObject = (JsonObject) error.getBody();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (jsonObject != null) {
                                String errorDescription = jsonObject.get("error_description").toString().trim();
                                //If user has attempted to log into a yet to be activated account,
                                // automatically redirect the user to account activation screen
                                if (errorDescription.contains("Please activate your account")) {
                                    Intent intent = new Intent(LoginActivity.this, AccountActivationActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("password", password);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(LoginActivity.this, errorDescription, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(LoginActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                    forgotLabel.setVisibility(View.VISIBLE);
                    signupButton.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= 21) {
                        loginButton.setVisibility(View.VISIBLE);
                    } else
                        loginButtonCompat.setVisibility(View.VISIBLE);

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

                            username_edittext.setBackgroundResource(R.drawable.top_edittext);
                            spinner.setVisibility(View.GONE);
                            autoCompleteTextView.setVisibility(View.GONE);
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
                            ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                            dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, LoginActivity.this));

                            for (int i = 0; i < cityList.size(); i++) {
                                if (cityList.get(i).getCityCode() == (sessionManager.getUrlLocationCode()))
                                    autoCompleteTextView.setText(cities.get(i));
                            }
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

                            ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
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
                    }
                });
            }

            return null;
        }
    }

}


