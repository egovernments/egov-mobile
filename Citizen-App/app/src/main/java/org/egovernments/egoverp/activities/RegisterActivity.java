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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.helper.CustomAutoCompleteTextView;
import org.egovernments.egoverp.helper.NothingSelectedSpinnerAdapter;
import org.egovernments.egoverp.helper.PasswordLevel;
import org.egovernments.egoverp.listeners.SMSListener;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.District;
import org.egovernments.egoverp.models.RegisterRequest;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.ApiUrl;
import org.egovernments.egoverp.network.SessionManager;
import org.egovernments.egoverp.network.UpdateService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@SuppressWarnings("unchecked")
public class RegisterActivity extends AppCompatActivity {

    private String deviceID;
    private String deviceOS;
    private String deviceType;

    private TextView municipalityInfo;

    private TextInputLayout nameInputLayout;

    private ProgressDialog progressDialog;

    private ConfigManager configManager;

    private Handler handler;

    private Spinner spinnerCity;
    private Spinner spinnerDistrict;

    private SessionManager sessionManager;

    private CustomAutoCompleteTextView cityAutoCompleteTextView;
    private CustomAutoCompleteTextView districtAutoCompleteTextView;

    List<District> districtsList;
    List<City> citiesList;

    boolean isMultiCity=true;

    EditText name_edittext;
    EditText email_edittext;
    EditText phoneno_edittext;
    EditText password_edittext;
    EditText confirmpassword_edittext;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;

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

        spinnerCity = (Spinner) findViewById(R.id.signup_city);
        spinnerDistrict =(Spinner) findViewById(R.id.spinner_district);

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCancelable(false);

        sessionManager = new SessionManager(getApplicationContext());

        municipalityInfo = (TextView) findViewById(R.id.municipality_change_info);
        nameInputLayout = (TextInputLayout) findViewById(R.id.signup_name_inputlayout);

        name_edittext = (EditText) findViewById(R.id.signup_name);
        email_edittext = (EditText) findViewById(R.id.signup_email);
        phoneno_edittext = (EditText) findViewById(R.id.signup_phoneno);
        password_edittext = (EditText) findViewById(R.id.signup_password);
        confirmpassword_edittext = (EditText) findViewById(R.id.signup_confirmpassword);

        cityAutoCompleteTextView = (CustomAutoCompleteTextView) findViewById(R.id.register_spinner_autocomplete);
        districtAutoCompleteTextView =(CustomAutoCompleteTextView)findViewById(R.id.autocomplete_district);

        cityAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(RegisterActivity.this, "Fetching municipality list, please wait", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

        districtAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(RegisterActivity.this, "Fetching districts list, please wait", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceOS = Integer.toString(Build.VERSION.SDK_INT);
        deviceType = "mobile";


        Button register = (Button) findViewById(R.id.signup_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(name_edittext.getText().toString().trim(), email_edittext.getText().toString().trim()
                        , phoneno_edittext.getText().toString().trim(), password_edittext.getText().toString().trim(), confirmpassword_edittext.getText().toString().trim());
            }
        });

        confirmpassword_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit(name_edittext.getText().toString().trim(), email_edittext.getText().toString().trim()
                            , phoneno_edittext.getText().toString().trim(), password_edittext.getText().toString().trim(), confirmpassword_edittext.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });


        password_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && !TextUtils.isEmpty(password_edittext.getText()) && !AppUtils.isValidPassword(password_edittext.getText().toString(), configManager)){
                    showValidationErrorMessage(getPasswordConstraintInformation());
                    password_edittext.setText("");
                }
            }
        });

        handler = new Handler();

        try {
            configManager = AppUtils.getConfigManager(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new GetAllCitiesTask().execute();

        BroadcastReceiver otpReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            if(alertDialog!=null && alertDialog.isShowing()) {
                EditText etOTP=(EditText) alertDialog.findViewById(R.id.etOTP);
                if(etOTP!=null) {
                    etOTP.setText(intent.getStringExtra(SMSListener.PARAM_OTP_CODE));
                    etOTP.setSelection(etOTP.getText().length());
                }
                registerAccount(intent.getStringExtra(SMSListener.PARAM_OTP_CODE));
            }
            }
        };

        LocalBroadcastManager.getInstance(RegisterActivity.this).registerReceiver(otpReceiver,
                new IntentFilter(SMSListener.OTP_LISTENER));

    }

    private boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private String getPasswordConstraintInformation()
    {
        String pwdLevel=configManager.getString("app.passwordLevel");
        if(pwdLevel.equals(PasswordLevel.HIGH))
        {
            return getResources().getString(R.string.password_level_high);
        }
        else if(pwdLevel.equals(PasswordLevel.MEDIUM))
        {
            return getResources().getString(R.string.password_level_medium);
        }
        else
        {
            return getResources().getString(R.string.password_level_low);
        }
    }

    private void submit(final String name, final String email, final String phoneno, final String password, final String confirmpassword) {

        final City selectedCity=getCityByName(cityAutoCompleteTextView.getText().toString());

        if ((selectedCity == null && isMultiCity)) {
            showValidationErrorMessage("Please select the your district and city");
        } else if(TextUtils.isEmpty(name)){
            showValidationErrorMessage("Please enter your name");
        } else if(TextUtils.isEmpty(phoneno)){
            showValidationErrorMessage("Please enter your phone no");
        } else if (phoneno.length() != 10) {
            showValidationErrorMessage("Phone no. must be 10 digits");
        } else if(!TextUtils.isEmpty(email) && !isValidEmail(email)){
            showValidationErrorMessage("Please enter a valid email ID");
        } else if(TextUtils.isEmpty(password)){
            showValidationErrorMessage("Please enter the password");
        } else if (!AppUtils.isValidPassword(password, configManager)) {
            showValidationErrorMessage(getPasswordConstraintInformation());
        } else if (!password.equals(confirmpassword)) {
            showValidationErrorMessage("Passwords do not match");
        } else {

            if(isMultiCity) {
                sessionManager.setBaseURL(selectedCity.getUrl(), selectedCity.getCityName(), selectedCity.getCityCode());
            }
            sendOTPCode();

        }
    }

    class GetAllCitiesTask extends AsyncTask<String, Integer, Object> {

        @Override
        protected Object doInBackground(String... params) {
            loadDropdowns();
            return null;
        }

    }

    public void resetAndRefreshDropdown()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                cityAutoCompleteTextView.setOnClickListener(null);
                cityAutoCompleteTextView.setHint("Loading failed");
                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                            cityAutoCompleteTextView.setDrawableClickListener(null);
                            cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
                            new GetAllCitiesTask().execute();
                        }
                    }
                });
                Toast toast = Toast.makeText(RegisterActivity.this, "An unexpected error occurred while retrieving the list of available municipalities", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                            cityAutoCompleteTextView.setDrawableClickListener(null);
                            cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
                            new GetAllCitiesTask().execute();
                        }
                    }
                });
            }
        });
    }


    public void loadDropdowns()
    {
        try {
            if (configManager.getString("api.multicities").equals("false")) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((LinearLayout)findViewById(R.id.multicityoptions)).setVisibility(View.GONE);
                        municipalityInfo.setVisibility(View.GONE);
                        nameInputLayout.setBackgroundResource(R.drawable.top_edittext);
                        isMultiCity=false;
                    }
                });
            } else {
                loadDistrictDropdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resetAndRefreshDropdown();
        }
    }

    public void loadDistrictDropdown() throws IOException
    {
        districtsList = ApiController.getAllCitiesURLs(configManager.getString("api.multipleCitiesUrl"));

        if (districtsList != null) {

            final List<String> districts = new ArrayList<>();

            for (int i = 0; i < districtsList.size(); i++) {
                districts.add(districtsList.get(i).getDistrictName());
            }

            loadDropdownsWithData(districts, spinnerDistrict, districtAutoCompleteTextView, true);

        } else {
            resetAndRefreshDropdown();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadCityDropdown()
    {
        if(citiesList!=null){ citiesList.clear(); }

        cityAutoCompleteTextView.setHint("Loading");
        cityAutoCompleteTextView.setOnClickListener(null);
        cityAutoCompleteTextView.setAdapter(null);

        spinnerCity.setAdapter(null);

        String s = districtAutoCompleteTextView.getText().toString().toUpperCase();
        List<String> cities=new ArrayList<>();

        for (District district : districtsList) {
            if (s.equals(district.getDistrictName().toUpperCase())) {
                districtAutoCompleteTextView.setText(s.toUpperCase());
                cityAutoCompleteTextView.requestFocus();
                //noinspection unchecked
                citiesList= (List<City>) ((ArrayList<City>)district.getCities()).clone();
                for(City city: citiesList)
                {
                    cities.add(city.getCityName());
                }
                break;
            }
        }

        loadDropdownsWithData(cities, spinnerCity, cityAutoCompleteTextView, false);
    }

    public void loadDropdownsWithData(final List<String> autocompleteList, final Spinner autoCompleteSpinner, final CustomAutoCompleteTextView autocompleteTextBox, final Boolean isDistrict)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, autocompleteList);
                dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoCompleteSpinner.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, RegisterActivity.this));
                autoCompleteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if ((position - 1) > -1) {
                            if (isDistrict) {
                                autocompleteTextBox.setText(districtsList.get(position - 1).getDistrictName());
                                loadCityDropdown();
                            } else {
                                City selectedCity = citiesList.get(position - 1);
                                autocompleteTextBox.setText(selectedCity.getCityName());
                                name_edittext.requestFocus();
                            }
                        }
                        autocompleteTextBox.dismissDropDown();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, autocompleteList);
                //String hintText = (autocompleteList.size() > 0 ? (isDistrict ? "District" : "Municipality") : (isDistrict ? "Districts Not Found!" : "Municipalities Not Found!"));

                autocompleteTextBox.setHint((isDistrict ? "District" : "Municipality"));
                autocompleteTextBox.setCompoundDrawablesWithIntrinsicBounds((isDistrict ? R.drawable.ic_place_black_24dp : R.drawable.ic_location_city_black_24dp), 0, (autocompleteList.size() > 0 ? R.drawable.ic_keyboard_arrow_down_black_24dp : 0), 0);
                autocompleteTextBox.setOnClickListener(null);
                autocompleteTextBox.setAdapter(autoCompleteAdapter);
                autocompleteTextBox.setThreshold(1);
                autocompleteTextBox.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            autoCompleteSpinner.performClick();
                        }
                    }
                });

                autocompleteTextBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (isDistrict) {
                            if (citiesList != null) {
                                citiesList.clear();
                            }
                            cityAutoCompleteTextView.setText("");
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                autocompleteTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(final View v, boolean hasFocus) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!v.hasFocus()) {
                                    if (isDistrict && !TextUtils.isEmpty(districtAutoCompleteTextView.getText().toString())) {
                                        loadCityDropdown();
                                    }
                                }
                            }
                        });

                    }
                });

                autocompleteTextBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (isDistrict) {
                            loadCityDropdown();
                        }
                    }
                });

            }
        });
    }


    public City getCityByName(String cityName)
    {
        if(citiesList!=null) {
            for (City city : citiesList) {
                if (cityName.equals(city.getCityName())) {
                    return city;
                }
            }
        }
        return null;
    }

    public void showValidationErrorMessage(String message)
    {
        Toast toast = Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        progressDialog.dismiss();
    }

    public void showOTPVerificationDialog()
    {
        if(alertDialog!=null && alertDialog.isShowing())
        {
            alertDialog.dismiss();
        }

        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_verify_otp, null);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        final EditText etOTP=(EditText)dialogView.findViewById(R.id.etOTP);

        dialogBuilder.setPositiveButton("SIGN UP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!TextUtils.isEmpty(etOTP.getText()))
                    registerAccount(etOTP.getText().toString());
                else
                  Toast.makeText(RegisterActivity.this,"Please enter OTP code",Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setNeutralButton("GENERATE OTP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendOTPCode();
            }
        });

        alertDialog=dialogBuilder.create();
        alertDialog.show();
    }


    public void sendOTPCode()
    {

        progressDialog.show();

        ApiController.resetAndGetAPI(getApplicationContext()).sendOTP(phoneno_edittext.getText().toString(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                progressDialog.dismiss();
                showOTPVerificationDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                if(!TextUtils.isEmpty(error.getLocalizedMessage())){
                    Toast toast = Toast.makeText(RegisterActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionManager.setOTPLocalBroadCastRunning(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionManager.setOTPLocalBroadCastRunning(false);
    }

    public void registerAccount(final String otpCode)
    {
        progressDialog.show();

        RegisterRequest registerRequest = new RegisterRequest(email_edittext.getText().toString(), phoneno_edittext.getText().toString(),
                name_edittext.getText().toString(), password_edittext.getText().toString(), deviceID, deviceType, deviceOS, otpCode);

        ApiController.getAPI(getApplicationContext()).registerUser(registerRequest, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();
                        alertDialog.dismiss();
                        citizenLogin(phoneno_edittext.getText().toString(), password_edittext.getText().toString());
                        Toast toast = Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                });
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
                                Toast toast = Toast.makeText(RegisterActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            } else {
                                try {
                                    jsonObject = (JsonObject) error.getBody();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (jsonObject != null) {
                                    Toast toast = Toast.makeText(RegisterActivity.this, "An account already exists with that email ID or mobile no.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(RegisterActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                }

                            }
                        } else {
                            Toast toast = Toast.makeText(RegisterActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }

                        if(alertDialog.isShowing())
                        alertDialog.dismiss();

                    }
                });
            }
        });
    }


    public void citizenLogin(final String username, final String password)
    {

        progressDialog.show();

        ApiController.getAPI(getApplicationContext()).login(ApiUrl.AUTHORIZATION,username, "read write", password, "password", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                sessionManager.loginUser(username, password, AppUtils.getNullAsEmptyString(jsonObject.get("name")),
                        AppUtils.getNullAsEmptyString(jsonObject.get("mobileNumber")), AppUtils.getNullAsEmptyString(jsonObject.get("emailId")) ,
                        jsonObject.get("access_token").getAsString(), jsonObject.get("cityLat").getAsDouble(), jsonObject.get("cityLng").getAsDouble());

                startService(new Intent(RegisterActivity.this, UpdateService.class)
                        .putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_ALL));

                Intent openHomeScreen=new Intent(RegisterActivity.this, HomeActivity.class);
                openHomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(openHomeScreen);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {

                progressDialog.dismiss();

                Toast toast = Toast.makeText(RegisterActivity.this, "An error occurred while logging in", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

}


